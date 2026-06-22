package com.dc3.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc3.common.model.BaseEntity;

@TableName("reconciliation_diff")
public class ReconciliationDiff extends BaseEntity {

    private String reconciliationNo;

    private String warehouseCode;

    private String skuCode;

    private Integer bookQuantity;

    private Integer physicalQuantity;

    private Integer differenceQuantity;

    private Integer differenceType;

    private String relatedTransactionNo;

    private String description;

    private String remark;

    public String getReconciliationNo() {
        return reconciliationNo;
    }

    public void setReconciliationNo(String reconciliationNo) {
        this.reconciliationNo = reconciliationNo;
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

    public Integer getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(Integer bookQuantity) {
        this.bookQuantity = bookQuantity;
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

    public Integer getDifferenceType() {
        return differenceType;
    }

    public void setDifferenceType(Integer differenceType) {
        this.differenceType = differenceType;
    }

    public String getRelatedTransactionNo() {
        return relatedTransactionNo;
    }

    public void setRelatedTransactionNo(String relatedTransactionNo) {
        this.relatedTransactionNo = relatedTransactionNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ReconciliationDiff{" +
                "reconciliationNo='" + reconciliationNo + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", bookQuantity=" + bookQuantity +
                ", physicalQuantity=" + physicalQuantity +
                ", differenceQuantity=" + differenceQuantity +
                ", differenceType=" + differenceType +
                ", relatedTransactionNo='" + relatedTransactionNo + '\'' +
                ", description='" + description + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
