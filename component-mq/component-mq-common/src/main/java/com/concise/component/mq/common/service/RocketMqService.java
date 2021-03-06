package com.concise.component.mq.common.service;

import com.concise.component.mq.common.BaseMqMessage;

/**
 * @author shenguangyang
 * @date 2021/7/24 9:33
 */
public interface RocketMqService {
    /**
     * 发送带tag的消息
     *
     * @param message 消息
     * @param topic 表示一类消息的集合，每个主题包含若干条消息，每条消息只能属于一个主题，是RocketMQ进行消息订阅的基本单位。
     * @param tag topic中消息的标签，消费者消费topic中的消息时可以根据tag标签分类消费
     * @return org.apache.rocketmq.client.producer.SendResult
     **/
    <T extends BaseMqMessage> Object send(T message, String topic, String tag);

    /**
     * 发送带不tag的消息
     * @param message 消息
     * @param topic 表示一类消息的集合，每个主题包含若干条消息，每条消息只能属于一个主题，是RocketMQ进行消息订阅的基本单位。
     * @return SendResult
     */
    <T extends BaseMqMessage> Object send(T message, String topic);

    /**
     * 获取模板
     * @return RocketMQTemplate
     */
    Object getTemplate();
}
