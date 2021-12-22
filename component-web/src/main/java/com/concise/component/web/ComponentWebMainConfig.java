package com.concise.component.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-22 21:57
 */
@ComponentScan(basePackages = "com.concise.component.web")
public class ComponentWebMainConfig {
    private static final Logger log = LoggerFactory.getLogger(ComponentWebMainConfig.class);
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.web");
    }
}
