package com.concise.component.mq.common;

import com.concise.component.mq.common.listener.MqListenerScan;
import com.concise.component.mq.common.properties.MqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:32
 */
@MqListenerScan
@EnableConfigurationProperties(MqProperties.class)
@ComponentScan(basePackages = "com.concise.component.mq.common")
public class ComponentMqCommonAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentMqCommonAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.mq.common");
    }
}
