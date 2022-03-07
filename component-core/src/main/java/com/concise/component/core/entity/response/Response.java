package com.concise.component.core.entity.response;

import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.utils.StringUtils;
import org.slf4j.MDC;


/**
 * 接口返回实体类
 * @author shenguangyang
 */
public class Response extends BaseResponse {
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "message";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public Response() {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public Response(ResponseFormatAbstract format, Integer code, String msg) {
        super.put(format.getCodeAttributeName(), code);
        if (StringUtils.isEmpty(msg)) {
            super.put(format.getMessageAttributeName(), "success");
        }
        super.put(TRACE_ID, MDC.get(traceIdKey));
        super.put(SPAN_ID, MDC.get(spanIdKey));
    }

    public static Response buildResult(int rows, String errMessage) {
        return rows > 0 ? buildSuccess() : buildFailure(errMessage);
    }

    public static Response buildResult(int rows) {
        return rows > 0 ? buildSuccess() : buildFailure();
    }

    public static Response buildResult(boolean result, String errMessage) {
        return result ? buildSuccess() : buildFailure(errMessage);
    }

    public static Response buildResult(boolean result) {
        return result ? buildSuccess() : buildFailure();
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static Response buildSuccess() {
        return Response.buildSuccess("操作成功");
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static Response buildSuccess(String msg) {
        ResponseFormatAbstract apiFormat =  getApiFormat();
        Response response = new Response(apiFormat, apiFormat.getDefaultSuccessCodeAttributeValue(), msg);
        initResponseTag(apiFormat, response);
        return response;
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static Response buildFailure() {
        return Response.buildFailure("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static Response buildFailure(String msg) {
        return Response.buildFailure(null, msg);
    }

    public static Response buildFailure(ErrorResponseI errorResponseI) {
        return Response.buildFailure(errorResponseI.getCode(), errorResponseI.getMessage());
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static Response buildFailure(Integer code, String msg) {
        ResponseFormatAbstract apiFormat = getApiFormat();
        Response response = new Response(apiFormat, code == null ? apiFormat.getDefaultErrorCodeAttributeValue() : code, msg);
        buildFailureResponse(response, code, msg);
        return response;
    }
}
