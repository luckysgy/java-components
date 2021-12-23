package com.concise.component.mq.mqtt.config;

import com.concise.component.core.utils.UUIDUtil;
import com.concise.component.mq.mqtt.listener.MqttMessageListener;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author shenguangyang
 * @date 2021-12-13 20:16
 */
@Component
@Configuration
@ConditionalOnProperty(value = "mqtt.enabled", havingValue = "true")
public class MqttConfig {
    private static final Logger log = LoggerFactory.getLogger(MqttConfig.class);
    private static MqttProperties mqttProperties;

    @Autowired
    private ApplicationContext applicationContext;

    public MqttConfig(MqttProperties mqttProperties) {
        MqttConfig.mqttProperties = mqttProperties;
    }

    public static MqttClient publishMqttClient;
    public static Map<String, MqttTopic> publishDataMap = new ConcurrentHashMap<>();

    /**
     * key: topic
     * value: 回调函数
     */
    public static Map<String, ListenerMqttData> listenMqttDataMap = new ConcurrentHashMap<>();


    public static MqttTopic getPublishMqttTopic(String topic) {
        MqttTopic mqttTopic = publishDataMap.get(topic);
        if (mqttTopic == null) {
            synchronized (MqttConfig.class) {
                mqttTopic = publishDataMap.get(topic);
                if (mqttTopic == null) {
                    mqttTopic = publishMqttClient.getTopic(topic);
                    publishDataMap.put(topic, mqttTopic);
                }
            }
        }
        return mqttTopic;
    }

    @PostConstruct
    public void init() throws MqttException {
        // 创建发布者客户端
        publishMqttClient = createMqttClient();
        // 完成监听者的配置
        doListenerConfig();
    }

    /**
     * 收集所有回调类
     */
    private List<IMqttMessageListener> collectiMqttMessageListener() {
        List<IMqttMessageListener> iMqttMessageListeners = new ArrayList<>();
        //获取继承了settingsCrudBiz的所有子类
        Map<String, IMqttMessageListener> settings = applicationContext.getBeansOfType(IMqttMessageListener.class);
        settings.forEach((key,val) ->{
            //遍历执行
            iMqttMessageListeners.add(val);
        });
        return iMqttMessageListeners;
    }

    private static MqttClient createMqttClient() throws MqttException {
        String clientId = mqttProperties.getClientId() + "-" + UUIDUtil.uuid();
        MqttClient mqttClient = new MqttClient(mqttProperties.getServerUri(), clientId , new MemoryPersistence());
        // 判断是否断开连接
        if (!mqttClient.isConnected()) {
            log.info("连接设备, clientId: {}", clientId);
            mqttClient.connect(getOption());
        }
        return mqttClient;
    }

    private void doListenerConfig() throws MqttException {
        List<IMqttMessageListener> iMqttMessageListeners = collectiMqttMessageListener();
        for (IMqttMessageListener iMqttMessageListener : iMqttMessageListeners) {
            MqttClient mqttClient = createMqttClient();

            MqttMessageListener mqttMessageListener = iMqttMessageListener.getClass().getAnnotation(MqttMessageListener.class);
            if (mqttMessageListener == null) {
                throw new RuntimeException(iMqttMessageListener.getClass().getSimpleName() + " not MqttMessageListener annotation");
            }
            String mqttDataKey = "";
            Map<String, MqttTopic> mqttTopics = new ConcurrentHashMap<>();
            List<String> topics = new ArrayList<>();
            for (String topic : mqttMessageListener.topic()) {
                mqttDataKey = mqttDataKey + topic;
                mqttTopics.put(topic, mqttClient.getTopic(topic));
                topics.add(topic);
            }

            ListenerMqttData listenerMqttData = ListenerMqttData.builder()
                    .mqttMessageListener(iMqttMessageListener)
                    .mqttTopics(mqttTopics).client(mqttClient).topics(topics)
                    .qosEnums(Arrays.stream(mqttMessageListener.qos()).collect(Collectors.toList())).build();
            listenMqttDataMap.put(mqttDataKey, listenerMqttData);

            mqttClient.subscribe(getTopics(mqttDataKey), getQos(mqttDataKey), new IMqttMessageListener[]{iMqttMessageListener});
        }
        log.info("mqtt 连接成功");
    }

    private int[] getQos(String mqttDataKey) {
        ListenerMqttData listenerMqttData = listenMqttDataMap.get(mqttDataKey);
        int[] qosArray = new int[listenerMqttData.getQosEnums().size()];
        for (int i = 0; i < listenerMqttData.getQosEnums().size(); i++) {
            qosArray[i] = listenerMqttData.getQosEnums().get(i).getValue();
        }
        return qosArray;
    }

    private String[] getTopics(String mqttDataKey) {
        ListenerMqttData listenerMqttData = listenMqttDataMap.get(mqttDataKey);
        String[] topicsArray = new String[listenerMqttData.getTopics().size()];
        for (int i = 0; i < listenerMqttData.getTopics().size(); i++) {
            topicsArray[i] = listenerMqttData.getTopics().get(i);
        }
        return topicsArray;
    }

    /**
     * 获取连接参数
     */
    private static MqttConnectOptions getOption() {
        //MQTT连接设置
        MqttConnectOptions option = new MqttConnectOptions();
        //设置是否清空session,false表示服务器会保留客户端的连接记录，true表示每次连接到服务器都以新的身份连接
        option.setCleanSession(false);
        if (mqttProperties.getUsername() != null && !"".equals(mqttProperties.getUsername())) {
            //设置连接的用户名
            option.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null && !"".equals(mqttProperties.getPassword())) {
            //设置连接的密码
            option.setPassword(mqttProperties.getPassword().toCharArray());
        }

        option.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        option.setKeepAliveInterval(mqttProperties.getKeepAliveSeconds());
        //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息 TODO 没有收到心跳视为离线
        //option.setWill("type/23/sn/+/state", willMessage().getBytes(), 2, true);
        return option;
    }

}
