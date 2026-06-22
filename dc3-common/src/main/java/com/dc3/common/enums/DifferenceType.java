package com.dc3.common.enums;

public enum DifferenceType {

    MISSING_SHIPMENT(1, "漏发"),
    WRONG_SHIPMENT(2, "错发"),
    LOSS(3, "损耗"),
    OVERAGE(4, "盈余");

    private final Integer code;
    private final String desc;

    DifferenceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
