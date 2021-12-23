package com.concise.component.mq.mqtt.config;

import com.concise.component.mq.mqtt.enums.QosEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.util.List;
import java.util.Map;

/**
 * @author shenguangyang
 * @date 2021-12-19 9:28
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListenerMqttData {
    private List<String> topics;
    private List<QosEnum> qosEnums;
    private IMqttMessageListener mqttMessageListener;
    private MqttClient client;
    public Map<String, MqttTopic> mqttTopics;
}
