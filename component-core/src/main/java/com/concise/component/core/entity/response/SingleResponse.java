package com.concise.component.core.entity.response;


import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.utils.StringUtils;

/**
 * Response with single record to return
 * @author shenguangyang
 * @date 2021-09-08 19:20
 */

public class SingleResponse<T> extends BaseResponse {
    private static final long serialVersionUID = 1L;

    public T getData() {
        if (StringUtils.isNotNull(dataAttributeName)) {
            return (T) get(dataAttributeName);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return (T) get(apiFormat.getDataAttributeName());
    }

    public static <T> SingleResponse<T> buildSuccess(Integer code, String message, T data) {
        SingleResponse<T> response = new SingleResponse<>();
        return buildSuccessResponse(response, code, data, message);
    }

    public static <T> SingleResponse<T> buildResult(int rows, T data, String errMessage) {
        return rows > 0 ? buildSuccess(data) : buildFailure(errMessage);
    }

    public static <T> SingleResponse<T> buildResult(boolean result, T data,  String errMessage) {
        return result ? buildSuccess(data) : buildFailure(errMessage);
    }

    public static <T> SingleResponse<T> buildResult(boolean result, T data) {
        return result ? buildSuccess(data) : buildFailure(BaseErrorResponse.FAILD);
    }

    public static <T> SingleResponse<T> buildResult(boolean result, T data, ErrorResponseI errorResponseI) {
        return result ? buildSuccess(data) : buildFailure(errorResponseI);
    }

    public static <T> SingleResponse<T> buildSuccess(String message, T data) {
        return buildSuccess(null, message, data);
    }

    public static <T> SingleResponse<T> buildSuccess(T data) {
        return buildSuccess(null, null, data);
    }


    public static <T> SingleResponse<T> buildFailure(Integer errCode, String errMessage) {
        SingleResponse<T> response = new SingleResponse<>();
        return buildFailureResponse(response, errCode, errMessage);
    }

    public static <T> SingleResponse<T> buildFailure(String errMessage) {
        return buildFailure(null, errMessage);
    }

    public static <T> SingleResponse<T> buildFailure(ErrorResponseI errorResponseI) {
        return buildFailure(errorResponseI.getCode(), errorResponseI.getMessage());
    }
}
