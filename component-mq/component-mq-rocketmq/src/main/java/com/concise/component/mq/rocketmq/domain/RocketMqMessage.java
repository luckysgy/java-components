package com.concise.component.mq.rocketmq.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息
 * @author shenguangyang
 * @date 2021/7/24 9:26
 */
@Data
public class RocketMqMessage<T> implements Serializable {
    /**
     * 消息内容
     */
    private T content;

    /**
     * 消息的key
     */
    private String msgKey;

    /**
     * topic
     */
    private String producerTopic;
    /**
     * group
     */
    private String producerGroup;
    /**
     * tag
     */
    private String producerTag;
}
