package com.concise.mq.p1;

import com.concise.component.mq.mqtt.enums.QosEnum;
import com.concise.component.mq.mqtt.listener.MqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author shenguangyang
 * @date 2021-12-25 16:48
 */
@Component
@MqttMessageListener(topic = "test", qos = QosEnum.QoS1)
public class MqttDemo1Listener implements IMqttMessageListener {
    private static final Logger log = LoggerFactory.getLogger(MqttDemo1Listener.class);
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        log.info("topic: {}, qos: {},  message: {}", topic, mqttMessage.getQos(),  new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
    }
}
