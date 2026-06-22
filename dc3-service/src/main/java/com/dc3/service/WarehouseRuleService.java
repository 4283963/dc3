package com.dc3.service;

import com.dc3.domain.dto.WarehouseRuleDTO;
import com.dc3.domain.vo.WarehouseRuleVO;

import java.util.List;

public interface WarehouseRuleService {

    WarehouseRuleVO saveOrUpdate(WarehouseRuleDTO dto);

    WarehouseRuleVO getByWarehouseCode(String warehouseCode);

    List<WarehouseRuleVO> listAll();

    void deleteByWarehouseCode(String warehouseCode);
}
