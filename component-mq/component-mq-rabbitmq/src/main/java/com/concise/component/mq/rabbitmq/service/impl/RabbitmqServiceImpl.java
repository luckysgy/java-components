package com.concise.component.mq.rabbitmq.service.impl;

import com.alibaba.fastjson.JSON;
import com.concise.component.core.utils.UUIDUtil;
import com.concise.component.mq.common.MqMessage;
import com.concise.component.mq.common.service.MqSendFailService;
import com.concise.component.mq.rabbitmq.entity.Conversion;
import com.concise.component.mq.rabbitmq.entity.CustomCorrelationData;
import com.concise.component.mq.rabbitmq.entity.RabbitMqMessage;
import com.concise.component.mq.rabbitmq.service.RabbitmqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shenguangyang
 * @date 2021-10-06 21:38
 */
@Service
public class RabbitmqServiceImpl implements RabbitmqService {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqServiceImpl.class);

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MqSendFailService<RabbitMqMessage> sendFailService;

    @Override
    public <T extends MqMessage> void send(String exchange, String routingKey, T object) {
        send(exchange, routingKey, object, null);
    }

    @Override
    public <T extends MqMessage> void send(String exchange, String routingKey, T object, MessagePostProcessor messagePostProcessor) {
        if (rabbitTemplate == null) {
            log.warn("rabbitmq not enable");
        }
        // 该参数可以传,可以不传,不传时,correlationData的id值默认是null,消息发送成功后,在RabbitMqConfig类的rabbitTemplate类的confirm方法会接收到该值
        String msgId = UUIDUtil.uuid();
        CustomCorrelationData correlationData = new CustomCorrelationData();
        correlationData.setId(msgId);
        correlationData.setRoutingKey(routingKey);
        correlationData.setExchange(exchange);
        correlationData.setRetryCount(0);
        correlationData.setMessage(object);
        object.setMsgId(msgId);

        String msg = JSON.toJSONString(object);

        // 当由于网络原因或者mq服务器挂掉之后会抛出AmqpConnectException异常
        try {
            if (messagePostProcessor == null) {
                rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
            } else {
                rabbitTemplate.convertAndSend(exchange, routingKey, msg, messagePostProcessor, correlationData);
            }
        } catch (Exception e) {
            log.error("amqp send exception: {}, correlationData: {}", e.getMessage(), correlationData);
            RabbitMqMessage rabbitMqMessage = Conversion.to(correlationData);
            sendFailService.save(rabbitMqMessage);
        }
    }
}
