package com.concise.component.mq.rocketmq.demo;

import com.concise.component.mq.rocketmq.expand.MqBaseInfo;

/**
 * mq 基本信息, 比如存放topic, tag等信息
 *
 * 使用方式:
 * @author shenguangyang
 * @date 2021-09-30 20:45
 */
public class DefaultMqBaseInfo implements MqBaseInfo {
    public static final String TOPIC = "test";
    public static final String CONSUMER_GROUP = "test";
    public static final String SELECTOR_EXPRESSION = "test";

    private DefaultMqBaseInfo() {
    }

    public static DefaultMqBaseInfo build() {
        return new DefaultMqBaseInfo();
    }


    public String getTopic() {
        return TOPIC;
    }


    public String getConsumerGroup() {
        return CONSUMER_GROUP;
    }


    public String getSelectorExpression() {
        return SELECTOR_EXPRESSION;
    }
}
