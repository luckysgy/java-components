package com.concise.component.mq.rabbitmq.service.impl;

import com.concise.component.cache.common.service.CacheService;
import com.concise.component.mq.common.service.MqSendFailService;
import com.concise.component.mq.rabbitmq.entity.FailMessageKey;
import com.concise.component.mq.rabbitmq.entity.RabbitMqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shenguangyang
 * @date 2021-10-07 10:15
 */
@Service
public class MqSendFailServiceImpl implements MqSendFailService<RabbitMqMessage> {
    private static final Logger log = LoggerFactory.getLogger(MqSendFailServiceImpl.class);

    @Autowired
    private CacheService cacheService;

    @Override
    public List<RabbitMqMessage> get() {
        List<RabbitMqMessage> result = new ArrayList<>();
        FailMessageKey failMessageKey = FailMessageKey.failMessage;
        Map<String, RabbitMqMessage> allMap = cacheService.opsHash().getAll(failMessageKey.getKey());
        for (Map.Entry<String, RabbitMqMessage> entry : allMap.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public RabbitMqMessage get(String msgId) {
        FailMessageKey failMessageKey = FailMessageKey.failMessage;
        return cacheService.opsHash().get(failMessageKey.getKey(), failMessageKey.getHashKey(msgId));
    }

    @Override
    public void save(RabbitMqMessage message) {
        FailMessageKey key = FailMessageKey.failMessage;
        cacheService.opsHash().put(key.getKey(), key.getHashKey(message.getMsgId()), message);
    }

    @Override
    public void updateByMsgId(RabbitMqMessage rabbitMqMessage) {
        save(rabbitMqMessage);
    }

    @Override
    public void delete(String msgId) {
        FailMessageKey key = FailMessageKey.failMessage;
        cacheService.opsHash().delete(key.getKey(), key.getHashKey(msgId));
    }
}
