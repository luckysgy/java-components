package com.concise.component.mq.kafka;

import com.concise.component.mq.kafka.enable.EnableKafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2022-01-08 20:06
 */
@EnableKafka(value = false)
@ComponentScan(basePackages = "com.concise.component.mq.kafka")
public class ComponentMqKafkaAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentMqKafkaAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.mq.kafka");
    }
}
