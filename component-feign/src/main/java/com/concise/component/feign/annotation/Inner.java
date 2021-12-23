package com.concise.component.feign.annotation;

import java.lang.annotation.*;

/**
 * 将该注解标注在只有内部服务才能调用的接口方法上
 *
 * 假如现在我们有三个服务：分别是用户服务、订单服务和产品服务。用户如果购买产品，则需要调用产品服务生成订单，
 * 那么我们在这个调用过程中有必要鉴权吗？答案是否定的，因为这些资源服务放在内网环境中，完全不用考虑安全问题。
 * @author shenguangyang
 * @date 2021/7/25 18:53
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {
    /**
     * 是否AOP统一处理
     */
    boolean value() default true;
}
