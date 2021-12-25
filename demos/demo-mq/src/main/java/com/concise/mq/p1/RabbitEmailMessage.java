package com.concise.mq.p1;

import com.concise.component.mq.rabbitmq.entity.RabbitMqMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shenguangyang
 * @date 2021-12-25 19:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RabbitEmailMessage extends RabbitMqMessage {
    private String sender;
    private String recipient;
}
