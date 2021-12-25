package com.concise.mq.p1;

import com.alibaba.fastjson.JSON;
import com.concise.component.mq.common.listener.MqListener;
import com.concise.component.mq.common.service.MqRepeatConsumeService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021-12-25 20:11
 */
@Component
@RabbitListener(queues = "${rabbitmq.queues.orderExpired.name}")
public class RabbitOrderListener implements MqListener {
    private static final Logger log = LoggerFactory.getLogger(RabbitOrderListener.class);

    @Autowired
    private MqRepeatConsumeService repeatConsumeService;

    @RabbitHandler
    public void onMessage(String msg, Channel channel, Message message) {
        try {
            RabbitOrderMessage bizMessage = JSON.parseObject(message.getBody(), RabbitOrderMessage.class);
            // 判断是否重复消费
            if (repeatConsumeService.isConsumed(bizMessage.getMsgId())) {
                log.warn("message {} are repeatedly consumed, do not perform business", bizMessage.getMsgId());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                return;
            }
            repeatConsumeService.markConsumed(bizMessage.getMsgId());

            // 使用rabbitEmailMessage 进行业务处理
            log.info("处理业务: {}", msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);

                // channel.basicRecover(true);
            } catch (Exception ex) {
                log.error("EmailMqListener basicNack fail: {}", ex.getMessage());
                return;
            }
            log.error("EmailMqListener exec fail: {}", e.getMessage());
        }
    }
}
