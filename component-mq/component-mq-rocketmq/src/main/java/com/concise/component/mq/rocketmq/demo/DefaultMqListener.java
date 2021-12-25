package com.concise.component.mq.rocketmq.demo;

import com.concise.component.mq.common.listener.MqListener;
import com.concise.component.mq.rocketmq.domain.RocketMqMessage;
import com.concise.component.mq.rocketmq.service.RocketMqSendService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
 * @author shenguangyang
 * @date 2021-10-04 14:57
 */
@Component
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        topic = DefaultMqBaseInfo.TOPIC,
        consumerGroup = DefaultMqBaseInfo.CONSUMER_GROUP,
        selectorExpression = DefaultMqBaseInfo.SELECTOR_EXPRESSION)
public class DefaultMqListener implements MqListener, RocketMQListener<RocketMqMessage<DefaultMqMessage>> {
    private static final Logger log = LoggerFactory.getLogger(DefaultMqListener.class);

    @Override
    public void onMessage(RocketMqMessage<DefaultMqMessage> message) {
        // mqProcessor.consume(message.getContent());
    }
}
