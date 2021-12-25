package com.concise.component.mq.mqtt.enable;

import com.concise.component.mq.common.listener.MqListenerScanRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 使能mqtt
 * @author shenguangyang
 * @date 2021-12-25 12:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EnableMqttRegistrar.class})
public @interface EnableMqtt {
    /**
     * 是否使能, 默认使能
     * @return
     */
    boolean value() default true;
}
