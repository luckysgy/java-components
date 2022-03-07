package com.concise.component.core.entity.response.format;

import com.concise.component.core.thread.RequestContext;
import com.concise.component.core.thread.RequestTtl;
import com.concise.component.core.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenguangyang
 * @date 2021/7/13 1:25
 */
@Component
public class ResponseFormatHandler {
    private static final Logger log = LoggerFactory.getLogger(ResponseFormatHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    private static final ResponseFormatAbstract DEFAULT_API_FORMAT = new ResponseFormatSystem();
    /** key = 类名  value = 类*/
    private static final Map<String, ResponseFormatAbstract> format = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Map<String, ResponseFormatAbstract> settings = applicationContext.getBeansOfType(ResponseFormatAbstract.class);
        settings.forEach((key,apiFormatAbstract) ->{
            ResponseFormatAbstract isExist = format.get(apiFormatAbstract.getTag());
            if (isExist != null) {
                throw new RuntimeException("ApiFormatAbstract 子类 tag要全局唯一,tag = " + isExist.getTag() + " 已存在");
            }
            format.put(apiFormatAbstract.getTag(), apiFormatAbstract);
        });
    }

    /**
     * 获取格式
     * @return
     */
    public static ResponseFormatAbstract getFormat() {
        RequestContext requestContext = RequestTtl.get();
        if (ObjectUtils.anyNull(requestContext)) {
            requestContext = new RequestContext();
            log.warn("AppContext is null, Please create this context in the interceptor");
        }
        String apiFormatTag = requestContext.getApiFormatTag();
        // return getFormat(request.getHeader(RequestConstants.API_RESPONSE_FORMAT_HEADER));
        return getFormat(apiFormatTag);
    }

    /**
     * 获取格式
     * @param headerApiFormatTag 请求
     * @return
     */
    public static ResponseFormatAbstract getFormat(String headerApiFormatTag) {
        if (StringUtils.isNull(headerApiFormatTag)) {
            return format.get(DEFAULT_API_FORMAT.getTag());
        }
        ResponseFormatAbstract apiFormatAbstract = format.get(headerApiFormatTag);
        if (apiFormatAbstract == null) {
            apiFormatAbstract = format.get(DEFAULT_API_FORMAT.getTag());
        }
        return apiFormatAbstract;
    }
}
