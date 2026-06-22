package com.dc3.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc3.common.context.TenantContext;
import com.dc3.common.enums.DifferenceType;
import com.dc3.common.enums.EventType;
import com.dc3.common.exception.BusinessException;
import com.dc3.common.model.PageResult;
import com.dc3.domain.dto.ReconciliationQueryDTO;
import com.dc3.domain.entity.InventoryTransaction;
import com.dc3.domain.entity.Product;
import com.dc3.domain.entity.ReconciliationDiff;
import com.dc3.domain.entity.WarehouseReconciliationRule;
import com.dc3.domain.vo.DifferenceDetailVO;
import com.dc3.domain.vo.ReconciliationSummaryVO;
import com.dc3.domain.vo.SkuDifferenceVO;
import com.dc3.repository.mapper.InventoryTransactionMapper;
import com.dc3.repository.mapper.ProductMapper;
import com.dc3.repository.mapper.ReconciliationDiffMapper;
import com.dc3.repository.mapper.StocktakeRecordMapper;
import com.dc3.repository.mapper.WarehouseReconciliationRuleMapper;
import com.dc3.service.ReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReconciliationServiceImpl implements ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationServiceImpl.class);
    private static final String DEFAULT_CURRENCY = "CNY";

    @Autowired
    private InventoryTransactionMapper transactionMapper;

    @Autowired
    private StocktakeRecordMapper stocktakeRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ReconciliationDiffMapper diffMapper;

    @Autowired
    private WarehouseReconciliationRuleMapper ruleMapper;

    private String resolveTenantId(ReconciliationQueryDTO query) {
        String contextTenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(contextTenantId)) {
            throw new BusinessException(401, "租户身份丢失，请重新登录");
        }
        if (query != null && StrUtil.isNotBlank(query.getTenantId())
                && !contextTenantId.equals(query.getTenantId())) {
            log.warn("检测到越权尝试：上下文租户={}，请求参数租户={}", contextTenantId, query.getTenantId());
            throw new BusinessException(403, "无权访问其他租户数据");
        }
        return contextTenantId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReconciliationSummaryVO getSummary(ReconciliationQueryDTO query) {
        final String tenantId = resolveTenantId(query);

        LocalDateTime startTime = query.getStartTime() != null ? query.getStartTime() : LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = query.getEndTime() != null ? query.getEndTime() : LocalDateTime.now();
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        Map<String, WarehouseReconciliationRule> ruleMap = loadWarehouseRules(tenantId);

        Map<String, Integer> bookQuantityMap = calculateBookQuantity(startTime, endTime, query.getWarehouseCode());
        Map<String, Integer> physicalQuantityMap = calculatePhysicalQuantity(startTime, endTime, query.getWarehouseCode());

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(bookQuantityMap.keySet());
        allKeys.addAll(physicalQuantityMap.keySet());

        if (StrUtil.isNotBlank(query.getSkuCode())) {
            final String filterSku = query.getSkuCode();
            allKeys = allKeys.stream()
                    .filter(key -> extractSku(key).equals(filterSku))
                    .collect(Collectors.toSet());
        }

        Set<String> allSkuCodes = allKeys.stream()
                .map(this::extractSku)
                .collect(Collectors.toSet());
        Map<String, Product> productMap = new HashMap<>();
        if (!allSkuCodes.isEmpty()) {
            List<Product> products = productMapper.selectBySkuCodes(tenantId, allSkuCodes);
            productMap = products.stream()
                    .collect(Collectors.toMap(Product::getSkuCode, p -> p, (a, b) -> a));
        }

        String reconciliationNo = "REC" + IdUtil.getSnowflakeNextIdStr();
        List<SkuDifferenceVO> skuDifferences = new ArrayList<>();
        List<ReconciliationDiff> diffsToSave = new ArrayList<>();

        int totalBook = 0;
        int totalPhysical = 0;
        int totalDiff = 0;
        int missingCount = 0;
        int wrongCount = 0;
        int lossCount = 0;
        int overageCount = 0;
        int exemptLossCount = 0;
        int actualLossCount = 0;

        BigDecimal totalDiffAmountCny = BigDecimal.ZERO;
        BigDecimal exemptAmountCny = BigDecimal.ZERO;
        BigDecimal actualChargeAmountCny = BigDecimal.ZERO;

        for (String key : allKeys) {
            String warehouseCode = extractWarehouse(key);
            String skuCode = extractSku(key);

            Integer bookQty = bookQuantityMap.getOrDefault(key, 0);
            Integer physicalQty = physicalQuantityMap.getOrDefault(key, 0);
            int difference = physicalQty - bookQty;

            if (difference == 0) {
                continue;
            }

            totalBook += bookQty;
            totalPhysical += physicalQty;
            totalDiff += Math.abs(difference);

            DifferenceType diffType = determineDifferenceType(difference, bookQty, physicalQty);
            switch (diffType) {
                case MISSING_SHIPMENT:
                    missingCount += Math.abs(difference);
                    break;
                case WRONG_SHIPMENT:
                    wrongCount += Math.abs(difference);
                    break;
                case LOSS:
                    lossCount += Math.abs(difference);
                    break;
                case OVERAGE:
                    overageCount += Math.abs(difference);
                    break;
            }

            WarehouseReconciliationRule rule = ruleMap.get(warehouseCode);
            String currency = rule != null && StrUtil.isNotBlank(rule.getCurrency()) ? rule.getCurrency() : DEFAULT_CURRENCY;
            BigDecimal exchangeRate = rule != null && rule.getExchangeRate() != null ? rule.getExchangeRate() : BigDecimal.ONE;
            boolean exemptEnabled = rule != null && Integer.valueOf(1).equals(rule.getIsExemptEnabled());
            int exemptQtyPerSku = rule != null && rule.getLossExemptQuantity() != null ? rule.getLossExemptQuantity() : 0;

            Product product = productMap.get(skuCode);
            BigDecimal costPrice = product != null && product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
            BigDecimal diffAmount = costPrice.multiply(BigDecimal.valueOf(Math.abs(difference)));
            BigDecimal diffAmountCny = diffAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

            int exemptQuantity = 0;
            int actualChargeQuantity = Math.abs(difference);
            boolean isExempted = false;

            if (exemptEnabled && (diffType == DifferenceType.LOSS || diffType == DifferenceType.MISSING_SHIPMENT || diffType == DifferenceType.WRONG_SHIPMENT)) {
                if (Math.abs(difference) <= exemptQtyPerSku) {
                    exemptQuantity = Math.abs(difference);
                    actualChargeQuantity = 0;
                    isExempted = true;
                    exemptLossCount += Math.abs(difference);
                } else {
                    exemptQuantity = exemptQtyPerSku;
                    actualChargeQuantity = Math.abs(difference) - exemptQtyPerSku;
                    exemptLossCount += exemptQtyPerSku;
                }
                actualLossCount += actualChargeQuantity;
            } else {
                actualLossCount += Math.abs(difference);
            }

            BigDecimal exemptQtyBd = BigDecimal.valueOf(exemptQuantity);
            BigDecimal exemptItemAmountCny = costPrice.multiply(exemptQtyBd).multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal actualChargeQtyBd = BigDecimal.valueOf(actualChargeQuantity);
            BigDecimal actualItemChargeCny = costPrice.multiply(actualChargeQtyBd).multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

            totalDiffAmountCny = totalDiffAmountCny.add(diffAmountCny);
            exemptAmountCny = exemptAmountCny.add(exemptItemAmountCny);
            actualChargeAmountCny = actualChargeAmountCny.add(actualItemChargeCny);

            SkuDifferenceVO skuDiff = new SkuDifferenceVO();
            skuDiff.setWarehouseCode(warehouseCode);
            skuDiff.setSkuCode(skuCode);
            skuDiff.setProductName(product != null ? product.getProductName() : skuCode);
            skuDiff.setBookQuantity(bookQty);
            skuDiff.setPhysicalQuantity(physicalQty);
            skuDiff.setDifferenceQuantity(difference);
            skuDiff.setDifferenceType(diffType.getCode());
            skuDiff.setDifferenceTypeDesc(diffType.getDesc());
            skuDiff.setDescription(buildDescription(diffType, bookQty, physicalQty, difference, exemptQuantity, isExempted));
            skuDiff.setCurrency(currency);
            skuDiff.setExchangeRate(exchangeRate);
            skuDiff.setCostPrice(costPrice);
            skuDiff.setDifferenceAmount(diffAmount);
            skuDiff.setDifferenceAmountCny(diffAmountCny);
            skuDiff.setExemptQuantity(exemptQuantity);
            skuDiff.setActualChargeQuantity(actualChargeQuantity);
            skuDiff.setIsExempted(isExempted);
            skuDifferences.add(skuDiff);

            ReconciliationDiff diff = new ReconciliationDiff();
            diff.setReconciliationNo(reconciliationNo);
            diff.setWarehouseCode(warehouseCode);
            diff.setSkuCode(skuCode);
            diff.setBookQuantity(bookQty);
            diff.setPhysicalQuantity(physicalQty);
            diff.setDifferenceQuantity(difference);
            diff.setDifferenceType(diffType.getCode());
            diff.setDescription(skuDiff.getDescription());
            diffsToSave.add(diff);
        }

        for (ReconciliationDiff diff : diffsToSave) {
            diffMapper.insert(diff);
        }

        skuDifferences.sort(Comparator.comparing(s -> Math.abs(s.getDifferenceQuantity()), Comparator.reverseOrder()));

        ReconciliationSummaryVO vo = new ReconciliationSummaryVO();
        vo.setTenantId(tenantId);
        vo.setStartTime(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        vo.setEndTime(endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        vo.setTotalSkus(skuDifferences.size());
        vo.setTotalBookQuantity(totalBook);
        vo.setTotalPhysicalQuantity(totalPhysical);
        vo.setTotalDifferenceQuantity(totalDiff);
        vo.setMissingShipmentCount(missingCount);
        vo.setWrongShipmentCount(wrongCount);
        vo.setLossCount(lossCount);
        vo.setOverageCount(overageCount);
        vo.setExemptLossCount(exemptLossCount);
        vo.setActualLossCount(actualLossCount);
        vo.setTotalDifferenceAmountCny(totalDiffAmountCny);
        vo.setExemptAmountCny(exemptAmountCny);
        vo.setActualChargeAmountCny(actualChargeAmountCny);
        vo.setBaseCurrency(DEFAULT_CURRENCY);
        vo.setSkuDifferences(skuDifferences);

        log.info("对账汇总生成成功: tenantId={}, skuCount={}, 总差异金额(CNY)={}, 豁免金额(CNY)={}",
                tenantId, skuDifferences.size(), totalDiffAmountCny, exemptAmountCny);
        return vo;
    }

    @Override
    public PageResult<DifferenceDetailVO> getDifferenceDetails(ReconciliationQueryDTO query) {
        final String tenantId = resolveTenantId(query);

        LambdaQueryWrapper<ReconciliationDiff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReconciliationDiff::getTenantId, tenantId);
        if (StrUtil.isNotBlank(query.getWarehouseCode())) {
            wrapper.eq(ReconciliationDiff::getWarehouseCode, query.getWarehouseCode());
        }
        if (StrUtil.isNotBlank(query.getSkuCode())) {
            wrapper.eq(ReconciliationDiff::getSkuCode, query.getSkuCode());
        }
        wrapper.orderByDesc(ReconciliationDiff::getCreateTime);

        int pageNum = query.getPageNum() != null && query.getPageNum() > 0 ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null && query.getPageSize() > 0 ? query.getPageSize() : 20;
        Page<ReconciliationDiff> page = new Page<>(pageNum, pageSize);
        Page<ReconciliationDiff> result = diffMapper.selectPage(page, wrapper);

        Set<String> skuCodes = result.getRecords().stream()
                .map(ReconciliationDiff::getSkuCode)
                .collect(Collectors.toSet());
        Map<String, String> productNameMap = new HashMap<>();
        if (!skuCodes.isEmpty()) {
            List<Product> products = productMapper.selectBySkuCodes(tenantId, skuCodes);
            productNameMap = products.stream()
                    .collect(Collectors.toMap(Product::getSkuCode, Product::getProductName, (a, b) -> a));
        }

        Map<String, String> finalProductNameMap = productNameMap;
        List<DifferenceDetailVO> records = result.getRecords().stream().map(diff -> {
            DifferenceDetailVO vo = new DifferenceDetailVO();
            vo.setReconciliationNo(diff.getReconciliationNo());
            vo.setWarehouseCode(diff.getWarehouseCode());
            vo.setSkuCode(diff.getSkuCode());
            vo.setProductName(finalProductNameMap.getOrDefault(diff.getSkuCode(), diff.getSkuCode()));
            vo.setBookQuantity(diff.getBookQuantity());
            vo.setPhysicalQuantity(diff.getPhysicalQuantity());
            vo.setDifferenceQuantity(diff.getDifferenceQuantity());
            vo.setDifferenceType(diff.getDifferenceType());
            vo.setDifferenceTypeDesc(getDifferenceTypeDesc(diff.getDifferenceType()));
            vo.setRelatedTransactionNo(diff.getRelatedTransactionNo());
            vo.setDescription(diff.getDescription());
            vo.setCreateTime(diff.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), records, pageNum, pageSize);
    }

    private Map<String, WarehouseReconciliationRule> loadWarehouseRules(String tenantId) {
        if (StrUtil.isBlank(TenantContext.getTenantId())) {
            TenantContext.setTenantId(tenantId);
        }
        LambdaQueryWrapper<WarehouseReconciliationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseReconciliationRule::getTenantId, tenantId);
        List<WarehouseReconciliationRule> rules = ruleMapper.selectList(wrapper);
        Map<String, WarehouseReconciliationRule> map = new HashMap<>();
        for (WarehouseReconciliationRule rule : rules) {
            map.put(rule.getWarehouseCode(), rule);
        }
        return map;
    }

    private Map<String, Integer> calculateBookQuantity(LocalDateTime startTime, LocalDateTime endTime, String filterWarehouse) {
        final String tenantId = TenantContext.getTenantId();
        Map<String, Integer> result = new HashMap<>();

        LambdaQueryWrapper<InventoryTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryTransaction::getTenantId, tenantId);
        wrapper.between(InventoryTransaction::getEventTime, startTime, endTime);
        if (StrUtil.isNotBlank(filterWarehouse)) {
            wrapper.eq(InventoryTransaction::getWarehouseCode, filterWarehouse);
        }
        List<InventoryTransaction> txList = transactionMapper.selectList(wrapper);

        for (InventoryTransaction tx : txList) {
            String key = buildKey(tx.getWarehouseCode(), tx.getSkuCode());
            int current = result.getOrDefault(key, 0);
            if (EventType.INBOUND.getCode().equals(tx.getEventType())) {
                current += tx.getQuantity();
            } else if (EventType.OUTBOUND.getCode().equals(tx.getEventType())) {
                current -= tx.getQuantity();
            } else if (EventType.STOCKTAKE.getCode().equals(tx.getEventType())) {
                current += tx.getQuantity() != null ? tx.getQuantity() : 0;
            }
            result.put(key, current);
        }
        return result;
    }

    private Map<String, Integer> calculatePhysicalQuantity(LocalDateTime startTime, LocalDateTime endTime, String filterWarehouse) {
        final String tenantId = TenantContext.getTenantId();
        Map<String, Integer> result = new HashMap<>();
        List<StocktakeRecordMapper.LatestStocktakeResult> latestStocktakes =
                stocktakeRecordMapper.getLatestStocktake(tenantId, startTime, endTime);

        Map<String, Integer> bookQtyMap = calculateBookQuantity(startTime, endTime, filterWarehouse);

        for (StocktakeRecordMapper.LatestStocktakeResult st : latestStocktakes) {
            if (StrUtil.isNotBlank(filterWarehouse) && !filterWarehouse.equals(st.getWarehouseCode())) {
                continue;
            }
            String key = buildKey(st.getWarehouseCode(), st.getSkuCode());
            result.merge(key, st.getPhysicalQuantity(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : bookQtyMap.entrySet()) {
            result.putIfAbsent(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private DifferenceType determineDifferenceType(int difference, int bookQty, int physicalQty) {
        if (difference < 0) {
            if (bookQty > 0 && physicalQty == 0) {
                return DifferenceType.MISSING_SHIPMENT;
            } else if (Math.abs(difference) <= Math.max(1, (int) (bookQty * 0.1))) {
                return DifferenceType.WRONG_SHIPMENT;
            } else {
                return DifferenceType.LOSS;
            }
        } else {
            return DifferenceType.OVERAGE;
        }
    }

    private String buildDescription(DifferenceType type, int bookQty, int physicalQty, int difference,
                                    int exemptQty, boolean isExempted) {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case MISSING_SHIPMENT:
                sb.append(String.format("漏发：账面库存%d件，实物库存0件，系统显示有库存但实物缺失", bookQty));
                break;
            case WRONG_SHIPMENT:
                sb.append(String.format("错发：账面库存%d件，实物库存%d件，差异%d件，疑似拣货数量错误",
                        bookQty, physicalQty, Math.abs(difference)));
                break;
            case LOSS:
                sb.append(String.format("损耗：账面库存%d件，实物库存%d件，差异%d件，超出合理损耗范围",
                        bookQty, physicalQty, Math.abs(difference)));
                break;
            case OVERAGE:
                sb.append(String.format("盈余：账面库存%d件，实物库存%d件，多出%d件",
                        bookQty, physicalQty, difference));
                break;
            default:
                sb.append("未知差异类型");
        }
        if (exemptQty > 0) {
            sb.append(String.format("，已豁免%d件", exemptQty));
            if (isExempted) {
                sb.append("（全额豁免，不计入账单）");
            }
        }
        return sb.toString();
    }

    private String getDifferenceTypeDesc(Integer code) {
        for (DifferenceType type : DifferenceType.values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return "未知";
    }

    private String buildKey(String warehouseCode, String skuCode) {
        return warehouseCode + "|" + skuCode;
    }

    private String extractWarehouse(String key) {
        int idx = key.indexOf('|');
        return idx > 0 ? key.substring(0, idx) : "";
    }

    private String extractSku(String key) {
        int idx = key.indexOf('|');
        return idx > 0 ? key.substring(idx + 1) : key;
    }
}
