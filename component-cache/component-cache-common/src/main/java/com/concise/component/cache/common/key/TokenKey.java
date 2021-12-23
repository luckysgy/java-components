package com.concise.component.cache.common.key;

/**
 * @author shenguangyang
 * @date 2021/8/1 7:12
 */
public class TokenKey extends BaseKeyManager {
    public TokenKey(int expireSeconds, String templateKey) {
        super(expireSeconds, templateKey);
    }

    /**
     * 获取token
     */
    public static final TokenKey getToken = new TokenKey(60*60*24*7, "token:%s");
}
