package com.concise.component.mq.common.properties;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author shenguangyang
 * @date 2022-01-09 7:24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mq")
public class MqProperties {
    private static final Logger log = LoggerFactory.getLogger(MqProperties.class);
    /**
     * 使能的mq, 多个mq使用逗号分割
     */
    private String enableMq;
    /**
     * key: 消息名称
     */
    private Map<String, MqData> data;
}
