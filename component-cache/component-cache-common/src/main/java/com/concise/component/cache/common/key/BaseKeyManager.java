package com.concise.component.cache.common.key;

import java.util.StringJoiner;

/**
 * 所有管理key前缀对象都继承该类，并创建子类实现key前缀管理
 * <code>
 *   public class SeckillKey extends BaseKeyManager {
 *
 *     public SeckillKey(int expireSeconds, String templateKey) {
 *         super(expireSeconds, templateKey);
 *     }
 *     //  key含义: 验证码业务:用户id:商品id:用户id值_商品id值:key的值含义
 *     public static final SeckillKey getVerifyCode=new SeckillKey(60,"verifyCode:userId:goodsId:%s:codeResult");
 *   }
 * </code>
 *
 * @author shenguangyang
 * @date 2021/7/31 21:11
 */
public abstract class BaseKeyManager implements KeyManager {
    /**
     * 默认0代表永远不过期
     */
    private final int expireSeconds;

    /**
     * 模板key 变量值为 %s
     */
    private final String templateKey;

    /**
     * 模板HashKey 变量值为 %s
     */
    private String templateHashKey;

    public BaseKeyManager(String templateKey) {
        this(0,templateKey);
    }

    public BaseKeyManager(int expireSeconds, String templateKey){
        this.expireSeconds = expireSeconds ;
        this.templateKey = templateKey;
    }

    public BaseKeyManager(int expireSeconds, String templateKey, String templateHashKey) {
        this.expireSeconds = expireSeconds;
        this.templateKey = templateKey;
        this.templateHashKey = templateHashKey;
    }

    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 可确定获取唯一key
     * @return 前缀
     */
    @Override
    public String getKey(String... keyParams) {
        StringJoiner stringJoiner = new StringJoiner(":");
        String className = getClass().getSimpleName();
        StringJoiner keyParam = new StringJoiner("_");
        for (String param : keyParams) {
            keyParam.add(param);
        }
        String template = stringJoiner.add(className).add(templateKey).toString();
        return String.format(template, keyParam.toString());
    }

    @Override
    public String getKey() {
        return this.templateKey;
    }

    @Override
    public String getHashKey(String... hashKeyParams) {
        StringJoiner hashKeyParam = new StringJoiner("_");
        for (String param : hashKeyParams) {
            hashKeyParam.add(param);
        }
        return String.format(templateHashKey, hashKeyParam.toString());
    }
}
