package com.concise.component.mq.rabbitmq.sendfail;

import java.util.List;

/**
 * 发送消息失败服务
 * @author shenguangyang
 * @date 2021-10-07 10:11
 */
public interface MqSendFailService<T> {
    /**
     * 获取失败的数据
     * @return 失败的数据集合
     */
    List<T> get();

    /**
     * 获取一个消息
     * @param msgId id
     */
    T get(String msgId);

    /**
     * 保存数据
     */
    void save(T message);

    /**
     * 通过消息id更新重试次数
     */
    void updateByMsgId(T mqMessage);

    /**
     * 通过消息id删除数据
     */
    void delete(String msgId);
}
