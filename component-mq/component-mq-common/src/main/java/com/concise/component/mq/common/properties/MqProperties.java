package com.concise.component.mq.common.properties;

import cn.hutool.core.util.ObjectUtil;
import com.concise.component.core.exception.BizException;
import com.concise.component.mq.common.enable.MqEnable;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author shenguangyang
 * @date 2022-01-09 7:24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mq")
public class MqProperties {
    private static final Logger log = LoggerFactory.getLogger(MqProperties.class);
    /**
     * 使能的mq, 多个mq使用逗号分割
     */
    private String enableMq;
    /**
     * key: 消息名称
     */
    @NestedConfigurationProperty
    private Map<String, MqData> data;

    @PostConstruct
    public void init() {
        if (ObjectUtil.isNull(data)) {
            return;
        }
        for (Map.Entry<String, MqData> entry : data.entrySet()) {
            String enableMq = entry.getValue().getEnableMq();
            MqType.isSupported(enableMq);
            MqType mqType = MqType.getByType(enableMq);
            if (!MqEnable.isEnabled(mqType)) {
                throw new BizException("The enableMq [ " + enableMq + " ] specified in [ " + entry.getKey() + " ] is not enabled!");
            }
            entry.getValue().setEnableMqType(mqType);
        }
    }
}
