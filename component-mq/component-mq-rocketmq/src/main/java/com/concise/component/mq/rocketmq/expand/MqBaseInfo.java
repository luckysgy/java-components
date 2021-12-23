package com.concise.component.mq.rocketmq.expand;

import com.concise.component.mq.rocketmq.demo.DefaultMqBaseInfo;

/**
 * mq 基本信息, 比如存放topic, tag等信息
 *
 * 使用方式: {@link DefaultMqBaseInfo}
 *
 * 子类需要自定义如下几个常量
 * <code>
 *     public static final String TOPIC = "test";
 *     public static final String CONSUMER_GROUP = "test";
 *     public static final String SELECTOR_EXPRESSION = "test";
 * </code>
 *
 * @author shenguangyang
 * @date 2021-09-30 20:45
 */
public interface MqBaseInfo {
    String getTopic();
    String getConsumerGroup();
    String getSelectorExpression();
}
