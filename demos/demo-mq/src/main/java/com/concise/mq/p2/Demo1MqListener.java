package com.concise.mq.p2;

import com.concise.component.mq.common.listener.MqListener;
import com.concise.component.mq.rocketmq.domain.RocketMqMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-24 21:05
 */
@Component
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        topic = "demo1",
        consumerGroup = "demo1",
        selectorExpression = "demo1")
public class Demo1MqListener implements MqListener, RocketMQListener<RocketMqMessage<String>> {
    private static final Logger log = LoggerFactory.getLogger(Demo1MqListener.class);

    @PostConstruct
    public void init() {
        log.info("init Demo1MqListener");
    }
    public Demo1MqListener() {
        System.out.println("----------------------------------+++++++++++++++++++Demo1MqListener");
    }

    @Override
    public void onMessage(RocketMqMessage<String> stringRocketMqMessage) {
        System.out.println(this.getClass().getName());
        System.out.println(stringRocketMqMessage.getContent());
    }
}
