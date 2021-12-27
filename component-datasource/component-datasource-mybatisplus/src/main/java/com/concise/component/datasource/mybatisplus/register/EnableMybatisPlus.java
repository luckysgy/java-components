package com.concise.component.datasource.mybatisplus.register;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 使能mybatis plus
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EnableMybatisPlusRegister.class})
public @interface EnableMybatisPlus {
   boolean value() default true;
}
