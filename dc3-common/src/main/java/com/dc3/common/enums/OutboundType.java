package com.dc3.common.enums;

public enum OutboundType {

    SALES(1, "销售出库"),
    TRANSFER(2, "调拨出库"),
    RETURN(3, "退货出库"),
    SCRAP(4, "报废出库");

    private final Integer code;
    private final String desc;

    OutboundType(Integer code, String desc) {
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
