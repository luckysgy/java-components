package com.concise.component.lib.common.lib;

/**
 * 内部类的名称要求和库的名称一样, 不限制大小写, 但如果有下划线等特殊符号必须指明
 * 比如 opencv_core 可以定义成类名有: Opencv_Core / OPENCV_CORE / opencv_core 等
 * 目前只支持加载包的根路径下库文件, 不支持加载二级以及以上的库文件
 * @author shenguangyang
 * @date 2021-12-05 7:51
 */
public class LibHik {
    public static class HCNetSDK extends LibPath {
        public static String path = getPath(LibEnums.HIK, HCNetSDK.class);
    }
}
