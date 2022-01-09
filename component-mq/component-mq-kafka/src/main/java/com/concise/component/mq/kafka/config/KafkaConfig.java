package com.concise.component.mq.kafka.config;

import cn.hutool.core.util.ObjectUtil;
import com.concise.component.mq.common.properties.MqData;
import com.concise.component.mq.common.properties.MqProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author shenguangyang
 * @date 2022-01-09 7:43
 */
@Configuration
public class KafkaConfig {
    @Autowired
    private KafkaProperties properties;
    @Autowired
    private MqProperties mqProperties;

    /**
     * 有时候我们在程序启动时并不知道某个Topic需要多少Partition数合适，但是又不能一股脑的直接使用Broker的默认设置，
     * 这个时候就需要使用Kafka-Client自带的AdminClient来进行处理。
     */
    @PostConstruct
    public void init() {
        AdminClient client = AdminClient.create(properties.buildAdminProperties());
        if(client !=null){
            try {
                Collection<NewTopic> newTopics = new ArrayList<>();
                for (Map.Entry<String, MqData> entry : mqProperties.getData().entrySet()) {
                    MqData.Kafka kafka = entry.getValue().getKafka();
                    if (ObjectUtil.isNotNull(kafka)) {
                        newTopics.add(new NewTopic(kafka.getTopic(),1,(short) 1));
                    }
                }
                client.createTopics(newTopics);
            }catch (Throwable e){
                e.printStackTrace();
            }finally {
                client.close();
            }
        }
    }
}
