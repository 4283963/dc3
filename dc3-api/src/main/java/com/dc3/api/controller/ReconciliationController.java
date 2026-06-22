package com.dc3.api.controller;

import cn.hutool.core.util.StrUtil;
import com.dc3.common.context.TenantContext;
import com.dc3.common.exception.BusinessException;
import com.dc3.common.model.Result;
import com.dc3.domain.dto.ReconciliationQueryDTO;
import com.dc3.domain.vo.ReconciliationSummaryVO;
import com.dc3.service.ReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reconciliation")
public class ReconciliationController {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationController.class);

    @Autowired
    private ReconciliationService reconciliationService;

    private void enforceTenant(String headerTenantId, ReconciliationQueryDTO query) {
        String contextTenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(contextTenantId)) {
            throw new BusinessException(401, "租户身份丢失，请重新登录");
        }
        if (StrUtil.isNotBlank(headerTenantId) && !contextTenantId.equals(headerTenantId)) {
            log.warn("Header 租户与上下文不一致：header={}, context={}", headerTenantId, contextTenantId);
            throw new BusinessException(403, "租户身份校验失败");
        }
        if (query != null && StrUtil.isNotBlank(query.getTenantId())
                && !contextTenantId.equals(query.getTenantId())) {
            log.warn("检测到越权尝试：上下文租户={}，请求体租户={}", contextTenantId, query.getTenantId());
            throw new BusinessException(403, "无权访问其他租户数据");
        }
        if (query != null) {
            query.setTenantId(contextTenantId);
        }
    }

    @GetMapping("/summary")
    public Result<ReconciliationSummaryVO> getSummary(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        ReconciliationQueryDTO query = new ReconciliationQueryDTO();
        query.setWarehouseCode(warehouseCode);
        query.setSkuCode(skuCode);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        enforceTenant(tenantId, query);

        return Result.success(reconciliationService.getSummary(query));
    }

    @PostMapping("/summary")
    public Result<ReconciliationSummaryVO> getSummaryByPost(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody ReconciliationQueryDTO query) {
        enforceTenant(tenantId, query);
        return Result.success(reconciliationService.getSummary(query));
    }

    @GetMapping("/details")
    public Result<?> getDifferenceDetails(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String skuCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        ReconciliationQueryDTO query = new ReconciliationQueryDTO();
        query.setWarehouseCode(warehouseCode);
        query.setSkuCode(skuCode);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        enforceTenant(tenantId, query);

        return Result.success(reconciliationService.getDifferenceDetails(query));
    }

    @PostMapping("/details")
    public Result<?> getDifferenceDetailsByPost(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody ReconciliationQueryDTO query) {
        enforceTenant(tenantId, query);
        return Result.success(reconciliationService.getDifferenceDetails(query));
    }
}
