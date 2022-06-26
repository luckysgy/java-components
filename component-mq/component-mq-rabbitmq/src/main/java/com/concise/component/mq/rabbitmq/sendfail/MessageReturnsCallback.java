package com.concise.component.mq.rabbitmq.sendfail;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 如何生效
 *  在yaml文件中配置spring.rabbitmq.publisher-returns: true  #消息发送后,如果发送失败,则会返回失败信息信息
 *  没有被正确路由到合适队列的消息也会回调该方法
 * @author shenguangyang
 * @date 2021-10-06 21:51
 */
@Component
public class MessageReturnsCallback implements RabbitTemplate.ReturnsCallback {
    private static final Logger log = LoggerFactory.getLogger(MessageReturnsCallback.class);

    @Autowired
    private MqSendFailService<RabbitSendFailMqMessage> sendFailService;

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",
                returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText(), returned.getMessage());
        byte[] body = returned.getMessage().getBody();
        String msg = new String(body);
        RabbitSendFailMqMessage message = JSON.parseObject(msg, RabbitSendFailMqMessage.class);

        RabbitSendFailMqMessage cacheSendFailMqMessage = sendFailService.get(message.getMsgId());
        if (cacheSendFailMqMessage == null) {
            cacheSendFailMqMessage = new RabbitSendFailMqMessage();
        }
        cacheSendFailMqMessage.setMessage(message);
        cacheSendFailMqMessage.setMsgId(message.getMsgId());
        cacheSendFailMqMessage.setExchange(returned.getExchange());
        cacheSendFailMqMessage.setRoutingKey(returned.getRoutingKey());
        cacheSendFailMqMessage.nextRetryCount();
        sendFailService.save(cacheSendFailMqMessage);
    }
}
