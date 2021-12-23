package com.concise.component.log;

import com.concise.component.core.entity.response.Response;
import com.concise.component.log.utils.MDCSpanIdUtils;
import com.concise.component.log.utils.MDCTraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 22:06
 */
@ComponentScan(basePackages = "com.concise.component.log")
public class LogMainConfig {
    private static final Logger log = LoggerFactory.getLogger(LogMainConfig.class);
    @PostConstruct
    public void init() {
        Response.setTraceIdKey(MDCTraceUtils.KEY_TRACE_ID);
        Response.setSpanIdKey(MDCSpanIdUtils.KEY_SPAN_ID);
        log.info("init com.concise.component.log");
    }
}
