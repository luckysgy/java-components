package com.concise.component.mq.mqtt.service;

import com.concise.component.mq.common.BaseMqMessage;
import com.concise.component.mq.common.enums.QosEnum;
import com.concise.component.mq.common.service.MqttSendService;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;

/**
 * @author shenguangyang
 * @date 2022-01-09 12:14
 */
public interface MqttSendExpandService extends MqttSendService {
    <T extends BaseMqMessage> MqttDeliveryToken send(String topic, QosEnum qosEnum, T message) throws Exception;
}
