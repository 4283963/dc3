package com.dc3.listener;

import com.alibaba.fastjson2.JSON;
import com.dc3.common.constant.RabbitMqConstants;
import com.dc3.domain.dto.WarehouseStocktakeMessage;
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
public class StocktakeMessageListener {

    private static final Logger log = LoggerFactory.getLogger(StocktakeMessageListener.class);

    @Autowired
    private WarehouseEventService warehouseEventService;

    @RabbitListener(queues = RabbitMqConstants.QUEUE_STOCKTAKE, containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody());
            log.info("接收盘点消息: {}", body);
            WarehouseStocktakeMessage stocktake = JSON.parseObject(body, WarehouseStocktakeMessage.class);
            warehouseEventService.handleStocktake(stocktake);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("盘点消息处理失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
