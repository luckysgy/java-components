package com.concise.component.core.utils;

import com.concise.component.core.exception.Assert;
import com.concise.component.core.exception.UtilException;

import java.io.IOException;
import java.util.Base64;

/**
 * 图片与base64相互转换
 * @author shenguangyang
 *
 */
public class Base64Util {

	/**
	 * 解码
	 * @param base64Str
	 * @return
	 * @throws IOException
	 */
	public static byte[] decode(String base64Str) throws IOException {
		if (base64Str == null) {
			throw new UtilException("base64Str == null");
		}
		return Base64.getDecoder().decode(base64Str);
	}

	/**
	 * 对字节进行编码
	 * @param image
	 * @return
	 */
	public static String encode(byte[] image) {
		Assert.notNull(image, "image == null");
		return Base64.getEncoder().encodeToString(image);
	}

	/**
	 * @param targetStr
	 * @return
	 */
	public static String encode(String targetStr) {
		Assert.notNull(targetStr, "targetStr == null");
		return Base64.getEncoder().encodeToString(targetStr.getBytes());
	}
}
