package com.concise.component.mq.rabbitmq.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @author shenguangyang
 * @date 2021-10-07 12:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomCorrelationData extends CorrelationData {
    // 消息体
    private volatile Object message;
    // 交换机
    private String exchange;
    // 路由键
    private String routingKey;
    // 重试次数
    private int retryCount = 0;
}
