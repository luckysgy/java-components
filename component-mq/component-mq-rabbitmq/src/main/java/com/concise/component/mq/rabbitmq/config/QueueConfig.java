package com.concise.component.mq.rabbitmq.config;

import lombok.Data;

import java.util.Map;

/**
 * 队列配置
 * @author shenguangyang
 * @date 2021-10-05 13:22
 */
@Data
public class QueueConfig {
    /**
     * 队列名（每个队列的名称应该唯一）
     * 必须*
     */
    private String name;

    /**
     * 指定绑定交互机，可绑定多个（逗号分隔）
     * 必须*
     */
    private String exchangeName;

    /**
     * 队列路由键（队列绑定交换机的匹配键，例如：“user” 将会匹配到指定路由器下路由键为：【*.user、#.user】的队列）
     * 不能为null, 否则队列和交换机绑定失败
     */
    private String routingKey = "";

    /**
     * 是否为持久队列（该队列将在服务器重启后保留下来）
     */
    private Boolean durable = Boolean.TRUE;

    /**
     * 是否为排它队列
     */
    private Boolean exclusive = Boolean.FALSE;

    /**
     * 如果队列为空是否删除（如果服务器在不使用队列时是否删除队列）
     */
    private Boolean autoDelete = Boolean.FALSE;

    /**
     * 头队列是否全部匹配
     * 默认：是
     */
    private Boolean whereAll = Boolean.TRUE;

    /**
     * 参数
     */
    private Map<String, Object> args;

    /**
     * 消息头
     */
    private Map<String, Object> headers;
}
