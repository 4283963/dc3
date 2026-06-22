package com.dc3.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc3.domain.entity.StocktakeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StocktakeRecordMapper extends BaseMapper<StocktakeRecord> {

    @Select("SELECT sku_code, warehouse_code, physical_quantity, stocktake_time " +
            "FROM stocktake_record s " +
            "WHERE tenant_id = #{tenantId} " +
            "AND stocktake_time BETWEEN #{startTime} AND #{endTime} " +
            "AND is_deleted = 0 " +
            "AND stocktake_time = (" +
            "   SELECT MAX(stocktake_time) FROM stocktake_record " +
            "   WHERE tenant_id = #{tenantId} " +
            "   AND sku_code = s.sku_code " +
            "   AND warehouse_code = s.warehouse_code " +
            "   AND stocktake_time BETWEEN #{startTime} AND #{endTime} " +
            "   AND is_deleted = 0)")
    List<LatestStocktakeResult> getLatestStocktake(@Param("tenantId") String tenantId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(physical_quantity), 0) " +
            "FROM stocktake_record " +
            "WHERE tenant_id = #{tenantId} " +
            "AND sku_code = #{skuCode} " +
            "AND warehouse_code = #{warehouseCode} " +
            "AND stocktake_time <= #{time} " +
            "AND is_deleted = 0 " +
            "ORDER BY stocktake_time DESC " +
            "LIMIT 1")
    Integer getPhysicalQuantityAt(@Param("tenantId") String tenantId,
                                  @Param("skuCode") String skuCode,
                                  @Param("warehouseCode") String warehouseCode,
                                  @Param("time") LocalDateTime time);

    class LatestStocktakeResult {
        private String skuCode;
        private String warehouseCode;
        private Integer physicalQuantity;
        private LocalDateTime stocktakeTime;

        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
        public String getWarehouseCode() { return warehouseCode; }
        public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
        public Integer getPhysicalQuantity() { return physicalQuantity; }
        public void setPhysicalQuantity(Integer physicalQuantity) { this.physicalQuantity = physicalQuantity; }
        public LocalDateTime getStocktakeTime() { return stocktakeTime; }
        public void setStocktakeTime(LocalDateTime stocktakeTime) { this.stocktakeTime = stocktakeTime; }
    }
}
