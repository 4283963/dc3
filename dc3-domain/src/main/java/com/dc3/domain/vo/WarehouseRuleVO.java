package com.dc3.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WarehouseRuleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String warehouseCode;
    private String currency;
    private BigDecimal exchangeRate;
    private Integer lossExemptQuantity;
    private BigDecimal lossExemptAmount;
    private Integer isExemptEnabled;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
