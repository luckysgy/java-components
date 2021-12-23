package com.concise.component.core.entity.response;

import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.entity.response.format.ResponseFormatHandler;

import com.concise.component.core.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;

import java.util.HashMap;

/**
 * @author shenguangyang
 * @date 2021-09-08 19:25
 */
public abstract class BaseResponse extends HashMap<String, Object> {
    /**
     * 日志链路跟踪标识,表示当前请求
     */
    protected static final String TRACE_ID = "traceId";
    /**
     * 日志链路标识.spanId标识的是子线程
     */
    protected static final String SPAN_ID = "spanId";

    protected static String traceIdKey = "default";
    protected static String spanIdKey = "default";

    protected String codeAttributeName;
    protected String messageAttributeName;
    protected String dataAttributeName;
    protected String pageDataAttributeName;
    /** 默认成功码 */
    private Number defaultSuccessCodeAttributeValue;
    /** 默认错误码 */
    private Number defaultErrorCodeAttributeValue;

    /**
     * 获取api响应格式
     * @return
     */
    public static ResponseFormatAbstract getApiFormat() {
        return ResponseFormatHandler.getFormat();
    }

    /**
     * 初始化响应标签
     * @param response 响应体
     */
    protected static <T extends BaseResponse> void initResponseTag(ResponseFormatAbstract apiFormat, T response) {
        response.setCodeAttributeName(apiFormat.getCodeAttributeName());
        response.setDataAttributeName(apiFormat.getDataAttributeName());
        response.setMessageAttributeName(apiFormat.getMessageAttributeName());
        response.setDefaultErrorCodeAttributeValue(apiFormat.getDefaultErrorCodeAttributeValue());
        response.setDefaultSuccessCodeAttributeValue(apiFormat.getDefaultSuccessCodeAttributeValue());
        response.setPageDataAttributeName(apiFormat.getPageDataAttributeName());
    }

    /**
     * 构建成功的响应
     */
    protected static <T extends BaseResponse> T buildSuccessResponse(T response, Number code, Object data, String message) {
        ResponseFormatAbstract apiFormat = getApiFormat();
        if (ObjectUtils.isNotEmpty(data)) {
            response.put(apiFormat.getDataAttributeName(), data);
        }
        setBuildSuccessCommonField(apiFormat, response, code, message);
        return response;
    }

    protected static <T extends BaseResponse> void setBuildSuccessCommonField(ResponseFormatAbstract apiFormat, T response, Number code, String message) {
        response.put(apiFormat.getCodeAttributeName(), code == null ? apiFormat.getDefaultSuccessCodeAttributeValue() : code);
        if (StringUtils.isNotEmpty(message)) {
            response.put(apiFormat.getMessageAttributeName(), message);
        }
        initResponseTag(apiFormat, response);
        response.put(TRACE_ID, MDC.get(traceIdKey));
        response.put(SPAN_ID, MDC.get(spanIdKey));
    }

    /**
     * 构建失败的响应
     */
    protected static <T extends BaseResponse> T buildFailureResponse(T response, Number errCode, String errMessage) {
        ResponseFormatAbstract apiFormat = getApiFormat();
        response.put(apiFormat.getCodeAttributeName(), errCode == null ? apiFormat.getDefaultErrorCodeAttributeValue() : errCode);
        response.put(apiFormat.getMessageAttributeName(), errMessage);
        initResponseTag(apiFormat, response);
        response.put(TRACE_ID, MDC.get(traceIdKey));
        response.put(SPAN_ID, MDC.get(spanIdKey));
        return response;
    }

    public static void setTraceIdKey(String traceIdKey) {
        Response.traceIdKey = traceIdKey;
    }

    public static void setSpanIdKey(String spanIdKey) {
        Response.spanIdKey = spanIdKey;
    }

    public String getMessage() {
        if (StringUtils.isNotNull(messageAttributeName)) {
            return (String) get(messageAttributeName);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return (String) get(apiFormat.getMessageAttributeName());
    }

    public Number getCode() {
        if (StringUtils.isNotEmpty(codeAttributeName)) {
            return (Number) get(codeAttributeName);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return (Number) get(apiFormat.getCodeAttributeName());
    }

    public Boolean isSuccess() {
        if (StringUtils.isNotNull(codeAttributeName)) {
            return get(codeAttributeName).equals(defaultSuccessCodeAttributeValue);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return get(apiFormat.getCodeAttributeName()).equals(apiFormat.getDefaultSuccessCodeAttributeValue());
    }

    public String getCodeAttributeName() {
        return codeAttributeName;
    }

    public void setCodeAttributeName(String codeAttributeName) {
        this.codeAttributeName = codeAttributeName;
    }

    public String getMessageAttributeName() {
        return messageAttributeName;
    }

    public void setMessageAttributeName(String messageAttributeName) {
        this.messageAttributeName = messageAttributeName;
    }

    public String getDataAttributeName() {
        return dataAttributeName;
    }

    public void setPageDataAttributeName(String pageDataAttributeName) {
        this.pageDataAttributeName = pageDataAttributeName;
    }

    public void setDataAttributeName(String dataAttributeName) {
        this.dataAttributeName = dataAttributeName;
    }


    public Number getDefaultSuccessCodeAttributeValue() {
        return defaultSuccessCodeAttributeValue;
    }

    public void setDefaultSuccessCodeAttributeValue(Number defaultSuccessCodeAttributeValue) {
        this.defaultSuccessCodeAttributeValue = defaultSuccessCodeAttributeValue;
    }

    public Number getDefaultErrorCodeAttributeValue() {
        return defaultErrorCodeAttributeValue;
    }

    public void setDefaultErrorCodeAttributeValue(Number defaultErrorCodeAttributeValue) {
        this.defaultErrorCodeAttributeValue = defaultErrorCodeAttributeValue;
    }
}
