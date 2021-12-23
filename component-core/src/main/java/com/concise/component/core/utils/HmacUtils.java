package com.concise.component.core.utils;

import cn.hutool.core.lang.UUID;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * HMAC_SHA1 Sign生成器.
 * 
 * 需要apache.components.codec包
 * 
 */
public class HmacUtils {
	private static final Logger log = LoggerFactory.getLogger(HmacUtils.class);
	public static class  Sha1 {
		private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
		/**
		 * 使用 HMAC-SHA1 签名方法对data进行签名
		 * @param data 被签名的字符串
		 * @param key 密钥
		 * @return 加密后的字符串
		 */
		public static String gen(String data, String key) {
			byte[] result = null;
			try {
				//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
				SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
				//生成一个指定 Mac 算法 的 Mac 对象
				Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
				//用给定密钥初始化 Mac 对象
				mac.init(signinKey);
				//完成 Mac 操作
				byte[] rawHmac = mac.doFinal(data.getBytes());
				result = Base64.encodeBase64(rawHmac);
			} catch (NoSuchAlgorithmException | InvalidKeyException e) {
				log.error("HMAC-SHA1 签名方法对data进行签名失败,message = " + e.getMessage());
				return null;
			}
			if (null != result) {
				return new String(result);
			} else {
				return null;
			}
		}
		/**
		 * 测试
		 * @param args
		 */
		public static void main(String[] args) {
			int count = 10;
			while (count-- > 0) {
				long start = System.currentTimeMillis();
				String gen = gen("fe12f362-5e80-4208-9d9d-cd4b125f3153fe12f362-5e80-4208-9d9d-cd4b125f3153fe12f362-5e80-4208-9d9d-cd4b125f3153", UUID.randomUUID().toString());
				System.out.println(gen);
				long end = System.currentTimeMillis();
				System.out.println("耗时 " + (end - start) +" ms");
			}

		}
	}

}
