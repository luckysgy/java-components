package com.concise.component.feign.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 将所有注解了@Inner的方法和类暴露出来，允许不鉴权可以访问，这里需要注意的点是如果方法使用pathVariable
 * 传参的，则需要将这个参数转换为*。如果不转换，当成接口的访问路径，则找不到此接口。
 * @author shenguangyang
 * @date 2021/7/25 19:04
 */
public class PermitAllUrlConfig implements InitializingBean, ApplicationContextAware {
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private ApplicationContext applicationContext;

    private List<String> urls = new ArrayList<>();

    public static final String ASTERISK = "*";

    @Override
    public void afterPropertiesSet() {
//        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
//
//        map.keySet().forEach(info -> {
//            HandlerMethod handlerMethod = map.get(info);
//
//            // 获取方法上边的注解 替代path variable 为 *
//            Inner method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Inner.class);
//            Optional.ofNullable(method).ifPresent(inner -> info.getPatternsCondition().getPatterns()
//                    .forEach(url -> urls.add(ReUtil.replaceAll(url, PATTERN, ASTERISK))));
//
//            // 获取类上边的注解, 替代path variable 为 *
//            Inner controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Inner.class);
//            Optional.ofNullable(controller).ifPresent(inner -> info.getPatternsCondition().getPatterns()
//                    .forEach(url -> urls.add(ReUtil.replaceAll(url, PATTERN, ASTERISK))));
//        });
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
