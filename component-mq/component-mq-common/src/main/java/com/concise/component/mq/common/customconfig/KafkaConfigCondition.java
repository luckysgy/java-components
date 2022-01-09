package com.concise.component.mq.common.customconfig;

import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.enable.MqEnable;
import com.concise.component.mq.common.properties.MqType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author shenguangyang
 * @date 2022-01-09 20:16
 */
public class KafkaConfigCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String enableMq = conditionContext.getEnvironment().getProperty(MqEnable.ENABLE_MQ_KEY);
        if (StringUtils.isNotNull(enableMq)) {
            if (enableMq.contains(MqType.KAFKA.getType())) {
                return true;
            }
        }
        return false;
    }
}
