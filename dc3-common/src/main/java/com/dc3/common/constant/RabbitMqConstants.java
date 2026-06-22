package com.dc3.common.constant;

public final class RabbitMqConstants {

    public static final String WAREHOUSE_EVENT_EXCHANGE = "warehouse.event.exchange";

    public static final String QUEUE_INBOUND = "warehouse.queue.inbound";
    public static final String QUEUE_OUTBOUND = "warehouse.queue.outbound";
    public static final String QUEUE_STOCKTAKE = "warehouse.queue.stocktake";

    public static final String ROUTING_KEY_INBOUND = "warehouse.inbound";
    public static final String ROUTING_KEY_OUTBOUND = "warehouse.outbound";
    public static final String ROUTING_KEY_STOCKTAKE = "warehouse.stocktake";

    public static final String DEAD_LETTER_EXCHANGE = "warehouse.event.dlx.exchange";
    public static final String DEAD_LETTER_QUEUE = "warehouse.queue.dlq";
    public static final String DEAD_LETTER_ROUTING_KEY = "warehouse.dlx";

    private RabbitMqConstants() {
    }
}
