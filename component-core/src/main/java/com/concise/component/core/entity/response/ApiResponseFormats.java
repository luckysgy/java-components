package com.concise.component.core.entity.response;

import lombok.Getter;

/**
 * @author shenguangyang
 * @date 2021/7/12 23:52
 */
@Getter
public enum ApiResponseFormats {
    DEFAULT("code", "success", "message","data"),
    CUSTOM("errCode","success", "errMsg","info");

    /** 值为返回给前端的json对应的状态码属性名 */
    private final String code;
    /** 值为返回给前端的json对应的success属性名 */
    private final String success;
    /** 值为返回给前端的json对应的消息属性名 */
    private final String message;
    /** 值为返回给前端的json对应的数据属性名 */
    private final String data;

    ApiResponseFormats(String code, String success, String message, String data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
