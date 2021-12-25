package com.concise.component.storage.common.storagetype;

import lombok.Getter;

/**
 * @author shenguangyang
 * @date 2021-12-25 21:37
 */
public class StorageType {
    private static String userType;

    private StorageType() {

    }

    public static Boolean isUsed(StorageTypesEnum typesEnum) {
        return typesEnum.getType().equals(userType);
    }

    public static void create(String userType) {
        boolean isInclude = StorageTypesEnum.isInclude(userType);
        if (!isInclude) {
            throw new RuntimeException("user storageType [ " + userType + "] not supported, support " + StorageTypesEnum.getSupportType());
        }
        StorageType.userType = userType;
    }

    public static String getUserType() {
        return userType;
    }
}
