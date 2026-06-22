package com.dc3.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc3.common.model.BaseEntity;

import java.time.LocalDateTime;

@TableName("stocktake_record")
public class StocktakeRecord extends BaseEntity {

    private String stocktakeNo;

    private String warehouseCode;

    private String skuCode;

    private Integer systemQuantity;

    private Integer physicalQuantity;

    private Integer differenceQuantity;

    private String reason;

    private String operator;

    private LocalDateTime stocktakeTime;

    private String remark;

    public String getStocktakeNo() {
        return stocktakeNo;
    }

    public void setStocktakeNo(String stocktakeNo) {
        this.stocktakeNo = stocktakeNo;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Integer getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(Integer systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    public Integer getPhysicalQuantity() {
        return physicalQuantity;
    }

    public void setPhysicalQuantity(Integer physicalQuantity) {
        this.physicalQuantity = physicalQuantity;
    }

    public Integer getDifferenceQuantity() {
        return differenceQuantity;
    }

    public void setDifferenceQuantity(Integer differenceQuantity) {
        this.differenceQuantity = differenceQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getStocktakeTime() {
        return stocktakeTime;
    }

    public void setStocktakeTime(LocalDateTime stocktakeTime) {
        this.stocktakeTime = stocktakeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "StocktakeRecord{" +
                "stocktakeNo='" + stocktakeNo + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", systemQuantity=" + systemQuantity +
                ", physicalQuantity=" + physicalQuantity +
                ", differenceQuantity=" + differenceQuantity +
                ", reason='" + reason + '\'' +
                ", operator='" + operator + '\'' +
                ", stocktakeTime=" + stocktakeTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
