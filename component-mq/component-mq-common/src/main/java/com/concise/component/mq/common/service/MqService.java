package com.concise.component.mq.common.service;

import com.concise.component.mq.common.BaseMqMessage;

/**
 * 新增统一mq服务接口, 兼容mqtt, rocketmq, rabbitmq, kafka, 由于不同的业务需求不同使用不同mq实现方式也不同,
 * 这时候需要自己获取getTemplate模板
 * @author shenguangyang
 * @date 2022-01-09 6:50
 */
public interface MqService {
    /**
     * 支持 kafka, rocketmq, rabbitmq, mqtt等消息队列发送消息
     * kafka: 参数有对topic的分区以及key相关数据, 默认采用kafka默认的随机算法, 或者配置文件执行key以及分区
     *         需要特殊处理, 可以调用 {@link #getTemplate(Class)} 直接操作模板类
     * rocketmq, mqtt: 所有发送信息都可以在配置文件中配置
     * @param message 消息
     * @return
     *  kafka 返回结果 ListenableFuture
     *  mqtt 返回结果 MqttDeliveryToken
     *  rocketmq 返回结果 SendResult
     *  rabbitmq 返回结果 null
     *  对结果的转换，可以使用每个mq组件下的 XxxHelper, 比如kKafkaHelper,RocketmqHelper, MqttHelper
     */
    <T extends BaseMqMessage> Object send(T message) throws Exception;

    <T extends BaseMqMessage> Object getTemplate(Class<T> tClass);
}
