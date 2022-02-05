package com.concise.component.storage.common.storagetype;

/**
 * @author shenguangyang
 * @date 2021-12-25 21:37
 */
public class StorageEnableType {
    private static String userType;

    private StorageEnableType() {

    }

    public static Boolean isUsed(StorageTypesEnum typesEnum) {
        return typesEnum.getType().equals(userType);
    }

    public static void create(String userType) {
        boolean isInclude = StorageTypesEnum.isInclude(userType);
        if (!isInclude) {
            throw new RuntimeException("user storageType [ " + userType + "] not supported, support " + StorageTypesEnum.getSupportType());
        }
        StorageEnableType.userType = userType;
    }

    public static String getUserType() {
        return userType;
    }
}
