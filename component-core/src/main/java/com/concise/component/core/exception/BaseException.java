package com.concise.component.core.exception;

/**
 * 基础异常
 * 
 * @author shenguangyang
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Long code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public BaseException() {
    }

    public BaseException(Long code, String message) {
        this.code = code;
        this.message = message;
    }


    public Long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
