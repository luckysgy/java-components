package com.concise.component.core.entity.response.format;

import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021/7/13 1:22
 */
@Component
public class ResponseFormatCustom extends ResponseFormatAbstract {

    @Override
    String codeAttributeName() {
        return "errCode";
    }

    @Override
    String successAttributeName() {
        return "success";
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
    Integer defaultSuccessCodeAttributeValue() {
        return 0;
    }

    @Override
    Integer defaultErrorCodeAttributeValue() {
        return 500;
    }

    @Override
    String tag() {
        return "custom";
    }
}
