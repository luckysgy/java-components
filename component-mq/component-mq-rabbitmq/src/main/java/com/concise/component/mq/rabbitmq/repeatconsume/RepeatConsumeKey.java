package com.concise.component.mq.rabbitmq.repeatconsume;

import com.concise.component.cache.common.key.BaseKeyManager;

/**
 * 重复消费key
 * @author shenguangyang
 * @date 2021-10-07 14:39
 */
public class RepeatConsumeKey extends BaseKeyManager {
    public RepeatConsumeKey(int expireSeconds, String templateKey) {
        super(expireSeconds, templateKey);
    }

    /**
     * 重复消费, %s = 消息id
     */
    public static final RepeatConsumeKey repeatConsume = new RepeatConsumeKey(60*60, "mq.repeat.consume.%s");
}
