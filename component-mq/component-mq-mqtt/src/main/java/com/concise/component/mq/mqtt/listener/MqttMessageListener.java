package com.concise.component.mq.mqtt.listener;

import com.concise.component.mq.mqtt.enums.QosEnum;
import java.lang.annotation.*;

/**
 * @author shenguangyang
 * @date 2021-12-13 20:25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMessageListener {
    /**
     * @see MqttInfoManager
     */
    String[] topic();

    /**
     * @see MqttInfoManager
     */
    QosEnum[] qos();
}
