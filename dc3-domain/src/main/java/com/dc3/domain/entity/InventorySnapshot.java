package com.dc3.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc3.common.model.BaseEntity;

import java.time.LocalDateTime;

@TableName("inventory_snapshot")
public class InventorySnapshot extends BaseEntity {

    private String warehouseCode;

    private String skuCode;

    private Integer bookQuantity;

    private Integer physicalQuantity;

    private LocalDateTime snapshotDate;

    private String remark;

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

    public LocalDateTime getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDateTime snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "InventorySnapshot{" +
                "warehouseCode='" + warehouseCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", bookQuantity=" + bookQuantity +
                ", physicalQuantity=" + physicalQuantity +
                ", snapshotDate=" + snapshotDate +
                ", remark='" + remark + '\'' +
                '}';
    }
}
