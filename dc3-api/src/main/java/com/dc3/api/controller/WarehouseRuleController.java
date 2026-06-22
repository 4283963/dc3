package com.dc3.api.controller;

import com.dc3.common.model.Result;
import com.dc3.domain.dto.WarehouseRuleDTO;
import com.dc3.domain.vo.WarehouseRuleVO;
import com.dc3.service.WarehouseRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/config")
public class WarehouseRuleController {

    private static final Logger log = LoggerFactory.getLogger(WarehouseRuleController.class);

    @Autowired
    private WarehouseRuleService warehouseRuleService;

    @PostMapping("/rule")
    public Result<WarehouseRuleVO> saveOrUpdateRule(@Valid @RequestBody WarehouseRuleDTO dto) {
        return Result.success(warehouseRuleService.saveOrUpdate(dto));
    }

    @GetMapping("/rule/{warehouseCode}")
    public Result<WarehouseRuleVO> getRule(@PathVariable String warehouseCode) {
        return Result.success(warehouseRuleService.getByWarehouseCode(warehouseCode));
    }

    @GetMapping("/rules")
    public Result<List<WarehouseRuleVO>> listRules() {
        return Result.success(warehouseRuleService.listAll());
    }

    @DeleteMapping("/rule/{warehouseCode}")
    public Result<Void> deleteRule(@PathVariable String warehouseCode) {
        warehouseRuleService.deleteByWarehouseCode(warehouseCode);
        return Result.success();
    }
}
