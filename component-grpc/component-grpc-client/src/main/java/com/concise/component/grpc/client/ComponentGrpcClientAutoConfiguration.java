package com.concise.component.grpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2022-01-01 20:10
 */
@ComponentScan(basePackages = "com.concise.component.grpc.client")
public class ComponentGrpcClientAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentGrpcClientAutoConfiguration.class);
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.grpc.client");
    }
}
