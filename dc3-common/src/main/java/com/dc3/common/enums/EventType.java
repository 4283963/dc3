package com.dc3.common.enums;

public enum EventType {

    INBOUND(1, "入库"),
    OUTBOUND(2, "出库"),
    STOCKTAKE(3, "盘点");

    private final Integer code;
    private final String desc;

    EventType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static EventType of(Integer code) {
        for (EventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown event type code: " + code);
    }
}
