package com.dc3.api.controller;

import com.dc3.common.model.Result;
import com.dc3.domain.dto.ReconciliationQueryDTO;
import com.dc3.domain.vo.ReconciliationSummaryVO;
import com.dc3.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reconciliation")
public class ReconciliationController {

    @Autowired
    private ReconciliationService reconciliationService;

    @GetMapping("/summary")
    public Result<ReconciliationSummaryVO> getSummary(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        ReconciliationQueryDTO query = new ReconciliationQueryDTO();
        query.setTenantId(tenantId);
        query.setWarehouseCode(warehouseCode);
        query.setSkuCode(skuCode);
        query.setStartTime(startTime);
        query.setEndTime(endTime);

        return Result.success(reconciliationService.getSummary(query));
    }

    @PostMapping("/summary")
    public Result<ReconciliationSummaryVO> getSummaryByPost(@Valid @RequestBody ReconciliationQueryDTO query) {
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
        query.setTenantId(tenantId);
        query.setWarehouseCode(warehouseCode);
        query.setSkuCode(skuCode);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        return Result.success(reconciliationService.getDifferenceDetails(query));
    }

    @PostMapping("/details")
    public Result<?> getDifferenceDetailsByPost(@Valid @RequestBody ReconciliationQueryDTO query) {
        return Result.success(reconciliationService.getDifferenceDetails(query));
    }
}
