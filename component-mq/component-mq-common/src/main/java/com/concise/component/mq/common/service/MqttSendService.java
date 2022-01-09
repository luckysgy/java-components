package com.concise.component.mq.common.service;

import com.concise.component.mq.common.BaseMqMessage;

/**
 * @author shenguangyang
 * @date 2021-12-19 9:41
 */
public interface MqttSendService {
    <T extends BaseMqMessage> Object send(String topic, int qos, T message) throws Exception;

    /**
     * 获取模板
     * @return RocketMQTemplate
     */
    Object getTemplate();
}
