package com.concise.component.mq.rabbitmq.send;

import com.concise.component.mq.common.BaseMqMessage;
import com.concise.component.mq.common.service.RabbitmqService;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * @author shenguangyang
 * @date 2022-01-09 12:06
 */
public interface RabbitmqExpandService extends RabbitmqService {
    /**
     * <code>
     *  MessagePostProcessor messagePostProcessor = message -> {
     *       // 5s之后会过期
     *       message.getMessageProperties().setExpiration("5000");
     *       message.getMessageProperties().setContentEncoding("utf-8");
     *       return message;
     *  };
     * </code>
     * @param exchange
     * @param routingKey
     * @param object
     * @param messagePostProcessor
     * @param <T>
     */
    <T extends BaseMqMessage> void send(String exchange, String routingKey, final T object, MessagePostProcessor messagePostProcessor);
}
