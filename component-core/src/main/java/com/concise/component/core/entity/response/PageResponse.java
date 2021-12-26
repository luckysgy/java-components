package com.concise.component.core.entity.response;

import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

/**
 * 表格分页数据对象
 * Response with batch page record to return,
 * usually use in page query
 * @author shenguangyang
 */
public class PageResponse<T> extends BaseResponse {
    private static final long serialVersionUID = 1L;
    /** 总记录数 */
    private static final String totalAttributeName = "total";

    public Collection<T> getData() {
        if (StringUtils.isNotNull(pageDataAttributeName)) {
            return (Collection<T>) get(pageDataAttributeName);
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        return (Collection<T>) get(apiFormat.getDataAttributeName());
    }

    public void setData(Collection<T> data) {
        put(pageDataAttributeName, data);
    }

    /**
     * 表格数据对象
     */
    private PageResponse() {
    }

    public static <T> PageResponse<T> buildFailure(Integer errCode, String errMessage) {
        PageResponse<T> response = new PageResponse<>();
        response.put(totalAttributeName, 0);
        return buildFailureResponse(response, errCode, errMessage);
    }

    public static <T> PageResponse<T> buildFailure(ErrorResponseI errorResponseI) {
        return buildFailure(errorResponseI.getCode(), errorResponseI.getMessage());
    }

    public static <T> PageResponse<T> buildSuccess(Integer code, String message, Collection<T> data, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.put(totalAttributeName, total);
        ResponseFormatAbstract apiFormat = getApiFormat();
        if (ObjectUtils.isNotEmpty(data)) {
            response.put(apiFormat.getPageDataAttributeName(), data);
        }
        setBuildSuccessCommonField(apiFormat, response, code, message);
        return response;
    }

    public static <T> PageResponse<T> buildSuccess(String message, Collection<T> data, long total) {
        return buildSuccess(null, message, data, total);
    }

    public static <T> PageResponse<T> buildSuccess(Collection<T> data, long total) {
        return buildSuccess(null, null, data, total);
    }

    public static <T> PageResponse<T> buildSuccess(Collection<T> data) {
        return buildSuccess(null, null, data, 0);
    }

    public long getTotal() {
        return (long) get(totalAttributeName);
    }

    public void setTotal(long total) {
        put(totalAttributeName, total);
    }




}