package com.concise.component.mq.mqtt.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021-12-14 7:52
 */
@Data
@Component
@ConfigurationProperties(
        prefix = "mqtt"
)
public class MqttProperties {
    /**
     * tcp://10.135.50.154:1883
     * mqtt的tcp地址
     */
    private String serverUri;

    /**
     * mqtt客户端ID
     */
    private String clientId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 设置超时时间 单位为秒
     */
    private Integer connectionTimeout = 10;

    /**
     * 设置会话心跳时间 单位为秒 服务器会每隔(1.5*keepTime)秒的时间向客户端发送个消息判断客户端是否在线
     * 但这个方法并没有重连的机制
     */
    private Integer keepAliveSeconds = 20;

    private String subType = "2060";
}
