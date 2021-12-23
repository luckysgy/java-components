package com.concise.component.mq.rocketmq.service;

import com.concise.component.mq.rocketmq.expand.MqBaseInfo;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @author shenguangyang
 * @date 2021/7/24 9:33
 */
public interface RocketMqSendService {
    /**
     * 发送带tag的消息
     *
     * @param message 消息
     * @param topic 表示一类消息的集合，每个主题包含若干条消息，每条消息只能属于一个主题，是RocketMQ进行消息订阅的基本单位。
     * @param group 组
     * @param tag topic中消息的标签，消费者消费topic中的消息时可以根据tag标签分类消费
     * @return org.apache.rocketmq.client.producer.SendResult
     **/
    <T> SendResult send(T message, String topic, String group, String tag);

    /**
     * 发送带不tag的消息
     * @param message 消息
     * @param topic 表示一类消息的集合，每个主题包含若干条消息，每条消息只能属于一个主题，是RocketMQ进行消息订阅的基本单位。
     * @param group 组
     * @return {@link SendResult}
     */
    <T> SendResult send(T message, String topic, String group);

    /**
     * 发送带不tag的消息
     * @param message 消息
     * @param mqBaseInfo mq基本信息
     * @param isTag 是否使能标签
     * @return {@link SendResult}
     */
    <T,D extends MqBaseInfo> SendResult send(T message, D mqBaseInfo, boolean isTag);
}
