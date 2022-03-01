package com.concise.component.core.entity.response;
import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.utils.StringUtils;

import java.util.Collection;

/**
 * Response with batch record to return,
 * @author shenguangyang
 * @date 2021-09-08 19:49
 */
public class MultiResponse<T> extends BaseResponse {

    public Collection<T> getData() {
        if (StringUtils.isNotNull(dataAttributeName)) {
            return (Collection<T>) get(dataAttributeName);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return (Collection<T>) get(apiFormat.getDataAttributeName());
    }

    public static <T> MultiResponse<T> buildSuccess(Integer code, String message, Collection<T> data) {
        MultiResponse<T> response = new MultiResponse<>();
        if (data == null) {
            return buildSuccessResponse(response, code, EMPTY_COLLECTION_DATA, message);
        }
        return buildSuccessResponse(response, code, data, message);
    }

    public static <T> MultiResponse<T> buildResult(boolean result, Collection<T> data, ErrorResponseI errorResponseI) {
        return result ? buildSuccess(data) : buildFailure(errorResponseI);
    }

    public static <T> MultiResponse<T> buildSuccess(String message, Collection<T> data) {
        return buildSuccess(null, message, data);
    }

    public static <T> MultiResponse<T> buildSuccess(Collection<T> data) {
        return buildSuccess(null, "success", data);
    }


    public static <T> MultiResponse<T> buildFailure(Integer errCode, String errMessage) {
        MultiResponse<T> response = new MultiResponse<>();
        buildFailureResponse(response, errCode, errMessage);
        response.put(getApiFormat().getDataAttributeName(), EMPTY_COLLECTION_DATA);
        return response;
    }

    public static <T> MultiResponse<T> buildFailure(String errMessage) {
        return buildFailure(null, errMessage);
    }

    public static <T> MultiResponse<T> buildFailure(ErrorResponseI errorResponseI) {
        return buildFailure(errorResponseI.getCode(), errorResponseI.getMessage());
    }
}
