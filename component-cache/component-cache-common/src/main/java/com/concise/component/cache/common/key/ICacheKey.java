package com.concise.component.cache.common.key;

/**
 * 管理key的枚举需要实现该接口
 * @author shenguangyang
 * @date 2021/6/20 13:23
 * @deprecated {@link KeyManager}
 */
public interface ICacheKey {
    /** 缓存中的key */
    String getKey();
    /** 缓存中的hash key */
    String getHashKey();
    /** 超时时间 */
    long getTimeout();
}
