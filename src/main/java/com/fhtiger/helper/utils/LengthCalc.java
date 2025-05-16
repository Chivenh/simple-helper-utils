package com.fhtiger.helper.utils;

import java.util.function.ToIntFunction;

/**
 * 计算字符串实际数据长度的方法枚举
 * @author Chivenh
 * @since 2020-07-10 10:56
 */
@SuppressWarnings("unused")
public class LengthCalc {

	private LengthCalc()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 中文匹配 [Α-￥] [一-龥]
	 */
	private static final String CHINESE_REGEX = "[\\u0391-\\uFFE5]";

	/**
	 *  双字节字符匹配
	 */
	private static final String DOUBLE_CHARS_REGEX = "[^\\x00-\\xff]";

	public static final ToIntFunction<String> CHINESE_FUNCTION = data -> {
		int chineseChars = 0;

		int dataLength = data.length();

		String theChar;

		for (int i = 0; i < dataLength; i++) {
			theChar = data.charAt(i) + "";

			if (theChar.matches(CHINESE_REGEX)) {
				chineseChars++;
			}
		}

		return dataLength + chineseChars;
	};

	public static final ToIntFunction<String> DOUBLE_CHARS_FUNCTION = data -> data.replaceAll(DOUBLE_CHARS_REGEX, "**").length();
}
