package com.concise.component.core.exception;

import com.concise.component.core.entity.response.ErrorResponseI;

/**
 * 自定义异常
 * 
 * @author shenguangyang
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Number code;

    private String message;

    public BizException(String message) {
        this.message = message;
    }

    public BizException(ErrorResponseI errorResponse) {
        this.message = errorResponse.getMessage();
        this.code = errorResponse.getCode();
    }

    public BizException(Long code, String message) {
        this.message = message;
        this.code = code;
    }

    public BizException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public Number getCode()
    {
        return code;
    }
}
