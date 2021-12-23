package com.concise.component.mq.rabbitmq.service;

import com.alibaba.fastjson.JSON;
import com.concise.component.mq.common.MqConsumer;
import com.concise.component.mq.common.MqMessage;
import com.concise.component.mq.common.service.MqRepeatConsumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * mq监听服务
 * @author shenguangyang
 * @date 2021-10-07 14:51
 */
@Service
public class MqListener {
    private static final Logger log = LoggerFactory.getLogger(MqListener.class);

    @Autowired
    private MqRepeatConsumeService repeatConsumeService;

    /**
     * 执行监听到的消息, 内部已经实现防止重复消费 你需要在 {@link MqConsumer#consume(Object)} 中调用改方法
     * 你也可以不调用, 自行实现相关业务逻辑
     * @param msg 消息
     * @param mqConsumer 消费者类
     * @param tClass 消息实体
     */
    public <T extends MqMessage> void exec(String msg, MqConsumer<T> mqConsumer, Class<T> tClass) {
        T message = JSON.parseObject(msg, tClass);
        if (repeatConsumeService.isConsumed(message.getMsgId())) {
            log.warn("message {} are repeatedly consumed, do not perform business", message.getMsgId());
            return;
        }
        repeatConsumeService.markConsumed(message.getMsgId());
        log.debug("message {} is consumed", message.getMsgId());
        mqConsumer.consume(message);
    };
}
