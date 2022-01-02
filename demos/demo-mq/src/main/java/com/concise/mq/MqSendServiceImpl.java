package com.concise.mq;

import com.concise.component.mq.rabbitmq.send.RabbitmqService;
import com.concise.mq.p1.RabbitEmailMessage;
import com.concise.mq.p1.RabbitOrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author shenguangyang
 * @date 2021-12-25 19:33
 */
@Service
public class MqSendServiceImpl implements MqSendService {
    private static final Logger log = LoggerFactory.getLogger(MqSendServiceImpl.class);
    @Autowired
    private RabbitmqService rabbitmqService;
    @Value("${rabbitmq.exchanges.email.name}")
    private String EMAIL_EXCHANGE;

    @Value("${rabbitmq.exchanges.order.name}")
    private String ORDER_EXCHANGE;

    @Override
    public void send(RabbitEmailMessage message) {
        // 给消息设置过期时间
//        MessagePostProcessor messagePostProcessor = message -> {
//            // 5s之后会过期
//            message.getMessageProperties().setExpiration("5000");
//            message.getMessageProperties().setContentEncoding("utf-8");
//            return message;
//        };
        log.info("ExchangeNames.EMAIL: {} , mqMessage: {}", EMAIL_EXCHANGE, message);
        rabbitmqService.send(EMAIL_EXCHANGE, "", message);
    }

    @Override
    public void send(RabbitOrderMessage message) {
        log.info("ExchangeNames.EMAIL: {} , mqMessage: {}", EMAIL_EXCHANGE, message);
        rabbitmqService.send(ORDER_EXCHANGE, "", message);
    }
}
