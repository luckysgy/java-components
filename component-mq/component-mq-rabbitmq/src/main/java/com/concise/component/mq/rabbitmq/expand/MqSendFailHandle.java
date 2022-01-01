package com.concise.component.mq.rabbitmq.expand;

import com.concise.component.mq.rabbitmq.entity.RabbitBaseMqMessage;

/**
 * 消息发送失败处理, 由用户自己去继承扩展实现具体的逻辑
 * @author shenguangyang
 * @date 2021-10-07 10:28
 */
public interface MqSendFailHandle {
    /**
     * 消息已经达到最大的重试次数, 但依旧还是消费失败, 这里很有可能需要人工的干预
     * @param message 失败的消息
     */
    void reachMaxRetryCount(RabbitBaseMqMessage message);
}
