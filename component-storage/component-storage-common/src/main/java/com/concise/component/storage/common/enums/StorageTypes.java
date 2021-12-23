package com.concise.component.storage.common.enums;

import lombok.Getter;

/**
 * 存储类型
 * @author shenguangyang
 * @date 2021/7/17 13:46
 */
@Getter
public enum StorageTypes {
    MINIO("minio"),
    OSS("oss");
    private final String type;

    StorageTypes(String type) {
        this.type = type;
    }
}
