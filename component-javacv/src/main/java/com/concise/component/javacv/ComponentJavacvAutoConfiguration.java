package com.concise.component.javacv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2022-01-15 8:42
 */
@ComponentScan(basePackages = "com.concise.component.javacv")
public class ComponentJavacvAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentJavacvAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.javacv");
    }
}
