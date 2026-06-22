package com.dc3.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WarehouseInboundMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tenantId;

    private String warehouseCode;

    private String inboundNo;

    private Integer inboundType;

    private String referenceNo;

    private String operator;

    private LocalDateTime inboundTime;

    private List<InboundItem> items;

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

    public String getInboundNo() {
        return inboundNo;
    }

    public void setInboundNo(String inboundNo) {
        this.inboundNo = inboundNo;
    }

    public Integer getInboundType() {
        return inboundType;
    }

    public void setInboundType(Integer inboundType) {
        this.inboundType = inboundType;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getInboundTime() {
        return inboundTime;
    }

    public void setInboundTime(LocalDateTime inboundTime) {
        this.inboundTime = inboundTime;
    }

    public List<InboundItem> getItems() {
        return items;
    }

    public void setItems(List<InboundItem> items) {
        this.items = items;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "WarehouseInboundMessage{" +
                "tenantId='" + tenantId + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", inboundNo='" + inboundNo + '\'' +
                ", inboundType=" + inboundType +
                ", referenceNo='" + referenceNo + '\'' +
                ", operator='" + operator + '\'' +
                ", inboundTime=" + inboundTime +
                ", items=" + items +
                ", remark='" + remark + '\'' +
                '}';
    }

    public static class InboundItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String skuCode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String batchNo;
        private LocalDateTime productionDate;
        private LocalDateTime expiryDate;

        public String getSkuCode() {
            return skuCode;
        }

        public void setSkuCode(String skuCode) {
            this.skuCode = skuCode;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getBatchNo() {
            return batchNo;
        }

        public void setBatchNo(String batchNo) {
            this.batchNo = batchNo;
        }

        public LocalDateTime getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(LocalDateTime productionDate) {
            this.productionDate = productionDate;
        }

        public LocalDateTime getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
        }

        @Override
        public String toString() {
            return "InboundItem{" +
                    "skuCode='" + skuCode + '\'' +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    ", batchNo='" + batchNo + '\'' +
                    ", productionDate=" + productionDate +
                    ", expiryDate=" + expiryDate +
                    '}';
        }
    }
}
