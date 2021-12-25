package com.concise.component.mq.mqtt.service;

import com.concise.component.mq.mqtt.config.MqttConfig;
import com.concise.component.mq.mqtt.config.MqttEnabled;
import com.concise.component.mq.mqtt.enums.QosEnum;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author shenguangyang
 * @date 2021-12-19 9:42
 */
@Service
public class MqttSendServiceImpl implements MqttSendService {
    private static final Logger log = LoggerFactory.getLogger(MqttSendServiceImpl.class);

    @Override
    public void send(String topic, QosEnum qosEnum, String message) throws MqttException {
        if (!MqttEnabled.enabled) {
            log.warn("mqtt not enable");
            return;
        }
        MqttTopic mqttTopic = MqttConfig.getPublishMqttTopic(topic);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes(StandardCharsets.UTF_8));
        mqttMessage.setQos(qosEnum.getValue());
        mqttMessage.setRetained(true);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
        log.info("message is published completely! " + token.isComplete());
    }
}
