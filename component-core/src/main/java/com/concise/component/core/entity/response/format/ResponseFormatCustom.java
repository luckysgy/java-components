package com.concise.component.core.entity.response.format;

/**
 * @author shenguangyang
 * @date 2021/7/13 1:22
 */
public class ResponseFormatCustom extends ResponseFormatAbstract {

    @Override
    String codeAttributeName() {
        return "errCode";
    }

    @Override
    String messageAttributeName() {
        return "errMsg";
    }

    @Override
    String dataAttributeName() {
        return "info";
    }

    @Override
    String pageDataAttributeName() {
        return "data";
    }

    @Override
    Long defaultSuccessCodeAttributeValue() {
        return 0L;
    }

    @Override
    Long defaultErrorCodeAttributeValue() {
        return 500L;
    }

    @Override
    String tag() {
        return "custom";
    }
}
