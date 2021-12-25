package com.concise.mq;

import com.concise.component.mq.common.listener.MqListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-24 21:05
 */
@Component
//@Lazy
//@ConditionalOnMqListener(Demo1MqListener.class)
public class Demo3MqListener implements MqListener {
    private static final Logger log = LoggerFactory.getLogger(Demo3MqListener.class);

    @PostConstruct
    public void init() {
        log.info("init Demo3MqListener");
    }
    public Demo3MqListener() {
        System.out.println("----------------------------------+++++++++++++++++++Demo3MqListener");
    }

    public void say() {
        System.out.println("哈哈");
    }
}
