package com.concise.component.mq.kafka;

import com.concise.component.core.exception.BizException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @author shenguangyang
 * @date 2022-01-09 7:12
 */
public class KafkaHelper {
    public static <K> ListenableFuture<SendResult<K, String>> toListenableFuture(Object sendResult) {
        if (sendResult instanceof ListenableFuture) {
            return  (ListenableFuture<SendResult<K, String>>) sendResult;
        }
        throw new BizException("sendResult not instanceof ListenableFuture");
    }

    public static <K, V> KafkaTemplate<K, V> toKafkaTemplate(Object template) {
        if (template instanceof KafkaTemplate) {
            return (KafkaTemplate<K, V>) template;
        }
        throw new BizException("template not instanceof KafkaTemplate");
    }
}
