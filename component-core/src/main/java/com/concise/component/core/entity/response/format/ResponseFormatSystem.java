package com.concise.component.core.entity.response.format;

import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021/7/13 1:22
 */
@Component
public class ResponseFormatSystem extends ResponseFormatAbstract {
    @Override
    String codeAttributeName() {
        return "code";
    }

    @Override
    String successAttributeName() {
        return "success";
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
        return "data";
    }

    @Override
    Integer defaultSuccessCodeAttributeValue() {
        return 200;
    }

    @Override
    Integer defaultErrorCodeAttributeValue() {
        return 500;
    }

    @Override
    String tag() {
        return "system";
    }
}
