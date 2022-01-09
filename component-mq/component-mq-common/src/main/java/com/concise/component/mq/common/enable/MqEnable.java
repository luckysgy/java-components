package com.concise.component.mq.common.enable;

import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.properties.MqType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenguangyang
 * @date 2022-01-09 8:48
 */
@Component
public class MqEnable {
    private static final Logger log = LoggerFactory.getLogger(MqEnable.class);
    private static final String EMPTY_STR = "";
    private static final Map<MqType, String> ENABLE_MQ_TYPE = new ConcurrentHashMap<>();
    public static final String ENABLE_MQ_KEY = "mq.enableMq";
    /**
     * 多个mq之间使用逗号隔开
     * @param enableMqStr
     */
    public static void addEnableMq(String enableMqStr) {
        if (StringUtils.isNotEmpty(enableMqStr)) {
            enableMqStr = enableMqStr.trim();
            String[] split = enableMqStr.split(",");
            for (String enableMqType : split) {
                MqType.isSupported(enableMqType);
                log.info("enable mq: {}", enableMqType);
                ENABLE_MQ_TYPE.put(MqType.getByType(enableMqType), EMPTY_STR);
            }
            return;
        }
        log.warn("not enable any mq");
    }

    public static boolean isEnabled(MqType mqType) {
        for (Map.Entry<MqType, String> entry : ENABLE_MQ_TYPE.entrySet()) {
            if (entry.getKey().getType().equals(mqType.getType())) {
                return true;
            }
        }
        return false;
    }

}
