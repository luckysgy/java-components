package com.concise.component.knife4j;

import com.concise.component.knife4j.config.Knife4jApiInfoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shenguangyang
 * @date 2022-03-29 19:46
 */
@EnableConfigurationProperties(Knife4jApiInfoProperties.class)
@ComponentScan(basePackages = "com.concise.component.knife4j")
public class ComponentKnife4jAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentKnife4jAutoConfiguration.class);
}
