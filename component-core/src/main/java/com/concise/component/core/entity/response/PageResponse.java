package com.concise.component.core.entity.response;

import com.concise.component.core.entity.response.format.ResponseFormatAbstract;
import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.StringUtils;

import java.util.Collection;

/**
 * 表格分页数据对象
 * Response with batch page record to return,
 * usually use in page query
 * @author shenguangyang
 */
public class PageResponse<T> extends BaseResponse {
    private static final long serialVersionUID = 1L;

    public PageResponseData<T> getData() {
        if (StringUtils.isNotNull(pageDataAttributeName)) {
            Object obj = get(pageDataAttributeName);
            if (!(obj instanceof PageResponseData)) {
                throw new BizException("获取PageResponseData失败");
            }
            return (PageResponseData<T>) obj;
        }
        ResponseFormatAbstract apiFormat = getApiFormat();
        Object obj = get(apiFormat.getDataAttributeName());
        if (!(obj instanceof PageResponseData)) {
            throw new BizException("获取PageResponseData失败");
        }
        return (PageResponseData<T>) obj;
    }

    /**
     * 表格数据对象
     */
    private PageResponse() {
    }

    public static <T> PageResponse<T> buildFailure(Integer errCode, String errMessage) {
        ResponseFormatAbstract apiFormat = getApiFormat();
        PageResponse<T> response = new PageResponse<>();
        PageResponseData<T> pageResponseData = new PageResponseData<>();
        response.put(apiFormat.getPageDataAttributeName(), pageResponseData);
        return buildFailureResponse(response, errCode, errMessage);
    }

    public static <T> PageResponse<T> buildFailure(ErrorResponseI errorResponseI) {
        return buildFailure(errorResponseI.getCode(), errorResponseI.getMessage());
    }

    public static <T> PageResponse<T> buildSuccess(Integer code, String message, Collection<T> data, long total, Integer pageSize, Integer pageNum) {
        PageResponse<T> response = new PageResponse<>();
        PageResponseData<T> pageResponseData = new PageResponseData<>();
        pageResponseData.setPageNum(pageNum);
        pageResponseData.setList(data);
        pageResponseData.setPageSize(pageSize);
        pageResponseData.setTotal(total);
        ResponseFormatAbstract apiFormat = getApiFormat();
        response.put(apiFormat.getPageDataAttributeName(), pageResponseData);
        setBuildSuccessCommonField(apiFormat, response, code, message);
        return response;
    }

    public static <T> PageResponse<T> buildSuccess(String message, Collection<T> data, long total, Integer pageSize, Integer pageNum) {
        return buildSuccess(null, message, data, total, pageSize, pageNum);
    }

    public static <T> PageResponse<T> buildSuccess(Collection<T> data, long total, Integer pageSize, Integer pageNum) {
        return buildSuccess(null, null, data, total, pageSize, pageNum);
    }
}