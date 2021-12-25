package com.concise.mq.controller;

import com.concise.component.core.utils.UUIDUtil;
import com.concise.component.mq.rocketmq.service.RocketMqSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenguangyang
 * @date 2021-12-25 12:17
 */
@RestController
@RequestMapping("/mq/send")
public class MqController {
    @Autowired
    private RocketMqSendService rocketMqSendService;

    @GetMapping("/demo1")
    public String demo1() {
        rocketMqSendService.send(UUIDUtil.uuid(), "demo1", "demo1", "demo1");
        return "ok";
    }

    @GetMapping("/demo2")
    public String demo2() {
        rocketMqSendService.send(UUIDUtil.uuid(), "demo2", "demo2", "demo2");
        return "ok";
    }
}
