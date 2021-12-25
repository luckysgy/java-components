package com.concise.mq.p1;

import com.concise.component.mq.rabbitmq.entity.RabbitMqMessage;
import com.concise.component.mq.rocketmq.domain.RocketMqMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shenguangyang
 * @date 2021-12-25 20:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RabbitOrderMessage extends RabbitMqMessage {
    private String serialNumber;
}
