package com.concise.component.core.entity.response.format;

import com.concise.component.core.constant.ComponentCoreConstants;

/**
 * 兼容多种api返回结果
 *
 * 使用方式: 请求头中需要携带 {@link ComponentCoreConstants#API_RESPONSE_FORMAT_HEADER} 字段, 值就是
 * {@link ResponseFormatAbstract#getTag()} , 比如指定 tag = system, 则响应结果使用 {@link ResponseFormatSystem},
 * 如果请求头中没有携带指定字段或者指定字段内容为空, 则使用默认返回结果 {@link ResponseFormatSystem}
 *
 * {@link ResponseFormatSystem} 是系统默认返回结果
 * @author shenguangyang
 * @date 2021/7/13 1:14
 */
public abstract class ResponseFormatAbstract {
    /** 值为返回给前端的json对应的状态码属性名 */
    abstract String codeAttributeName();
    /** 值为返回给前端的json对应的消息属性名 */
    abstract String messageAttributeName();
    /** 值为返回给前端的json对应的数据属性名 */
    abstract String dataAttributeName();
    /** 分页数据对应的数据属性名 */
    abstract String pageDataAttributeName();
    /** 成功状态码 */
    abstract Long defaultSuccessCodeAttributeValue();
    /** 失败状态码 */
    abstract Long defaultErrorCodeAttributeValue();

    /** 标签 全局唯一 */
    abstract String tag();

    public String getCodeAttributeName() {
        return this.codeAttributeName();
    }

    public String getMessageAttributeName() {
        return this.messageAttributeName();
    }

    public String getDataAttributeName() {
        return this.dataAttributeName();
    }

    public String getPageDataAttributeName() {
        return this.pageDataAttributeName();
    }

    public Long getDefaultSuccessCodeAttributeValue() {
        return this.defaultSuccessCodeAttributeValue();
    }

    public Long getDefaultErrorCodeAttributeValue() {
        return this.defaultErrorCodeAttributeValue();
    }

    public String getTag() {
        return this.tag();
    }

}
