package com.dc3.domain.vo;

import java.io.Serializable;
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

    private List<SkuDifferenceVO> skuDifferences;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalSkus() {
        return totalSkus;
    }

    public void setTotalSkus(Integer totalSkus) {
        this.totalSkus = totalSkus;
    }

    public Integer getTotalBookQuantity() {
        return totalBookQuantity;
    }

    public void setTotalBookQuantity(Integer totalBookQuantity) {
        this.totalBookQuantity = totalBookQuantity;
    }

    public Integer getTotalPhysicalQuantity() {
        return totalPhysicalQuantity;
    }

    public void setTotalPhysicalQuantity(Integer totalPhysicalQuantity) {
        this.totalPhysicalQuantity = totalPhysicalQuantity;
    }

    public Integer getTotalDifferenceQuantity() {
        return totalDifferenceQuantity;
    }

    public void setTotalDifferenceQuantity(Integer totalDifferenceQuantity) {
        this.totalDifferenceQuantity = totalDifferenceQuantity;
    }

    public Integer getMissingShipmentCount() {
        return missingShipmentCount;
    }

    public void setMissingShipmentCount(Integer missingShipmentCount) {
        this.missingShipmentCount = missingShipmentCount;
    }

    public Integer getWrongShipmentCount() {
        return wrongShipmentCount;
    }

    public void setWrongShipmentCount(Integer wrongShipmentCount) {
        this.wrongShipmentCount = wrongShipmentCount;
    }

    public Integer getLossCount() {
        return lossCount;
    }

    public void setLossCount(Integer lossCount) {
        this.lossCount = lossCount;
    }

    public Integer getOverageCount() {
        return overageCount;
    }

    public void setOverageCount(Integer overageCount) {
        this.overageCount = overageCount;
    }

    public List<SkuDifferenceVO> getSkuDifferences() {
        return skuDifferences;
    }

    public void setSkuDifferences(List<SkuDifferenceVO> skuDifferences) {
        this.skuDifferences = skuDifferences;
    }

    @Override
    public String toString() {
        return "ReconciliationSummaryVO{" +
                "tenantId='" + tenantId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalSkus=" + totalSkus +
                ", totalBookQuantity=" + totalBookQuantity +
                ", totalPhysicalQuantity=" + totalPhysicalQuantity +
                ", totalDifferenceQuantity=" + totalDifferenceQuantity +
                ", missingShipmentCount=" + missingShipmentCount +
                ", wrongShipmentCount=" + wrongShipmentCount +
                ", lossCount=" + lossCount +
                ", overageCount=" + overageCount +
                ", skuDifferences=" + skuDifferences +
                '}';
    }
}
