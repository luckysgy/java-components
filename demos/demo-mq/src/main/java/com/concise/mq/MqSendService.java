package com.concise.mq;

import com.concise.mq.p1.RabbitEmailMessage;
import com.concise.mq.p1.RabbitOrderMessage;

/**
 * @author shenguangyang
 * @date 2021-12-25 19:32
 */
public interface MqSendService {
    default void send(RabbitEmailMessage message) {}
    default void send(RabbitOrderMessage message) {}
}
