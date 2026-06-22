package com.dc3.infra.config;

import com.dc3.common.constant.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                throw new RuntimeException("消息发送到交换机失败: " + cause);
            }
        });
        rabbitTemplate.setReturnsCallback(returned -> {
            throw new RuntimeException("消息路由到队列失败: " + returned.getMessage());
        });
        return rabbitTemplate;
    }

    @Bean
    public DirectExchange warehouseEventExchange() {
        return ExchangeBuilder.directExchange(RabbitMqConstants.WAREHOUSE_EVENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitMqConstants.DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public Queue inboundQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_ROUTING_KEY);
        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INBOUND).withArguments(args).build();
    }

    @Bean
    public Queue outboundQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_ROUTING_KEY);
        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OUTBOUND).withArguments(args).build();
    }

    @Bean
    public Queue stocktakeQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_ROUTING_KEY);
        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STOCKTAKE).withArguments(args).build();
    }

    @Bean
    public Binding inboundBinding() {
        return BindingBuilder.bind(inboundQueue())
                .to(warehouseEventExchange())
                .with(RabbitMqConstants.ROUTING_KEY_INBOUND);
    }

    @Bean
    public Binding outboundBinding() {
        return BindingBuilder.bind(outboundQueue())
                .to(warehouseEventExchange())
                .with(RabbitMqConstants.ROUTING_KEY_OUTBOUND);
    }

    @Bean
    public Binding stocktakeBinding() {
        return BindingBuilder.bind(stocktakeQueue())
                .to(warehouseEventExchange())
                .with(RabbitMqConstants.ROUTING_KEY_STOCKTAKE);
    }
}
