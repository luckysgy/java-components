package com.concise.component.mq.rabbitmq.sendfail;

import com.concise.component.cache.common.service.CacheService;
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
public class MqSendFailServiceImpl implements MqSendFailService<RabbitSendFailMqMessage> {
    private static final Logger log = LoggerFactory.getLogger(MqSendFailServiceImpl.class);

    @Autowired
    private CacheService cacheService;

    @Override
    public List<RabbitSendFailMqMessage> get() {
        List<RabbitSendFailMqMessage> result = new ArrayList<>();
        FailMessageKey failMessageKey = FailMessageKey.failMessage;
        Map<String, RabbitSendFailMqMessage> allMap = cacheService.opsHash().getAll(failMessageKey.getKey());
        for (Map.Entry<String, RabbitSendFailMqMessage> entry : allMap.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public RabbitSendFailMqMessage get(String msgId) {
        FailMessageKey failMessageKey = FailMessageKey.failMessage;
        return cacheService.opsHash().get(failMessageKey.getKey(), failMessageKey.getHashKey(msgId));
    }

    @Override
    public void save(RabbitSendFailMqMessage message) {
        FailMessageKey key = FailMessageKey.failMessage;
        cacheService.opsHash().put(key.getKey(), key.getHashKey(message.getMsgId()), message);
    }

    @Override
    public void updateByMsgId(RabbitSendFailMqMessage rabbitMqMessage) {
        save(rabbitMqMessage);
    }

    @Override
    public void delete(String msgId) {
        FailMessageKey key = FailMessageKey.failMessage;
        cacheService.opsHash().delete(key.getKey(), key.getHashKey(msgId));
    }
}
