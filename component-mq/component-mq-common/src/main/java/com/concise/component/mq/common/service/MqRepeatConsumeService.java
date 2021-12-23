package com.concise.component.mq.common.service;

/**
 * 用于解决重复消费服务
 * @author shenguangyang
 * @date 2021-10-07 14:33
 */
public interface MqRepeatConsumeService {
    /**
     * 判断是否已经被消费过
     * @param msgId 消息id
     * @return
     */
    boolean isConsumed(String msgId);

    /**
     * 标记已经被消费
     * @param msgId 消费id
     */
    void markConsumed(String msgId);
}
