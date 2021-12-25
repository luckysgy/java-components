package com.concise.component.mq.rocketmq;

import com.concise.component.mq.rocketmq.enable.EnableRocketmq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 22:00
 */
@EnableRocketmq(value = false)
@ComponentScan(basePackages = "com.concise.component.mq.rocketmq")
public class RocketMqMainConfig {
    private static final Logger log = LoggerFactory.getLogger(RocketMqMainConfig.class);
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.mq.rocketmq");
    }
}
