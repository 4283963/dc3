package com.dc3.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc3.common.context.TenantContext;
import com.dc3.common.exception.BusinessException;
import com.dc3.domain.dto.WarehouseRuleDTO;
import com.dc3.domain.entity.WarehouseReconciliationRule;
import com.dc3.domain.vo.WarehouseRuleVO;
import com.dc3.repository.mapper.WarehouseReconciliationRuleMapper;
import com.dc3.service.WarehouseRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseRuleServiceImpl implements WarehouseRuleService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseRuleServiceImpl.class);

    @Autowired
    private WarehouseReconciliationRuleMapper ruleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseRuleVO saveOrUpdate(WarehouseRuleDTO dto) {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new BusinessException(401, "租户身份丢失");
        }
        if (dto == null || StrUtil.isBlank(dto.getWarehouseCode())) {
            throw new BusinessException("仓库编码不能为空");
        }
        if (dto.getExchangeRate() != null && dto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("汇率必须大于0");
        }
        if (dto.getLossExemptQuantity() != null && dto.getLossExemptQuantity() < 0) {
            throw new BusinessException("损耗豁免件数不能为负数");
        }
        if (dto.getLossExemptAmount() != null && dto.getLossExemptAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("损耗豁免金额不能为负数");
        }

        LambdaQueryWrapper<WarehouseReconciliationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseReconciliationRule::getTenantId, tenantId)
                .eq(WarehouseReconciliationRule::getWarehouseCode, dto.getWarehouseCode());
        WarehouseReconciliationRule exist = ruleMapper.selectOne(wrapper);

        WarehouseReconciliationRule rule;
        if (exist != null) {
            rule = exist;
            if (StrUtil.isNotBlank(dto.getCurrency())) {
                rule.setCurrency(dto.getCurrency());
            }
            if (dto.getExchangeRate() != null) {
                rule.setExchangeRate(dto.getExchangeRate());
            }
            if (dto.getLossExemptQuantity() != null) {
                rule.setLossExemptQuantity(dto.getLossExemptQuantity());
            }
            if (dto.getLossExemptAmount() != null) {
                rule.setLossExemptAmount(dto.getLossExemptAmount());
            }
            if (dto.getIsExemptEnabled() != null) {
                rule.setIsExemptEnabled(dto.getIsExemptEnabled());
            }
            if (dto.getRemark() != null) {
                rule.setRemark(dto.getRemark());
            }
            ruleMapper.updateById(rule);
            log.info("更新仓库对账规则: tenantId={}, warehouseCode={}", tenantId, dto.getWarehouseCode());
        } else {
            rule = new WarehouseReconciliationRule();
            rule.setWarehouseCode(dto.getWarehouseCode());
            rule.setCurrency(StrUtil.isNotBlank(dto.getCurrency()) ? dto.getCurrency() : "CNY");
            rule.setExchangeRate(dto.getExchangeRate() != null ? dto.getExchangeRate() : BigDecimal.ONE);
            rule.setLossExemptQuantity(dto.getLossExemptQuantity() != null ? dto.getLossExemptQuantity() : 0);
            rule.setLossExemptAmount(dto.getLossExemptAmount() != null ? dto.getLossExemptAmount() : BigDecimal.ZERO);
            rule.setIsExemptEnabled(dto.getIsExemptEnabled() != null ? dto.getIsExemptEnabled() : 1);
            rule.setRemark(dto.getRemark());
            ruleMapper.insert(rule);
            log.info("新增仓库对账规则: tenantId={}, warehouseCode={}", tenantId, dto.getWarehouseCode());
        }

        return toVO(rule);
    }

    @Override
    public WarehouseRuleVO getByWarehouseCode(String warehouseCode) {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new BusinessException(401, "租户身份丢失");
        }
        if (StrUtil.isBlank(warehouseCode)) {
            throw new BusinessException("仓库编码不能为空");
        }
        LambdaQueryWrapper<WarehouseReconciliationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseReconciliationRule::getTenantId, tenantId)
                .eq(WarehouseReconciliationRule::getWarehouseCode, warehouseCode);
        WarehouseReconciliationRule rule = ruleMapper.selectOne(wrapper);
        return rule != null ? toVO(rule) : null;
    }

    @Override
    public List<WarehouseRuleVO> listAll() {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new BusinessException(401, "租户身份丢失");
        }
        LambdaQueryWrapper<WarehouseReconciliationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseReconciliationRule::getTenantId, tenantId)
                .orderByDesc(WarehouseReconciliationRule::getCreateTime);
        List<WarehouseReconciliationRule> rules = ruleMapper.selectList(wrapper);
        return rules.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByWarehouseCode(String warehouseCode) {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new BusinessException(401, "租户身份丢失");
        }
        if (StrUtil.isBlank(warehouseCode)) {
            throw new BusinessException("仓库编码不能为空");
        }
        LambdaQueryWrapper<WarehouseReconciliationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseReconciliationRule::getTenantId, tenantId)
                .eq(WarehouseReconciliationRule::getWarehouseCode, warehouseCode);
        ruleMapper.delete(wrapper);
        log.info("删除仓库对账规则: tenantId={}, warehouseCode={}", tenantId, warehouseCode);
    }

    private WarehouseRuleVO toVO(WarehouseReconciliationRule rule) {
        if (rule == null) {
            return null;
        }
        WarehouseRuleVO vo = new WarehouseRuleVO();
        vo.setId(rule.getId());
        vo.setWarehouseCode(rule.getWarehouseCode());
        vo.setCurrency(rule.getCurrency());
        vo.setExchangeRate(rule.getExchangeRate());
        vo.setLossExemptQuantity(rule.getLossExemptQuantity());
        vo.setLossExemptAmount(rule.getLossExemptAmount());
        vo.setIsExemptEnabled(rule.getIsExemptEnabled());
        vo.setRemark(rule.getRemark());
        vo.setCreateTime(rule.getCreateTime());
        vo.setUpdateTime(rule.getUpdateTime());
        return vo;
    }
}
