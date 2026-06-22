package com.dc3.common.enums;

public enum InboundType {

    PURCHASE(1, "采购入库"),
    RETURN(2, "退货入库"),
    TRANSFER(3, "调拨入库"),
    OTHER(4, "其他入库");

    private final Integer code;
    private final String desc;

    InboundType(Integer code, String desc) {
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
