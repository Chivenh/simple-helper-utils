package com.fhtiger.utils.helperutils.helper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用MD5加密字符串
 * 
 * @author LuoGang
 */
public class MD5Helper {
	/** MD5算法 */
	public static final String MD5 = "MD5";

	/**
	 * Returns an MD5 MessageDigest.
	 *
	 * @return An MD5 digest instance.
	 * @throws IllegalArgumentException
	 *             when a {@link NoSuchAlgorithmException} is caught, which should never happen because MD5 is a
	 *             built-in algorithm
	 * @see MessageDigestAlgorithms#MD5
	 */
	public static MessageDigest getMd5Digest() {
		try {
			return MessageDigest.getInstance(MD5);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param text 明文
	 * @param encoding 明文使用的编码
	 * 
	 * @return 密文
	 */
	public static String encode(String text, String encoding) {
		byte[] bytes = null;
		try {
			bytes = StringHelper.isEmpty(encoding) ? text.getBytes() : text.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return DigestUtils.md5Hex(bytes);
	}

	/**
	 * 用MD5加密字符串,使用系统默认的编码
	 * 
	 * @param text 要加密的字符串
	 * @return 密文
	 * 
	 * @see SystemHelper#ENCODING
	 */
	public static String encode(String text) {
		return encode(text, SystemHelper.ENCODING);
	}

	/**
	 * 用MD5加密字符串
	 * 
	 * @param text 要加密的字符串
	 * @return 密文
	 */
	public static String md5(String text) {
		return encode(text);
	}
}
