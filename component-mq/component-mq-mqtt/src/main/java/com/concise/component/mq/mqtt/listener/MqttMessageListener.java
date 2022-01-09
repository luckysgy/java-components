package com.concise.component.mq.mqtt.listener;

import com.concise.component.mq.common.enums.QosEnum;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

import java.lang.annotation.*;

/**
 * 在监听者上标注该注解, 然后实现 {@link IMqttMessageListener} 接口
 *
 * <code>
 *      @Component
 *      @MqttMessageListener(topic = "test", qos = QosEnum.QoS1)
 *      public class MqttDemo1Listener implements IMqttMessageListener {
 *          private static final Logger log = LoggerFactory.getLogger(MqttDemo1Listener.class);
 *          @Override
 *          public void messageArrived(String topic, MqttMessage mqttMessage) {
 *              log.info("topic: {}, qos: {},  message: {}", topic, mqttMessage.getQos(),  new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
 *          }
 *      }
 * </code>
 * @author shenguangyang
 * @date 2021-12-13 20:25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMessageListener {
    String[] topic();

    QosEnum[] qos();
}
