package com.concise.component.mq.rabbitmq.entity;

import com.concise.component.mq.common.BaseMqMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * mq 消息数据
 * @author shenguangyang
 * @date 2021-10-07 10:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RabbitBaseMqMessage extends BaseMqMessage {
    private static final int MAX_TRY_COUNT = 10;

    // 消息体
    private volatile Object message;
    // 交换机
    private String exchange;
    // 路由键
    private String routingKey;
    // 重试次数
    private int retryCount = 0;
    // 消息状态
    private int status;

    public void nextRetryCount() {
        this.retryCount = this.retryCount + 1;
    }

    /**
     * 是否达到最大重试次数
     * @return true 已经达到最大重新投递次数
     */
    public boolean isMaxRetryCount() {
        return this.retryCount >= MAX_TRY_COUNT;
    }
}
