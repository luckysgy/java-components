package com.concise.component.mq.rocketmq.service.impl;

import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.BaseMqMessage;
import com.concise.component.mq.common.service.RocketMqService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author shenguangyang
 * @date 2021/7/24 9:39
 */
@Service
public class RocketMqServiceImpl implements RocketMqService {
    private static final Logger log = LoggerFactory.getLogger(RocketMqServiceImpl.class);

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public <T extends BaseMqMessage> SendResult send(T message, String topic, String tag)  {
        if (rocketMQTemplate == null || rocketMQTemplate.getProducer() == null) {
            log.warn("rocketmq not initiated");
            return null;
        }
        if (StringUtils.isBlank(topic)) {
            throw  new BizException("发送方topic不能为空");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        message.setMsgId(uuid);
        // 发送消息
        Message<T> messageFinal = MessageBuilder.withPayload(message).setHeader("KEYS", uuid).build();
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            destination = topic + ":" + tag;
        }
        // log.info("成功发送消息,消息内容为:{},返回值为:{}", message, result);
        return rocketMQTemplate.syncSend(destination, messageFinal);
    }

    @Override
    public <T extends BaseMqMessage> SendResult send(T message, String topic) {
        return this.send(message, topic, null);
    }

    @Override
    public Object getTemplate() {
        return rocketMQTemplate;
    }
}
