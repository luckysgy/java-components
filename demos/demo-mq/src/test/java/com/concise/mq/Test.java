package com.concise.mq;

import com.concise.component.core.utils.UUIDUtil;
import com.concise.component.mq.mqtt.enums.QosEnum;
import com.concise.component.mq.mqtt.service.MqttSendService;
import com.concise.component.mq.rocketmq.service.RocketMqSendService;
import com.concise.mq.p2.Demo1MqListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author shenguangyang
 * @date 2021-12-25 0:20
 */
@SpringBootTest
public class Test {
    @Autowired
    private RocketMqSendService rocketMqSendService;

    @Autowired
    private MqttSendService mqttSendService;

    @org.junit.jupiter.api.Test
    public void test() throws InterruptedException {
        System.out.println("test");
        TimeUnit.SECONDS.sleep(5);
        rocketMqSendService.send(UUIDUtil.uuid(), "demo1", "demo1", "demo1");
        TimeUnit.HOURS.sleep(5);
    }

    @org.junit.jupiter.api.Test
    public void test1() throws InterruptedException, MqttException {
        System.out.println("test1");
        TimeUnit.SECONDS.sleep(5);
        mqttSendService.send("test", QosEnum.QoS1, UUIDUtil.uuid());
        TimeUnit.HOURS.sleep(5);
    }
}
