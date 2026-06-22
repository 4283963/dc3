package com.dc3.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class SkuDifferenceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String warehouseCode;
    private String skuCode;
    private String productName;
    private Integer bookQuantity;
    private Integer physicalQuantity;
    private Integer differenceQuantity;
    private Integer differenceType;
    private String differenceTypeDesc;
    private String description;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal costPrice;
    private BigDecimal differenceAmount;
    private BigDecimal differenceAmountCny;
    private Integer exemptQuantity;
    private Integer actualChargeQuantity;
    private Boolean isExempted;

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getBookQuantity() { return bookQuantity; }
    public void setBookQuantity(Integer bookQuantity) { this.bookQuantity = bookQuantity; }
    public Integer getPhysicalQuantity() { return physicalQuantity; }
    public void setPhysicalQuantity(Integer physicalQuantity) { this.physicalQuantity = physicalQuantity; }
    public Integer getDifferenceQuantity() { return differenceQuantity; }
    public void setDifferenceQuantity(Integer differenceQuantity) { this.differenceQuantity = differenceQuantity; }
    public Integer getDifferenceType() { return differenceType; }
    public void setDifferenceType(Integer differenceType) { this.differenceType = differenceType; }
    public String getDifferenceTypeDesc() { return differenceTypeDesc; }
    public void setDifferenceTypeDesc(String differenceTypeDesc) { this.differenceTypeDesc = differenceTypeDesc; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getDifferenceAmount() { return differenceAmount; }
    public void setDifferenceAmount(BigDecimal differenceAmount) { this.differenceAmount = differenceAmount; }
    public BigDecimal getDifferenceAmountCny() { return differenceAmountCny; }
    public void setDifferenceAmountCny(BigDecimal differenceAmountCny) { this.differenceAmountCny = differenceAmountCny; }
    public Integer getExemptQuantity() { return exemptQuantity; }
    public void setExemptQuantity(Integer exemptQuantity) { this.exemptQuantity = exemptQuantity; }
    public Integer getActualChargeQuantity() { return actualChargeQuantity; }
    public void setActualChargeQuantity(Integer actualChargeQuantity) { this.actualChargeQuantity = actualChargeQuantity; }
    public Boolean getIsExempted() { return isExempted; }
    public void setIsExempted(Boolean isExempted) { this.isExempted = isExempted; }

    @Override
    public String toString() {
        return "SkuDifferenceVO{" +
                "warehouseCode='" + warehouseCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", productName='" + productName + '\'' +
                ", bookQuantity=" + bookQuantity +
                ", physicalQuantity=" + physicalQuantity +
                ", differenceQuantity=" + differenceQuantity +
                ", differenceType=" + differenceType +
                ", differenceTypeDesc='" + differenceTypeDesc + '\'' +
                ", differenceAmount=" + differenceAmount +
                ", differenceAmountCny=" + differenceAmountCny +
                ", exemptQuantity=" + exemptQuantity +
                ", isExempted=" + isExempted +
                '}';
    }
}
