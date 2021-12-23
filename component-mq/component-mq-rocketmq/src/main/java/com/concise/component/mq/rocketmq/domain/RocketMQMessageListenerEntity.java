package com.concise.component.mq.rocketmq.domain;

import lombok.Data;

/**
 * @author shenguangyang
 * @date 2021/7/25 8:15
 */
@Data
public class RocketMQMessageListenerEntity {
    private String topic;
    private String consumerGroup;
    private String selectorExpression;
}
