package com.dc3.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ReconciliationSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String startTime;
    private String endTime;
    private Integer totalSkus;
    private Integer totalBookQuantity;
    private Integer totalPhysicalQuantity;
    private Integer totalDifferenceQuantity;
    private Integer missingShipmentCount;
    private Integer wrongShipmentCount;
    private Integer lossCount;
    private Integer overageCount;
    private Integer exemptLossCount;
    private Integer actualLossCount;
    private BigDecimal totalDifferenceAmount;
    private BigDecimal totalDifferenceAmountCny;
    private BigDecimal exemptAmountCny;
    private BigDecimal actualChargeAmountCny;
    private String baseCurrency;
    private List<SkuDifferenceVO> skuDifferences;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getTotalSkus() { return totalSkus; }
    public void setTotalSkus(Integer totalSkus) { this.totalSkus = totalSkus; }
    public Integer getTotalBookQuantity() { return totalBookQuantity; }
    public void setTotalBookQuantity(Integer totalBookQuantity) { this.totalBookQuantity = totalBookQuantity; }
    public Integer getTotalPhysicalQuantity() { return totalPhysicalQuantity; }
    public void setTotalPhysicalQuantity(Integer totalPhysicalQuantity) { this.totalPhysicalQuantity = totalPhysicalQuantity; }
    public Integer getTotalDifferenceQuantity() { return totalDifferenceQuantity; }
    public void setTotalDifferenceQuantity(Integer totalDifferenceQuantity) { this.totalDifferenceQuantity = totalDifferenceQuantity; }
    public Integer getMissingShipmentCount() { return missingShipmentCount; }
    public void setMissingShipmentCount(Integer missingShipmentCount) { this.missingShipmentCount = missingShipmentCount; }
    public Integer getWrongShipmentCount() { return wrongShipmentCount; }
    public void setWrongShipmentCount(Integer wrongShipmentCount) { this.wrongShipmentCount = wrongShipmentCount; }
    public Integer getLossCount() { return lossCount; }
    public void setLossCount(Integer lossCount) { this.lossCount = lossCount; }
    public Integer getOverageCount() { return overageCount; }
    public void setOverageCount(Integer overageCount) { this.overageCount = overageCount; }
    public Integer getExemptLossCount() { return exemptLossCount; }
    public void setExemptLossCount(Integer exemptLossCount) { this.exemptLossCount = exemptLossCount; }
    public Integer getActualLossCount() { return actualLossCount; }
    public void setActualLossCount(Integer actualLossCount) { this.actualLossCount = actualLossCount; }
    public BigDecimal getTotalDifferenceAmount() { return totalDifferenceAmount; }
    public void setTotalDifferenceAmount(BigDecimal totalDifferenceAmount) { this.totalDifferenceAmount = totalDifferenceAmount; }
    public BigDecimal getTotalDifferenceAmountCny() { return totalDifferenceAmountCny; }
    public void setTotalDifferenceAmountCny(BigDecimal totalDifferenceAmountCny) { this.totalDifferenceAmountCny = totalDifferenceAmountCny; }
    public BigDecimal getExemptAmountCny() { return exemptAmountCny; }
    public void setExemptAmountCny(BigDecimal exemptAmountCny) { this.exemptAmountCny = exemptAmountCny; }
    public BigDecimal getActualChargeAmountCny() { return actualChargeAmountCny; }
    public void setActualChargeAmountCny(BigDecimal actualChargeAmountCny) { this.actualChargeAmountCny = actualChargeAmountCny; }
    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public List<SkuDifferenceVO> getSkuDifferences() { return skuDifferences; }
    public void setSkuDifferences(List<SkuDifferenceVO> skuDifferences) { this.skuDifferences = skuDifferences; }

    @Override
    public String toString() {
        return "ReconciliationSummaryVO{" +
                "tenantId='" + tenantId + '\'' +
                ", totalSkus=" + totalSkus +
                ", totalDifferenceAmountCny=" + totalDifferenceAmountCny +
                ", exemptAmountCny=" + exemptAmountCny +
                ", actualChargeAmountCny=" + actualChargeAmountCny +
                '}';
    }
}
