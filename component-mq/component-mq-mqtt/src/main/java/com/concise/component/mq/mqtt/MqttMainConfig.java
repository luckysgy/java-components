package com.concise.component.mq.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:36
 */
@ComponentScan(basePackages = "com.concise.component.mq.mqtt")
public class MqttMainConfig {
    private static final Logger log = LoggerFactory.getLogger(MqttMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.mq.mqtt");
    }
}
