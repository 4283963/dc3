package com.dc3.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc3.common.model.BaseEntity;

import java.math.BigDecimal;

@TableName("warehouse_reconciliation_rule")
public class WarehouseReconciliationRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String warehouseCode;

    private String currency;

    private BigDecimal exchangeRate;

    private Integer lossExemptQuantity;

    private BigDecimal lossExemptAmount;

    private Integer isExemptEnabled;

    private String remark;

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public Integer getLossExemptQuantity() { return lossExemptQuantity; }
    public void setLossExemptQuantity(Integer lossExemptQuantity) { this.lossExemptQuantity = lossExemptQuantity; }
    public BigDecimal getLossExemptAmount() { return lossExemptAmount; }
    public void setLossExemptAmount(BigDecimal lossExemptAmount) { this.lossExemptAmount = lossExemptAmount; }
    public Integer getIsExemptEnabled() { return isExemptEnabled; }
    public void setIsExemptEnabled(Integer isExemptEnabled) { this.isExemptEnabled = isExemptEnabled; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
