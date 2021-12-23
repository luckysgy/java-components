package com.concise.component.mq.rocketmq.service.impl;

import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.rocketmq.domain.RocketMqMessage;
import com.concise.component.mq.rocketmq.expand.MqBaseInfo;
import com.concise.component.mq.rocketmq.service.RocketMqSendService;
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
public class RocketMqSendServiceImpl implements RocketMqSendService {
    private static final Logger log = LoggerFactory.getLogger(RocketMqSendServiceImpl.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public <T> SendResult send(T message, String topic, String group, String tag)  {
        if (StringUtils.isBlank(topic) || StringUtils.isBlank(group)) {
            throw  new BizException("发送方topic或者group不能为空");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        RocketMqMessage<T> rocketMqMessage = new RocketMqMessage<>();
        rocketMqMessage.setProducerTopic(topic);
        rocketMqMessage.setProducerGroup(group);
        rocketMqMessage.setProducerTag(tag);
        rocketMqMessage.setContent(message);
        rocketMqMessage.setMsgKey(uuid);
        // 发送消息
        Message<RocketMqMessage<T>> messageFinal = MessageBuilder.withPayload(rocketMqMessage).setHeader("KEYS", uuid).build();
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            destination = topic + ":" + tag;
        }
        SendResult result = rocketMQTemplate.syncSend(destination, messageFinal);
        log.info("成功发送消息,消息内容为:{},返回值为:{}", message, result);
        return result;
    }

    @Override
    public <T> SendResult send(T message, String topic, String group) {
        return this.send(message, topic, group, null);
    }

    @Override
    public <T,D extends MqBaseInfo> SendResult send(T message, D mqBaseInfo, boolean isTag) {
        if (isTag) {
            return send(message, mqBaseInfo.getTopic(), mqBaseInfo.getConsumerGroup(), mqBaseInfo.getSelectorExpression());
        }
        if (StringUtils.isNotNull(mqBaseInfo.getSelectorExpression()) && !"*".equals(mqBaseInfo.getSelectorExpression())) {
            log.warn("由于 {} 类上@RocketMQMessageListener的SelectorExpression值为 {} 且您关闭了tag选择功能, 因此消息不会被消费",
                    mqBaseInfo.getClass().getSimpleName(), mqBaseInfo.getSelectorExpression());
        }
        return send(message, mqBaseInfo.getTopic(), mqBaseInfo.getConsumerGroup(), null);
    }
}
