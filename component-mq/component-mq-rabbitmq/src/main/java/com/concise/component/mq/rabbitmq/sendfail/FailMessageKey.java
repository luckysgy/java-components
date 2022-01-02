package com.concise.component.mq.rabbitmq.sendfail;

import com.concise.component.cache.common.key.BaseKeyManager;

/**
 * @author shenguangyang
 * @date 2021-10-07 11:54
 */
public class FailMessageKey extends BaseKeyManager {
    public FailMessageKey(int expireSeconds, String templateKey) {
        super(expireSeconds, templateKey);
    }

    public FailMessageKey(int expireSeconds, String templateKey, String templateHashKey) {
        super(expireSeconds, templateKey, templateHashKey);
    }

    /**
     * hash %s: 消息id
     */
    public static final FailMessageKey failMessage = new FailMessageKey(60*60, "mq.fail_message", "%s");


}
