package com.concise.mq.p1;

import com.concise.component.mq.common.listener.MqListener;
import com.concise.component.mq.rocketmq.demo.DefaultMqBaseInfo;
import com.concise.component.mq.rocketmq.demo.DefaultMqMessage;
import com.concise.component.mq.rocketmq.domain.RocketMqMessage;
import com.concise.mq.p2.Demo1MqListener;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-24 21:05
 */
@Component
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        topic = "demo2",
        consumerGroup = "demo2",
        selectorExpression = "demo2")
public class Demo2MqListener implements MqListener, RocketMQListener<RocketMqMessage<String>> {
    private static final Logger log = LoggerFactory.getLogger(Demo2MqListener.class);

    @PostConstruct
    public void init() {
        log.info("init Demo2MqListener");
    }
    public Demo2MqListener() {
        System.out.println("----------------------------------+++++++++++++++++++Demo2MqListener");
    }

    @Override
    public void onMessage(RocketMqMessage<String> stringRocketMqMessage) {
        System.out.println(this.getClass().getName());
        System.out.println(stringRocketMqMessage.getContent());
    }
}
