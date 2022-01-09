package com.concise.component.mq.common.service;

import com.concise.component.mq.common.BaseMqMessage;

/**
 * @author shenguangyang
 * @date 2021-10-06 21:27
 */
public interface RabbitmqService {
    /**
     * 发送消息, 发送的消息会被转成json进行传输
     * @param exchange 指定交换机
     *        推荐定义成常量, eg:
     *        <code>
     *                  public static String EMAIL_EXCHANGE = "";

     *                  @Value("${rabbitmq.exchanges.email.name}")
     *                  public void setEmail(String email) {
     *                      MqConstant.EMAIL_EXCHANGE = email;
     *                  }
     *        </code>
     * @param routingKey 路由key, 同样推荐定义成常量, 直接定义即可不需要通过@Value注入值
     * @param object 发送的对象
     */
    <T extends BaseMqMessage> void send(String exchange, String routingKey, final T object);
}
