package com.concise.component.mq.rocketmq;

import cn.hutool.core.util.ObjectUtil;
import com.concise.component.core.exception.BizException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * @author shenguangyang
 * @date 2022-01-09 12:22
 */
public class RocketmqHelper {
    public static SendResult toSendResult(Object sendResult) {
        if (ObjectUtil.isNotNull(sendResult)) {
            if (sendResult instanceof SendResult) {
                return (SendResult) sendResult;
            }
        }
        throw new BizException("toSendResult fail");
    }

    public static RocketMQTemplate toRocketMQTemplate(Object template) {
        if (ObjectUtil.isNotNull(template)) {
            if (template instanceof RocketMQTemplate) {
                return (RocketMQTemplate) template;
            }
        }
        throw new BizException("toRocketMQTemplate fail");
    }
}
