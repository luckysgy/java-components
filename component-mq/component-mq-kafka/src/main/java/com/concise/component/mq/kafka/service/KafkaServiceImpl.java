package com.concise.component.mq.kafka.service;

import com.concise.component.mq.common.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author shenguangyang
 * @date 2022-01-09 6:58
 */
@Service
public class KafkaServiceImpl implements KafkaService {
    @Autowired
    private KafkaTemplate<Object, Object> template;

    @Override
    public Object send(String topic, Integer partition, Object message) {
        return this.template.send(topic, partition, message);
    }

    @Override
    public Object send(String topic, Integer partition, String key, Object message) {
        return this.template.send(topic, partition,key, message);
    }

    @Override
    public Object send(String topic, String key, Object message) {
        return this.template.send(topic,key, message);
    }

    @Override
    public Object send(String topic, Object message) {
        return this.template.send(topic, message);
    }

    @Override
    public Object getTemplate() {
        return template;
    }
}
