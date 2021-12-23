package com.concise.component.log.utils;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Description:
 *
 * @author shenguangyang
 * @date 2021/04/21
 */
public class MDCSpanIdUtils {
    /**
     * 追踪id的名称
     */
    public static final String KEY_SPAN_ID = "spanId";

    /**
     * 日志链路追踪id信息头
     */
    public static final String SPAN_ID_HEADER = "x-spanId-header";

    /**
     * filter的优先级，值越低越优先
     */
    public static final int FILTER_ORDER = -1;

    /**
     * 创建SPANId并赋值MDC
     */
    public static void addSpanId() {
        MDC.put(KEY_SPAN_ID, createSpanId());
    }

    /**
     * 赋值MDC
     */
    public static void putSpanId(String spanId) {
        MDC.put(KEY_SPAN_ID, spanId);
    }

    /**
     * 获取MDC中的SPANId值
     */
    public static String getSpanId() {
        return MDC.get(KEY_SPAN_ID);
    }

    /**
     * 清除MDC的值
     */
    public static void removeSpanId() {
        MDC.remove(KEY_SPAN_ID);
    }

    /**
     * 创建SPANId
     */
    public static String createSpanId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
