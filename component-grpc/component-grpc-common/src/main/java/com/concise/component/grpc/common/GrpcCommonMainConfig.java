package com.concise.component.grpc.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2022-01-01 20:13
 */
@ComponentScan(basePackages = "com.concise.component.grpc.common")
public class GrpcCommonMainConfig {
    private static final Logger log = LoggerFactory.getLogger(GrpcCommonMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.grpc.common");
    }
}
