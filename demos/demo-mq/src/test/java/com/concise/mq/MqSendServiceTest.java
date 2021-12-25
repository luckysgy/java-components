package com.concise.mq;

import com.concise.component.core.utils.UUIDUtil;
import com.concise.mq.p1.RabbitEmailMessage;
import com.concise.mq.p1.RabbitOrderMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @author shenguangyang
 * @date 2021-12-25 19:52
 */
@SpringBootTest
public class MqSendServiceTest {
    @Autowired
    private MqSendService mqSendService;

    @Test
    public void sendEmailMessage() throws InterruptedException {
        RabbitEmailMessage message = new RabbitEmailMessage();
        message.setSender(UUIDUtil.uuid());
        message.setRecipient(UUIDUtil.uuid());
        mqSendService.send(message);
        TimeUnit.SECONDS.sleep(120);
    }

    @Test
    public void sendOrderlMessage() throws InterruptedException {
        RabbitOrderMessage message = new RabbitOrderMessage();
        message.setSerialNumber(UUIDUtil.uuid());
        mqSendService.send(message);
        TimeUnit.SECONDS.sleep(120);
    }
}
