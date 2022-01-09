package com.concise.component.mq.rabbitmq;

import com.concise.component.mq.rabbitmq.sendfail.MqSendFailHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * 交换机: 可以绑定一个或多个队列, 将接收到的消息转发到某队列中
 * Publish / Consume
 *
 * 交换机必须与队列进行绑定, 当队列没有指定交换机时候,会有一个默认的交换机
 *
 * 生产者发布的消息都是投递给交换机, 交换机会根据type,有选择的发送给队列
 *
 * 生产者发送失败和消费者消费失败处理
 * <H2>消息发送失败情况：</H2>
 *  1、网络抖动导致生产者和mq之间的连接中断，导致消息都没发。
 *      答：rabbitmq有自动重连机制，叫retry。具体到rabbitTemplate中叫retryTemplate，可以通过设置retryTemplate来设置重连次数。
 *          1.1、到了重连次数了，还是没连上怎么办呢？造成这种情况通常是服务器宕机等环境问题，这时候会报AmqpException，我们可以捕获这个异常，
 *          然后把消息存入缓存中。等环境正常后，做消息补发。
 *
 *  2、消息发了但是mq没收到，或者mq收到了但是进入到交换机之前（如果开启了消息持久化，那则是持久化之前。交换机、队列、消息默认都是持久化的）消息丢了。
 *      答：rabbitmq有confirm机制，即mq收到消息后会发送一个叫ack的标识给生产者，ack为true表示收到了，ack为false表示没收到或丢了。
 *      rabbitTemplate中有confirmCallback，在这个callback里把ack为false的消息存到缓存，用另外线程重发。
 *
 *  3. 消息到交换机了，但是找不到对应的queue。
 *      答：rabbitmq有return机制，在rabbitTemplate中有returnCallback。找不到queue的消息都会进入到这个callback，
 *      在这个callback里把消息存到缓存，用另外线程重发。
 *
 * 如果消息发送失败, 会主动回调 {@link MqSendFailHandle} 实现的接口
 *
 * 由于mq重启或者网络波动会导致消息丢失问题, 这里采用redis缓存存放发送失败的消息, 定时重发(依赖redis缓存)
 * @author shenguangyang
 * @date 2021-10-04 15:07
 */
@EnableScheduling
@ComponentScan(basePackages = "com.concise.component.mq.rabbitmq")
public class RabbitMqMainConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.mq.rabbitmq");
    }
}
