package com.concise.component.mq.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:32
 */
@ComponentScan(basePackages = "com.concise.component.mq.common")
public class MqCommonMainConfig {
    private static final Logger log = LoggerFactory.getLogger(MqCommonMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.mq.common");
    }
}
