package com.concise.component.mq.common;

import java.io.Serializable;

/**
 * 消息实体需要继承该类
 * @author shenguangyang
 * @date 2021-10-07 14:46
 */
public class MqMessage implements Serializable {
    /**
     * 消息id
     */
    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
