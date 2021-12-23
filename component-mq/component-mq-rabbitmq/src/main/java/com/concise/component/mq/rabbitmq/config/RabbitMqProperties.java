package com.concise.component.mq.rabbitmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shenguangyang
 * @date 2021-10-05 12:50
 */
@Data
@Component
@ConfigurationProperties(
        prefix = "rabbitmq"
)
public class RabbitMqProperties {
    /**
     * 装载自定义配置交换机, key是自定义消息类型, 方便管理
     */
    private Map<String, ExchangeConfig> exchanges = new HashMap<>();

    /**
     * 装载自定义配置队列，key是自定义消息类型, 方便管理
     */
    private Map<String, QueueConfig> queues = new HashMap<>();

}
