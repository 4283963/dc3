package com.dc3.domain.vo;

import java.io.Serializable;

public class SkuDifferenceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String skuCode;

    private String productName;

    private Integer bookQuantity;

    private Integer physicalQuantity;

    private Integer differenceQuantity;

    private Integer differenceType;

    private String differenceTypeDesc;

    private String description;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getDifferenceTypeDesc() {
        return differenceTypeDesc;
    }

    public void setDifferenceTypeDesc(String differenceTypeDesc) {
        this.differenceTypeDesc = differenceTypeDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SkuDifferenceVO{" +
                "skuCode='" + skuCode + '\'' +
                ", productName='" + productName + '\'' +
                ", bookQuantity=" + bookQuantity +
                ", physicalQuantity=" + physicalQuantity +
                ", differenceQuantity=" + differenceQuantity +
                ", differenceType=" + differenceType +
                ", differenceTypeDesc='" + differenceTypeDesc + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
