package com.concise.component.mq.common.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.BaseMqMessage;
import com.concise.component.mq.common.properties.MqData;
import com.concise.component.mq.common.properties.MqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenguangyang
 * @date 2022-01-09 6:56
 */
@Service
public class MqServiceImpl implements MqService {
    private static final Logger log = LoggerFactory.getLogger(MqServiceImpl.class);
    @Autowired(required = false)
    private KafkaService kafkaService;

    @Autowired(required = false)
    private RocketMqService rocketMqService;

    @Autowired(required = false)
    private RabbitmqService rabbitmqService;

    @Autowired(required = false)
    private MqttSendService mqttSendService;

    @Autowired
    private MqProperties mqProperties;

    private static final Map<String, MqData> mqDataMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (ObjectUtil.isNull(mqProperties.getData())) {
            return;
        }
        mqDataMap.putAll(mqProperties.getData());
    }

    @Override
    public <T extends BaseMqMessage> Object send(T message) throws Exception {
        MqData mqData = mqDataMap.get(StringUtils.uncapitalize(message.getClass().getSimpleName()));
        if (ObjectUtil.isNotNull(mqData)) {
            MqData.Kafka kafka = mqData.getKafka();
            // kafka
            if (ObjectUtil.isNotNull(kafka) && ObjectUtil.isNotNull(kafka.getEnable()) && kafka.getEnable() &&
                    ObjectUtil.isNotNull(kafkaService)) {
                if (ObjectUtil.isNotNull(kafka.getPartition()) && ObjectUtil.isNotNull(kafka.getKey())) {
                    return kafkaService.send(kafka.getTopic(), kafka.getPartition(), kafka.getKey(), JSON.toJSONString(message));
                } else if (ObjectUtil.isNotNull(kafka.getPartition())) {
                    return kafkaService.send(kafka.getTopic(), kafka.getPartition(), JSON.toJSONString(message));
                } else if (ObjectUtil.isNotNull(kafka.getKey())) {
                    return kafkaService.send(kafka.getTopic(), kafka.getKey(), JSON.toJSONString(message));
                } else {
                    return kafkaService.send(kafka.getTopic(), JSON.toJSONString(message));
                }
            }

            // rocketmq
            MqData.Rocketmq rocketmq = mqData.getRocketmq();
            if (ObjectUtil.isNotNull(rocketmq) && ObjectUtil.isNotNull(rocketmq.getEnable()) && rocketmq.getEnable()
                    && ObjectUtil.isNotNull(rocketMqService)) {
                return ObjectUtil.isNotNull(rocketmq.getIsEnableTag()) && rocketmq.getIsEnableTag() ?
                        rocketMqService.send(message, rocketmq.getTopic(), rocketmq.getTag()) :
                        rocketMqService.send(message, rocketmq.getTopic());
            }

            // mqtt
            MqData.Mqtt mqtt = mqData.getMqtt();
            if (ObjectUtil.isNotNull(mqtt) && ObjectUtil.isNotNull(mqtt.getEnable()) && mqtt.getEnable()
                    && ObjectUtil.isNotNull(mqttSendService)) {
                return mqttSendService.send(mqtt.getTopic(), mqtt.getQos(), message);
            }

            // rabbitmq
            MqData.Rabbitmq rabbitmq = mqData.getRabbitmq();
            if (ObjectUtil.isNotNull(rabbitmq) && ObjectUtil.isNotNull(rabbitmqService)) {
                if (ObjectUtil.isNotNull(rabbitmq.getEnable()) && rabbitmq.getEnable()) {
                    rabbitmqService.send(rabbitmq.getExchange(), rabbitmq.getRoutingKey(), message);
                    return null;
                }
            }
        }
        log.warn("{} mqData == null", message.getClass().getSimpleName());
        return null;
    }

    @Override
    public <T extends BaseMqMessage> Object getTemplate(Class<T> tClass) {
        MqData mqData = mqDataMap.get(StringUtils.uncapitalize(tClass.getSimpleName()));
        if (ObjectUtil.isNotNull(mqData) && ObjectUtil.isNotNull(mqData.getKafka())) {
            return kafkaService.getTemplate();
        }

        if (ObjectUtil.isNotNull(mqData) && ObjectUtil.isNotNull(mqData.getRocketmq())) {
            return rocketMqService.getTemplate();
        }
        return null;
    }
}
