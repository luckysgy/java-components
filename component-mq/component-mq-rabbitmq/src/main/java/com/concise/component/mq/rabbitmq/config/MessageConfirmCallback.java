package com.concise.component.mq.rabbitmq.config;

import com.concise.component.mq.common.service.MqSendFailService;
import com.concise.component.mq.rabbitmq.entity.Conversion;
import com.concise.component.mq.rabbitmq.entity.CustomCorrelationData;
import com.concise.component.mq.rabbitmq.entity.RabbitMqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通过实现 ConfirmCallback 接口，消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中
 *
 * 如何生效:
 *  配置yaml: spring.rabbitmq.publisher-confirm-type (消息发送后,如果发送成功到队列,则会回调成功信息)
 *
 * publisher-confirm-type三种类型
 *  NONE: 禁用发布确认模式，是默认值
 *  CORRELATED: 发布消息成功到交换器后会触发回调方法
 *  SIMPLE: 经测试有两种效果，其一效果和CORRELATED值一样会触发回调方法，其二在发布消息成功后使用rabbitTemplate调用waitForConfirms或
 *      waitForConfirmsOrDie方法等待broker节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是waitForConfirmsOrDie方法如
 *      果返回false则会关闭channel，则接下来无法发送消息到broker;
 * @author shenguangyang
 * @date 2021-10-06 21:47
 */
@Component
public class MessageConfirmCallback implements RabbitTemplate.ConfirmCallback {
    private static final Logger log = LoggerFactory.getLogger(MessageConfirmCallback.class);
    @Autowired
    private MqSendFailService<RabbitMqMessage> sendFailService;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.debug("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            if (correlationData instanceof CustomCorrelationData) {
                CustomCorrelationData custom = (CustomCorrelationData) correlationData;
                String id = custom.getId();
                sendFailService.delete(id);
            }
        } else {
            log.error("message send fail, id: {}", correlationData.getId());
            if (correlationData instanceof CustomCorrelationData) {
                CustomCorrelationData custom = (CustomCorrelationData) correlationData;
                RabbitMqMessage rabbitMqMessage = Conversion.to(custom);
                sendFailService.save(rabbitMqMessage);
            }
        }
    }
}
