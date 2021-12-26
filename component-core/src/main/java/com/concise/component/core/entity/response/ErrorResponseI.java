package com.concise.component.core.entity.response;

/**
 * Extends your error codes in your App by implements this Interface.
 *
 * Created by shenguangyang on 2020/12/18.
 */
public interface ErrorResponseI {

    Integer getCode();

    String getMessage();
}