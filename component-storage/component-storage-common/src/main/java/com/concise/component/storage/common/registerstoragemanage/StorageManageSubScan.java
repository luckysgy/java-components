package com.concise.component.storage.common.registerstoragemanage;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描桶名子类
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({StorageManageSubScanRegister.class})
public @interface StorageManageSubScan {

}
