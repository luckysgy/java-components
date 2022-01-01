package com.concise.component.mq.rabbitmq.task;

import com.alibaba.fastjson.JSON;
import com.concise.component.mq.common.service.MqSendFailService;
import com.concise.component.mq.rabbitmq.entity.RabbitBaseMqMessage;
import com.concise.component.mq.rabbitmq.expand.MqSendFailHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时从缓存中拉取发送失败消息重新投递到消息队列中
 * @author shenguangyang
 * @date 2021-10-07 10:01
 */
@Component
public class ReSendMessageTask {
    private static final Logger log = LoggerFactory.getLogger(ReSendMessageTask.class);

    @Autowired
    private MqSendFailService<RabbitBaseMqMessage> sendFailService;

    @Autowired(required = false)
    private MqSendFailHandle mqSendFailHandle;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/30 * * * * ?")
    public void reSend() {
        log.debug("task --- start resending failed messages");
        List<RabbitBaseMqMessage> rabbitMqMessages = sendFailService.get();
        for (RabbitBaseMqMessage rabbitMqMessage : rabbitMqMessages) {
            if (rabbitMqMessage.isMaxRetryCount()) {
                log.error("the message reaches the maximum number of reposts: {}", rabbitMqMessage.getMsgId());
                // 需要人工干预处理
                if (isHasMqSendFailHandle()) {
                    mqSendFailHandle.reachMaxRetryCount(rabbitMqMessage);
                }
                // 删除缓存记录
                sendFailService.delete(rabbitMqMessage.getMsgId());
            } else {
                log.debug("re-delivery this message : {}", rabbitMqMessage.getMsgId());
                CorrelationData correlationData = new CorrelationData();
                correlationData.setId(rabbitMqMessage.getMsgId());
                try {
                    rabbitTemplate.convertAndSend(rabbitMqMessage.getExchange(), rabbitMqMessage.getRoutingKey(), JSON.toJSONString(rabbitMqMessage.getMessage()), correlationData);
                } catch (AmqpException e) {
                    log.error("re-delivery this message fail: {}", rabbitMqMessage.getMsgId());
                }
                rabbitMqMessage.nextRetryCount();
                sendFailService.updateByMsgId(rabbitMqMessage);
            }
        }
        log.debug("task --- end resending failed messages");
    }

    public boolean isHasMqSendFailHandle() {
        return mqSendFailHandle != null;
    }
}
