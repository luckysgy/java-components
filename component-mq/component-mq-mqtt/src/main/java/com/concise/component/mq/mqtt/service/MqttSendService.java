package com.concise.component.mq.mqtt.service;

import com.concise.component.mq.common.BaseMqMessage;
import com.concise.component.mq.mqtt.enums.QosEnum;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * @author shenguangyang
 * @date 2021-12-19 9:41
 */
public interface MqttSendService {
    <T extends BaseMqMessage> void send(String topic, QosEnum qosEnum, T message) throws MqttException;
}
