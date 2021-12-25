package com.concise.mq.p1;

import com.alibaba.fastjson.JSON;
import com.concise.component.mq.common.listener.MqListener;
import com.concise.component.mq.common.service.MqRepeatConsumeService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021-12-25 19:20
 */
@Component
@RabbitListener(queues = "${rabbitmq.queues.email.name}")
public class RabbitEmailListener implements MqListener {
    private static final Logger log = LoggerFactory.getLogger(RabbitEmailListener.class);

    @Autowired
    private MqRepeatConsumeService repeatConsumeService;

    @RabbitHandler
    public void onMessage(String msg, Channel channel, Message message) {
        try {
            RabbitEmailMessage rabbitEmailMessage = JSON.parseObject(message.getBody(), RabbitEmailMessage.class);
            // 判断是否重复消费
            if (repeatConsumeService.isConsumed(rabbitEmailMessage.getMsgId())) {
                log.warn("message {} are repeatedly consumed, do not perform business", rabbitEmailMessage.getMsgId());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                return;
            }
            repeatConsumeService.markConsumed(rabbitEmailMessage.getMsgId());

            // 使用rabbitEmailMessage 进行业务处理
            log.info("处理业务: {}", msg);

            /*
              因为我在application.yml那里配置了消息手工确认也就是传说中的ack,所以消息消费后必须发送确认给mq
              很多人不理解ack(消息消费确认),以为这个确认是告诉消息发送者的,这个是错的,这个ack是告诉mq服务器,
              消息已经被我消费了,你可以删除它了
              如果没有发送basicAck的后果是:每次重启服务,你都会接收到该消息
              如果你不想用确认机制,就去掉application.yml的acknowledge-mode: manual配置,该配置默认
              是自动确认auto,去掉后,下面的channel.basicAck就不用写了
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            try {
                /*
                 * 用于否定确认，但与channel.basicNack相比有一个限制:一次只能拒绝单条消息
                 * 第二个参数，true会重新放回队列，所以需要自己根据业务逻辑判断什么时候使用拒绝
                 *
                 * 拒绝消费当前消息，如果第二参数传入true，就是将数据重新丢回队列里，那么下次还会消费这消息。设置false，
                 * 就是告诉服务器，我已经知道这条消息数据了，因为一些原因拒绝它，而且服务器也把这个消息丢掉就行。 下次不
                 * 想再消费这条消息了。使用拒绝后重新入列这个确认模式要谨慎，因为一般都是出现异常的时候，catch异常再拒绝入列，选择是否重入列。
                 * 但是如果使用不当会导致一些每次都被你重入列的消息一直消费-入列-消费-入列这样循环，会导致消息积压。
                 */
                // channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

                /*
                 * 用于否定确认
                 * 第一个参数依然是当前消息到的数据的唯一id;
                 * 第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。
                 * 第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去。
                 * 同样使用不确认后重新入列这个确认模式要谨慎，因为这里也可能因为考虑不周出现消息一直被重新丢回去的情况，导致积压。
                 */
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);

                /*
                 * basic.recover是否恢复消息到队列，参数是是否requeue，true则重新入队列，并且尽可能的将之前recover的
                 * 消息投递给其他消费者消费，而不是自己再次消费。false则消息会重新被投递给自己。
                 */
                // channel.basicRecover(true);
            } catch (Exception ex) {
                log.error("EmailMqListener basicNack fail: {}", ex.getMessage());
                return;
            }
            log.error("EmailMqListener exec fail: {}", e.getMessage());
        }
    }
}
