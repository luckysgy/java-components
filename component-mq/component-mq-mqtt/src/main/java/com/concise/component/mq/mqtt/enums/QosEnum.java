package com.concise.component.mq.mqtt.enums;

import lombok.Getter;

/**
 * QoS是Sender和Receiver之间的协议，而不是Publisher和Subscriber之间的协议。换句话说，Publisher发布了一条QoS1的消息，
 * 只能保证Broker能至少收到一次这个消息；而对于Subscriber能否至少收到一次这个消息，还要取决于Subscriber在Subscibe的
 * 时候和Broker协商的QoS等级。
 * @author shenguangyang
 * @date 2021-12-14 7:46
 */
@Getter
public enum QosEnum {
    /**
     * Sender 发送的一条消息，Receiver 最多能收到一次，也就是说 Sender 尽力向 Receiver 发送消息，如果发送失败，也就算了；
     */
    QoS0(0),
    /**
     * Sender 发送的一条消息，Receiver 至少能收到一次，也就是说 Sender 向 Receiver 发送消息，如果发送失败，会继续重试，
     * 直到 Receiver 收到消息为止，但是因为重传的原因，Receiver 有可能会收到重复的消息；
     */
    QoS1(1),
    /**
     * Sender 发送的一条消息，Receiver 确保能收到而且只收到一次，也就是说 Sender 尽力向 Receiver 发送消息，如果发送失败，
     * 会继续重试，直到 Receiver 收到消息为止，同时保证 Receiver 不会因为消息重传而收到重复的消息。
     */
    QoS2(2);
    private final int value;

    QosEnum(Integer value) {
        this.value = value;
    }
}
