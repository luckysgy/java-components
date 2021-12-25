package com.concise.component.storage.common.storagetype;

import lombok.Getter;

/**
 * 存储类型
 * @author shenguangyang
 * @date 2021/7/17 13:46
 */
@Getter
public enum StorageTypesEnum {
    MINIO("minio"),
    OSS("oss");
    private final String type;

    StorageTypesEnum(String type) {
        this.type = type;
    }

    public static boolean isInclude(String userType) {
        for (StorageTypesEnum value : StorageTypesEnum.values()) {
            if (value.getType().equals(userType)) {
                return true;
            }
        }
        return false;
    }

    public static String getSupportType() {
        StringBuilder supportType = new StringBuilder("[ ");
        for (StorageTypesEnum value : StorageTypesEnum.values()) {
            supportType = new StringBuilder(value + "" + supportType + " ]");
        }
        return supportType.toString();
    }
}
