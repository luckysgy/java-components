package com.concise.component.mq.common.service;

/**
 * @author shenguangyang
 * @date 2022-01-09 6:57
 */
public interface KafkaService {
    /**
     *
     * @param topic 主题
     * @param partition 分区
     * @param message 消息
     * @return
     */
    Object send(String topic, Integer partition, Object message);
    Object send(String topic, Integer partition, String key, Object message);
    Object send(String topic, String key, Object message);
    Object send(String topic, Object message);
    Object getTemplate();
}
