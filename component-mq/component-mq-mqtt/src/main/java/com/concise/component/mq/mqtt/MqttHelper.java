package com.concise.component.mq.mqtt;

import cn.hutool.core.util.ObjectUtil;
import com.concise.component.core.exception.BizException;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;

/**
 * @author shenguangyang
 * @date 2022-01-09 12:20
 */
public class MqttHelper {
    public static MqttDeliveryToken toMqttDeliveryToken(Object sendResult) {
        if (ObjectUtil.isNotNull(sendResult)) {
            if (sendResult instanceof MqttDeliveryToken) {
                return (MqttDeliveryToken) sendResult;
            }
        }
        throw new BizException("toMqttDeliveryToken fail");
    }
}
