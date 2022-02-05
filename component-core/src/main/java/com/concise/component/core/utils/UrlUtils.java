package com.concise.component.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * url工具类
 * @author shenguangyang
 * @date 2021/7/17 19:45
 */
public class UrlUtils {
    private static final Logger log = LoggerFactory.getLogger(UrlUtils.class);
    /**
     * 移除url最后的斜杠
     * 比如
     *    输入 http://127.0.0.1:9000/
     *    输出 http://127.0.0.1:9000
     * @param url url
     */
    public static String removeLastSlash(String url) {
        if (StringUtils.isNull(url)) {
            log.warn("removeLastSlash::url = [{}]",url);
            return "";
        }
        boolean isExistLastSlash = url.endsWith("/");
        if (isExistLastSlash) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 添加url最后的斜杠
     * 比如
     *    输入 http://127.0.0.1:9000
     *    输出 http://127.0.0.1:9000/
     * @param url url
     */
    public static String addLastSlash(String url) {
        if (StringUtils.isNull(url)) {
            log.warn("removeLastSlash::url = [{}]",url);
            return "";
        }
        boolean isExistLastSlash = url.endsWith("/");
        if (!isExistLastSlash) {
            return url + "/";
        }
        return url;
    }
}
