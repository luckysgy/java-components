package com.concise.component.mq.common.properties;

import lombok.Data;

/**
 * @author shenguangyang
 * @date 2022-01-09 7:27
 */
@Data
public class MqData {
    /**
     * 使用枚举表示使能的mq
     */
    private MqType enableMqType;
    /**
     * 使能的mq
     */
    private String enableMq;
    private Kafka kafka;
    private Rocketmq rocketmq;
    private Mqtt mqtt;
    private Rabbitmq rabbitmq;

    @Data
    public static class Kafka {
        private String topic;
        /**
         * 话题分区策略设置:
         * partition == null && key != null --> kafka会对key进行hash运算将数据传输到得到的分区号
         * partition != null && key != null --> kafka会对消息发送到指定分区中
         * partition != null && key == null --> kafka会对消息发送到指定分区中
         */
        private Integer partition;
        private String key;
        private String consumeGroup;
    }

    @Data
    public static class Rocketmq {
        private Boolean isEnableTag = false;
        private String tag;
        private String topic;
        private String consumeGroup;
    }

    @Data
    public static class Mqtt {
        private String topic;
        private Integer qos;
    }

    @Data
    public static class Rabbitmq {
        private String exchange;
        private String routingKey;
    }
}
