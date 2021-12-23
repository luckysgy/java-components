package com.concise.component.cache.common.key;

/**
 * 构建key管理
 * @author shenguangyang
 * @date 2021/6/20 13:21
 * @deprecated {@link KeyManager}
 */
public class CacheKey {
    private String key;
    private String hashKey;
    private long timeout;

    private ICacheKey iCacheKey;

    private CacheKey() {

    }

    public static CacheKey build(ICacheKey iCacheKey) {
        CacheKey cacheKey = new CacheKey();
        cacheKey.iCacheKey = iCacheKey;
        return cacheKey;
    }

    public CacheKey key(Object... keys) {
        String key = this.iCacheKey.getKey();
        this.key = String.format(key,keys);
        this.timeout = this.iCacheKey.getTimeout();
        this.hashKey = "";
        return this;
    }

    public CacheKey hashKey(Object... hashKeys) {
        String hashKey = this.iCacheKey.getHashKey();
        this.timeout = this.iCacheKey.getTimeout();
        this.hashKey = String.format(hashKey,hashKeys);
        return this;
    }

    public String getKey() {
        return key;
    }

    public String getHashKey() {
        return hashKey;
    }

    public long getTimeout() {
        return timeout;
    }

}
