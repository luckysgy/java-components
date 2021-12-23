package com.concise.component.mq.common;

/**
 * mq 消费消息
 * @author shenguangyang
 * @date 2021-10-05 10:19
 */
public interface MqConsumer<T> {
    /**
     * 消费消息
     */
    void consume(T mqMessage);
}
