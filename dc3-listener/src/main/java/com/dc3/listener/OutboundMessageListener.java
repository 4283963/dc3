package com.dc3.listener;

import com.alibaba.fastjson2.JSON;
import com.dc3.common.constant.RabbitMqConstants;
import com.dc3.domain.dto.WarehouseOutboundMessage;
import com.dc3.service.WarehouseEventService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OutboundMessageListener {

    private static final Logger log = LoggerFactory.getLogger(OutboundMessageListener.class);

    @Autowired
    private WarehouseEventService warehouseEventService;

    @RabbitListener(queues = RabbitMqConstants.QUEUE_OUTBOUND, containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody());
            log.info("接收出库消息: {}", body);
            WarehouseOutboundMessage outbound = JSON.parseObject(body, WarehouseOutboundMessage.class);
            warehouseEventService.handleOutbound(outbound);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("出库消息处理失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
