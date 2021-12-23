package com.concise.component.mq.rabbitmq.entity;

import lombok.Getter;

/**
 * @author shenguangyang
 * @date 2021-10-07 10:19
 */
@Getter
public enum MessageStatus {
    DELIVER_SUCCESS(1, "消息投递成功"),
    DELIVER_FAIL(-1, "消息投递失败"),
    CONSUMED_SUCCESS(2, "消息消费成功"),
    CONSUMED_FAIL(-2, "消息消费失败");

    private final int status;
    private final String message;

    MessageStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
