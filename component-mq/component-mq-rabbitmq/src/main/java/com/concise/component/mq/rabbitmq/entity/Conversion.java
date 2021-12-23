package com.concise.component.mq.rabbitmq.entity;

/**
 * 转换类
 * @author shenguangyang
 * @date 2021-10-07 12:55
 */
public class Conversion {
    public static RabbitMqMessage to(CustomCorrelationData customCorrelationData) {
        RabbitMqMessage rabbitMqMessage = new RabbitMqMessage();
        rabbitMqMessage.setMessage(customCorrelationData.getMessage());
        rabbitMqMessage.setMsgId(customCorrelationData.getId());
        rabbitMqMessage.setExchange(customCorrelationData.getExchange());
        rabbitMqMessage.setRetryCount(customCorrelationData.getRetryCount());
        rabbitMqMessage.setRoutingKey(customCorrelationData.getRoutingKey());
        return rabbitMqMessage;
    }
    public CustomCorrelationData to(RabbitMqMessage rabbitMqMessage) {
        CustomCorrelationData data = new CustomCorrelationData();
        data.setRetryCount(rabbitMqMessage.getRetryCount());
        data.setMessage(rabbitMqMessage.getMessage());
        data.setExchange(rabbitMqMessage.getExchange());
        data.setId(rabbitMqMessage.getMsgId());
        data.setRoutingKey(rabbitMqMessage.getRoutingKey());
        return data;
    }
}
