package com.concise.component.feign.aspect;

import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.ServletUtils;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.feign.annotation.Inner;
import com.concise.component.feign.config.ComponentFeignConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021/7/25 18:55
 */
@Aspect
@Component
public class InnerAspect implements Ordered {
    private static final Logger log = LoggerFactory.getLogger(InnerAspect.class);

    /**
     * 获取所有加了@Inner注解的方法或类，判断请求头中是否有我们规定的参数，如果没有，则不允许访问接口。
     * @param point 切面
     * @param inner 内网服务才能调用的接口注解
     * @return
     * @throws Throwable
     */
    @Around("@annotation(inner)")
    public Object around(ProceedingJoinPoint point, Inner inner) throws Throwable {
        String header = ServletUtils.getRequest().getHeader(ComponentFeignConstant.FROM);
        if (inner.value() && !StringUtils.equals(ComponentFeignConstant.FROM_IN, header)){
            log.warn("访问接口 {} 没有权限", point.getSignature().getName());
            throw new BizException("Access is denied");
        }
        return point.proceed();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
