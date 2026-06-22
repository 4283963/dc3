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
import com.dc3.domain.entity.Product;
import com.dc3.domain.entity.ReconciliationDiff;
import com.dc3.domain.vo.DifferenceDetailVO;
import com.dc3.domain.vo.ReconciliationSummaryVO;
import com.dc3.domain.vo.SkuDifferenceVO;
import com.dc3.repository.mapper.InventoryTransactionMapper;
import com.dc3.repository.mapper.ProductMapper;
import com.dc3.repository.mapper.ReconciliationDiffMapper;
import com.dc3.repository.mapper.StocktakeRecordMapper;
import com.dc3.service.ReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReconciliationServiceImpl implements ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationServiceImpl.class);

    @Autowired
    private InventoryTransactionMapper transactionMapper;

    @Autowired
    private StocktakeRecordMapper stocktakeRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ReconciliationDiffMapper diffMapper;

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

        Map<String, Integer> bookQuantityMap = calculateBookQuantity(startTime, endTime, query.getWarehouseCode());
        Map<String, Integer> physicalQuantityMap = calculatePhysicalQuantity(startTime, endTime, query.getWarehouseCode());

        Set<String> allSkuCodes = new HashSet<>();
        allSkuCodes.addAll(bookQuantityMap.keySet());
        allSkuCodes.addAll(physicalQuantityMap.keySet());
        if (StrUtil.isNotBlank(query.getSkuCode())) {
            final String filterSku = query.getSkuCode();
            allSkuCodes = allSkuCodes.stream()
                    .filter(sku -> sku.equals(filterSku))
                    .collect(Collectors.toSet());
        }

        Map<String, String> productNameMap = new HashMap<>();
        if (!allSkuCodes.isEmpty()) {
            List<Product> products = productMapper.selectBySkuCodes(tenantId, allSkuCodes);
            productNameMap = products.stream()
                    .collect(Collectors.toMap(Product::getSkuCode, Product::getProductName, (a, b) -> a));
        }

        String reconciliationNo = "REC" + IdUtil.getSnowflakeNextIdStr();
        List<SkuDifferenceVO> skuDifferences = new ArrayList<>();
        int totalBook = 0;
        int totalPhysical = 0;
        int totalDiff = 0;
        int missingCount = 0;
        int wrongCount = 0;
        int lossCount = 0;
        int overageCount = 0;

        List<ReconciliationDiff> diffsToSave = new ArrayList<>();

        for (String sku : allSkuCodes) {
            Integer bookQty = bookQuantityMap.getOrDefault(sku, 0);
            Integer physicalQty = physicalQuantityMap.getOrDefault(sku, 0);
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

            SkuDifferenceVO skuDiff = new SkuDifferenceVO();
            skuDiff.setSkuCode(sku);
            skuDiff.setProductName(productNameMap.getOrDefault(sku, sku));
            skuDiff.setBookQuantity(bookQty);
            skuDiff.setPhysicalQuantity(physicalQty);
            skuDiff.setDifferenceQuantity(difference);
            skuDiff.setDifferenceType(diffType.getCode());
            skuDiff.setDifferenceTypeDesc(diffType.getDesc());
            skuDiff.setDescription(buildDescription(diffType, bookQty, physicalQty, difference));
            skuDifferences.add(skuDiff);

            ReconciliationDiff diff = new ReconciliationDiff();
            diff.setReconciliationNo(reconciliationNo);
            diff.setWarehouseCode(query.getWarehouseCode());
            diff.setSkuCode(sku);
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
        vo.setSkuDifferences(skuDifferences);

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

    private Map<String, Integer> calculateBookQuantity(LocalDateTime startTime, LocalDateTime endTime, String warehouseCode) {
        final String tenantId = TenantContext.getTenantId();
        Map<String, Integer> result = new HashMap<>();

        LambdaQueryWrapper<com.dc3.domain.entity.InventoryTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.dc3.domain.entity.InventoryTransaction::getTenantId, tenantId);
        wrapper.between(com.dc3.domain.entity.InventoryTransaction::getEventTime, startTime, endTime);
        if (StrUtil.isNotBlank(warehouseCode)) {
            wrapper.eq(com.dc3.domain.entity.InventoryTransaction::getWarehouseCode, warehouseCode);
        }
        List<com.dc3.domain.entity.InventoryTransaction> txList = transactionMapper.selectList(wrapper);

        for (com.dc3.domain.entity.InventoryTransaction tx : txList) {
            String key = tx.getSkuCode();
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

    private Map<String, Integer> calculatePhysicalQuantity(LocalDateTime startTime, LocalDateTime endTime, String warehouseCode) {
        final String tenantId = TenantContext.getTenantId();
        Map<String, Integer> result = new HashMap<>();
        List<StocktakeRecordMapper.LatestStocktakeResult> latestStocktakes =
                stocktakeRecordMapper.getLatestStocktake(tenantId, startTime, endTime);

        Map<String, Integer> bookQtyMap = calculateBookQuantity(startTime, endTime, warehouseCode);

        for (StocktakeRecordMapper.LatestStocktakeResult st : latestStocktakes) {
            if (StrUtil.isNotBlank(warehouseCode) && !warehouseCode.equals(st.getWarehouseCode())) {
                continue;
            }
            String key = st.getSkuCode();
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

    private String buildDescription(DifferenceType type, int bookQty, int physicalQty, int difference) {
        switch (type) {
            case MISSING_SHIPMENT:
                return String.format("漏发：账面库存%d件，实物库存0件，系统显示有库存但实物缺失", bookQty);
            case WRONG_SHIPMENT:
                return String.format("错发：账面库存%d件，实物库存%d件，差异%d件，疑似拣货数量错误",
                        bookQty, physicalQty, Math.abs(difference));
            case LOSS:
                return String.format("损耗：账面库存%d件，实物库存%d件，差异%d件，超出合理损耗范围",
                        bookQty, physicalQty, Math.abs(difference));
            case OVERAGE:
                return String.format("盈余：账面库存%d件，实物库存%d件，多出%d件",
                        bookQty, physicalQty, difference);
            default:
                return "未知差异类型";
        }
    }

    private String getDifferenceTypeDesc(Integer code) {
        for (DifferenceType type : DifferenceType.values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return "未知";
    }
}
