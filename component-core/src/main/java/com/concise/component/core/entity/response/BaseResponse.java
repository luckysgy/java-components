package com.concise.component.core.entity.response;

import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.entity.response.format.ResponseFormatHandler;

import com.concise.component.core.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shenguangyang
 * @date 2021-09-08 19:25
 */
public abstract class BaseResponse extends HashMap<String, Object> {
    protected static final Map<String, Object> EMPTY_OBJECT_DATA = new HashMap<>();
    protected static final List<Object> EMPTY_COLLECTION_DATA = new ArrayList<>();
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
    protected String successAttributeName;
    protected String messageAttributeName;
    protected String dataAttributeName;
    protected String pageDataAttributeName;
    /** 默认成功码 */
    private Integer defaultSuccessCodeAttributeValue;
    /** 默认错误码 */
    private Integer defaultErrorCodeAttributeValue;

    /**
     * 获取api响应格式
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
        response.setSuccessAttributeName(apiFormat.getSuccessAttributeName());
        response.setDataAttributeName(apiFormat.getDataAttributeName());
        response.setMessageAttributeName(apiFormat.getMessageAttributeName());
        response.setDefaultErrorCodeAttributeValue(apiFormat.getDefaultErrorCodeAttributeValue());
        response.setDefaultSuccessCodeAttributeValue(apiFormat.getDefaultSuccessCodeAttributeValue());
        response.setPageDataAttributeName(apiFormat.getPageDataAttributeName());
    }

    /**
     * 构建成功的响应
     */
    protected static <T extends BaseResponse> T buildSuccessResponse(T response, Integer code, Object data, String message) {
        ResponseFormatAbstract apiFormat = getApiFormat();
        setBuildSuccessCommonField(apiFormat, response, code, message);
        response.put(apiFormat.getDataAttributeName(), data);
        return response;
    }

    protected static <T extends BaseResponse> void setBuildSuccessCommonField(ResponseFormatAbstract apiFormat, T response, Integer code, String message) {
        response.put(apiFormat.getCodeAttributeName(), code == null ? apiFormat.getDefaultSuccessCodeAttributeValue() : code);
        if (StringUtils.isNotEmpty(message)) {
            response.put(apiFormat.getMessageAttributeName(), message);
        }
        initResponseTag(apiFormat, response);
        response.put(TRACE_ID, MDC.get(traceIdKey));
        response.put(SPAN_ID, MDC.get(spanIdKey));
        response.put(apiFormat.getSuccessAttributeName(), true);
    }

    /**
     * 构建失败的响应
     */
    protected static <T extends BaseResponse> T buildFailureResponse(T response, Integer errCode, String errMessage) {
        ResponseFormatAbstract apiFormat = getApiFormat();
        response.put(apiFormat.getCodeAttributeName(), errCode == null ? apiFormat.getDefaultErrorCodeAttributeValue() : errCode);
        response.put(apiFormat.getMessageAttributeName(), errMessage);
        response.put(apiFormat.getSuccessAttributeName(), false);
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

    public Integer getCode() {
        if (StringUtils.isNotEmpty(codeAttributeName)) {
            return (Integer) get(codeAttributeName);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return (Integer) get(apiFormat.getCodeAttributeName());
    }

    public Boolean isSuccess() {
        if (StringUtils.isNotNull(codeAttributeName)) {
            return get(codeAttributeName).equals(defaultSuccessCodeAttributeValue);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return get(apiFormat.getCodeAttributeName()).equals(apiFormat.getDefaultSuccessCodeAttributeValue());
    }

    public String getSuccessAttributeName() {
        return successAttributeName;
    }

    public void setSuccessAttributeName(String successAttributeName) {
        this.successAttributeName = successAttributeName;
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


    public Integer getDefaultSuccessCodeAttributeValue() {
        return defaultSuccessCodeAttributeValue;
    }

    public void setDefaultSuccessCodeAttributeValue(Integer defaultSuccessCodeAttributeValue) {
        this.defaultSuccessCodeAttributeValue = defaultSuccessCodeAttributeValue;
    }

    public Integer getDefaultErrorCodeAttributeValue() {
        return defaultErrorCodeAttributeValue;
    }

    public void setDefaultErrorCodeAttributeValue(Integer defaultErrorCodeAttributeValue) {
        this.defaultErrorCodeAttributeValue = defaultErrorCodeAttributeValue;
    }
}
