package com.concise.component.storage.common.storagetype;

import com.concise.component.storage.common.storagetype.StorageTypeCondition;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 存储类型条件判断器
 * 可以用在方法和类上
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(StorageTypeCondition.class)
public @interface ConditionalOnStorageType {
    /**
     * 存储类型
     */
    StorageTypesEnum type();
}
