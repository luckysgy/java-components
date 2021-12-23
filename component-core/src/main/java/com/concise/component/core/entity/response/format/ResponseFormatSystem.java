package com.concise.component.core.entity.response.format;

/**
 * @author shenguangyang
 * @date 2021/7/13 1:22
 */
public class ResponseFormatSystem extends ResponseFormatAbstract {
    @Override
    String codeAttributeName() {
        return "code";
    }

    @Override
    String messageAttributeName() {
        return "message";
    }

    @Override
    String dataAttributeName() {
        return "data";
    }

    @Override
    String pageDataAttributeName() {
        return "rows";
    }

    @Override
    Long defaultSuccessCodeAttributeValue() {
        return 200L;
    }

    @Override
    Long defaultErrorCodeAttributeValue() {
        return 500L;
    }

    @Override
    String tag() {
        return "system";
    }
}
