package com.concise.component.mq.rabbitmq.config;

import lombok.Data;

/**
 * 交换机配
 * @author shenguangyang
 * @date 2021-10-05 13:22
 */
@Data
public class ExchangeConfig {
    /**
     * 是否为持久交换机（该交换机将在服务器重启后保留下来）
     */
    private Boolean durable = Boolean.TRUE;

    /**
     * 交换机名
     */
    private String name;

    /**
     * 交换机类型
     */
    private ExchangeType type;
}
