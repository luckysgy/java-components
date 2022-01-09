package com.concise.component.mq.common.properties;

import com.concise.component.core.exception.BizException;
import lombok.Getter;

/**
 * @author shenguangyang
 * @date 2022-01-09 8:31
 */
@Getter
public enum MqType {
    ROCKETMQ("rocketmq"),
    MQTT("mqtt"),
    RABBITMQ("rabbitmq"),
    KAFKA("kafka");
    private final String type;

    MqType(String type) {
        this.type = type;
    }

    public static MqType getByType(String type) {
        MqType[] values = MqType.values();
        for (MqType mqType : values) {
            if (mqType.getType().equals(type)) {
                return mqType;
            }
        }
        return null;
    }

    /**
     * 类型是否被支持
     * @param type
     */
    public static void isSupported(String type) {
        MqType[] values = MqType.values();
        for (MqType mqType : values) {
            if (mqType.getType().equals(type)) {
                return;
            }
        }
        throw new BizException("mqType not supported, support mqType [ " +
                ROCKETMQ.getType() + " , " + MQTT.getType() + " , " + KAFKA.getType() + " , " + RABBITMQ.getType() + " ]"
                );
    }
}
