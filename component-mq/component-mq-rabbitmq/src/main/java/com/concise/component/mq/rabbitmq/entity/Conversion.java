package com.concise.component.mq.rabbitmq.entity;

import com.concise.component.mq.rabbitmq.sendfail.RabbitSendFailMqMessage;

/**
 * 转换类
 * @author shenguangyang
 * @date 2021-10-07 12:55
 */
public class Conversion {
    public static RabbitSendFailMqMessage to(CustomCorrelationData customCorrelationData) {
        RabbitSendFailMqMessage rabbitMqMessage = new RabbitSendFailMqMessage();
        rabbitMqMessage.setMessage(customCorrelationData.getMessage());
        rabbitMqMessage.setMsgId(customCorrelationData.getId());
        rabbitMqMessage.setExchange(customCorrelationData.getExchange());
        rabbitMqMessage.setRetryCount(customCorrelationData.getRetryCount());
        rabbitMqMessage.setRoutingKey(customCorrelationData.getRoutingKey());
        return rabbitMqMessage;
    }
}
