package com.concise.component.mq.common;

/**
 * mq 发布消息
 * @author shenguangyang
 * @date 2021-10-04 13:36
 */
public interface MqProducer<T> {
    /**
     * 发布消息
     * @param mqMessage 消息内容
     */
    void produce(T mqMessage);
}
