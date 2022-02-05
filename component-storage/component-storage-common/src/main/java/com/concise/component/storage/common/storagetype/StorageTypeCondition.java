package com.concise.component.storage.common.storagetype;

import com.concise.component.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 存储类型条件
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
public class StorageTypeCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(StorageTypeCondition.class);
    /** 是否已经注册过存储服务 */
    private static Boolean isRegistStorageService = false;
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnStorageType.class.getName());
        if (attributes == null) {
            log.warn("ConditionalOnStorageType 属性为空");
            return false;
        }
        //从配置文件中获取属性
        String storageType = conditionContext.getEnvironment().getProperty("storageServer.type");
        StorageEnableType.create(storageType);

        StorageTypesEnum storageTypesEnum = (StorageTypesEnum) attributes.get("type");
        String storageEnable = conditionContext.getEnvironment().getProperty("storageServer.enable");
        if (StringUtils.isNotNull(storageEnable) && "true".equals(storageEnable)) {
            if (StorageEnableType.isUsed(storageTypesEnum) && !isRegistStorageService) {
                log.info("成功注入 {} 服务" , storageTypesEnum.getType());
                isRegistStorageService = true;
                return true;
            }
        }
        return false;
    }
}
