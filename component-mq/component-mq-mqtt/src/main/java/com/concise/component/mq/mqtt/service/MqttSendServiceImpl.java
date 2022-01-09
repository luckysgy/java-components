package com.concise.component.mq.mqtt.service;

import com.alibaba.fastjson.JSON;
import com.concise.component.mq.common.BaseMqMessage;
import com.concise.component.mq.common.enums.QosEnum;
import com.concise.component.mq.mqtt.config.MqttConfig;
import com.concise.component.mq.mqtt.config.MqttEnabled;
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
public class MqttSendServiceImpl implements MqttSendExpandService {
    private static final Logger log = LoggerFactory.getLogger(MqttSendServiceImpl.class);

    @Override
    public <T extends BaseMqMessage> MqttDeliveryToken send(String topic, QosEnum qosEnum, T message) throws MqttException {
        if (!MqttEnabled.enabled) {
            log.warn("mqtt not enable");
            return null;
        }
        MqttTopic mqttTopic = MqttConfig.getPublishMqttTopic(topic);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8));
        mqttMessage.setQos(qosEnum.getValue());
        mqttMessage.setRetained(true);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
        return token;
    }

    @Override
    public <T extends BaseMqMessage> MqttDeliveryToken send(String topic, int qos, T message) throws MqttException {
        QosEnum qosEnum = QosEnum.getQos(qos);
        return send(topic, qosEnum, message);
    }

    @Override
    public Object getTemplate() {
       throw new UnsupportedOperationException("use MqttConfig.getPublishMqttTopic(topic)");
    }
}
