package com.concise.component.mq.rabbitmq.repeatconsume;

import com.concise.component.cache.common.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shenguangyang
 * @date 2021-10-07 14:35
 */
@Service
public class MqRepeatConsumeServiceImpl implements MqRepeatConsumeService {
    @Autowired
    private CacheService cacheService;

    @Override
    public boolean isConsumed(String msgId) {
        RepeatConsumeKey repeatConsumeKey = RepeatConsumeKey.repeatConsume;
        String key = repeatConsumeKey.getKey(msgId);
        return cacheService.opsValue().get(key) != null;
    }

    @Override
    public void markConsumed(String msgId) {
        RepeatConsumeKey repeatConsumeKey = RepeatConsumeKey.repeatConsume;
        cacheService.opsValue().setEx(repeatConsumeKey.getKey(msgId), "1", repeatConsumeKey.getExpireSeconds());
    }
}
