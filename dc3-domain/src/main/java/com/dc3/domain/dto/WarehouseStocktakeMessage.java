package com.dc3.domain.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class WarehouseStocktakeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tenantId;

    private String warehouseCode;

    private String stocktakeNo;

    private String operator;

    private LocalDateTime stocktakeTime;

    private List<StocktakeItem> items;

    private String remark;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getStocktakeNo() {
        return stocktakeNo;
    }

    public void setStocktakeNo(String stocktakeNo) {
        this.stocktakeNo = stocktakeNo;
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

    public List<StocktakeItem> getItems() {
        return items;
    }

    public void setItems(List<StocktakeItem> items) {
        this.items = items;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static class StocktakeItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String skuCode;
        private Integer systemQuantity;
        private Integer physicalQuantity;
        private Integer differenceQuantity;
        private String reason;

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
    }
}
