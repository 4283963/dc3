package com.dc3.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc3.domain.entity.InventoryTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InventoryTransactionMapper extends BaseMapper<InventoryTransaction> {

    @Select("SELECT sku_code, warehouse_code, " +
            "SUM(CASE WHEN event_type = 1 THEN quantity WHEN event_type = 2 THEN -quantity ELSE 0 END) AS net_quantity " +
            "FROM inventory_transaction " +
            "WHERE tenant_id = #{tenantId} " +
            "AND event_time BETWEEN #{startTime} AND #{endTime} " +
            "AND is_deleted = 0 " +
            "GROUP BY sku_code, warehouse_code")
    List<NetQuantityResult> calculateNetQuantity(@Param("tenantId") String tenantId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory_transaction " +
            "WHERE tenant_id = #{tenantId} " +
            "AND sku_code = #{skuCode} " +
            "AND warehouse_code = #{warehouseCode} " +
            "AND event_time < #{endTime} " +
            "AND event_type = 1 " +
            "AND is_deleted = 0")
    Integer sumInboundBefore(@Param("tenantId") String tenantId,
                             @Param("skuCode") String skuCode,
                             @Param("warehouseCode") String warehouseCode,
                             @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory_transaction " +
            "WHERE tenant_id = #{tenantId} " +
            "AND sku_code = #{skuCode} " +
            "AND warehouse_code = #{warehouseCode} " +
            "AND event_time < #{endTime} " +
            "AND event_type = 2 " +
            "AND is_deleted = 0")
    Integer sumOutboundBefore(@Param("tenantId") String tenantId,
                              @Param("skuCode") String skuCode,
                              @Param("warehouseCode") String warehouseCode,
                              @Param("endTime") LocalDateTime endTime);

    class NetQuantityResult {
        private String skuCode;
        private String warehouseCode;
        private Integer netQuantity;

        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
        public String getWarehouseCode() { return warehouseCode; }
        public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
        public Integer getNetQuantity() { return netQuantity; }
        public void setNetQuantity(Integer netQuantity) { this.netQuantity = netQuantity; }
    }
}
