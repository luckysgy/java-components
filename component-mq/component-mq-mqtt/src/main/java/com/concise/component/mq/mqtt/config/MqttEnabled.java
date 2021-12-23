package com.concise.component.mq.mqtt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-19 11:22
 */
@Component
@ConditionalOnProperty(value = "mqtt.enabled", havingValue = "true")
public class MqttEnabled {

    public static boolean enabled = false;

    @PostConstruct
    public void init() {
        enabled = true;
    }
}
