package com.concise.mq;

import com.concise.component.mq.mqtt.enable.EnableMqtt;

import com.concise.component.mq.rocketmq.enable.EnableRocketmq;
import com.concise.component.mq.common.listener.MqListenerScan;
import com.concise.mq.p2.Demo1MqListener;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shenguangyang
 * @date 2021-12-24 21:05
 */
@EnableMqtt
@EnableRabbit
@EnableRocketmq
@MqListenerScan(listener = {Demo1MqListener.class}, basePackages = {"com.concise.mq.p2", "com.concise.mq.p1"})
@SpringBootApplication
public class MqApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }
}
