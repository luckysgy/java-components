package com.concise.component.core.thread;

import com.concise.component.core.constant.ComponentCoreConstants;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求上下文
 * @author shenguangyang
 * @date 2021-10-09 20:26
 */
@Data
public class RequestContext {
    /**
     * api格式标签, 从请求头中获取
     * @see ComponentCoreConstants#API_RESPONSE_FORMAT_HEADER
     */
    private String apiFormatTag = "";
    /**
     * 扩展数据
     */
    private Map<String, Object> extendData = new ConcurrentHashMap<>();
}
