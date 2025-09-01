package com.fhtiger.helper.utils;

import com.fhtiger.helper.utils.adapter.SplitAdapter;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * StringValueUtil
 * 字符串相关操作工具方法
 *
 * @author Chivenh
 * @since 2020年07月21日 15:07
 */
@SuppressWarnings({ "unused", "WeakerAccess" })

public final class StringValueUtil {

	private StringValueUtil() throws IllegalAccessException {
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	public static final String EMPTY = "";

	/**
	 * 将指定字符串片段重复指定次数后拼接返回
	 *
	 * @param fragment 字符串片段
	 * @param times    重复次数
	 * @return String
	 */
	public static String repeat(final CharSequence fragment, final int times) {
		return repeat(fragment, "", times);
	}

	/**
	 * 将指定字符串片段重复指定次数后拼接返回
	 *
	 * @param fragment  字符串片段
	 * @param delimiter 指定拼接符号
	 * @param times     重复次数
	 * @return String
	 */
	public static String repeat(final CharSequence fragment, final CharSequence delimiter, final int times) {
		if (SpecialUtil.isEmpty(fragment)) {
			return "";
		}
		String[] result = new String[times];
		Arrays.fill(result, fragment);
		return String.join(delimiter, result);
	}

	/**
	 * 返回字符串按指定长度截断后的结果
	 *
	 * @param value 原始字符串
	 * @param size  截断长度 &lt;&lt;+值自左向右截断，-值自右向左截断&gt;&gt;
	 * @return String
	 */
	public static String truncated(final String value, final int size) {

		if (SpecialUtil.isNotEmpty(value)) {
			int sLength = value.length();

			if (size > 0) {
				return value.substring(0, Math.min(size, sLength));
			} else if (size < 0) {
				int reverseSize = -size;

				return value.substring(reverseSize > sLength ? 0 : (sLength - reverseSize));
			}

			return EMPTY;
		}

		return value;
	}

	/**
	 * 补齐字符串
	 *
	 * @param value    原始字符串
	 * @param fillChar 补齐字符
	 * @param size     目标长度 &lt;&lt;+值在右侧补齐，-值在左侧补齐&gt;&gt;
	 * @return String
	 */
	public static String fillIn(final String value, final char fillChar, final int size) {

		if (SpecialUtil.isNotEmpty(value)) {
			int sLength = value.length();

			if (size > sLength) {
				char[] appendChars = new char[size - sLength];
				Arrays.fill(appendChars, fillChar);
				return value + String.copyValueOf(appendChars);
			} else if (-size > sLength) {
				char[] prependChars = new char[-size - sLength];
				Arrays.fill(prependChars, fillChar);
				return String.copyValueOf(prependChars) + value;
			}
		}

		return value;
	}

	/**
	 * 分隔成指定结果集
	 *
	 * @param value     原始字符串
	 * @param splitChar 分隔字符
	 * @param convert   转换器
	 * @param <T>       指定类型
	 * @return List&lt;T&gt;
	 */
	public static <T> List<T> splitTo(final String value, final String splitChar, final Function<String, T> convert) {

		if (SpecialUtil.isNotEmpty(value)) {
			String[] values = value.split(splitChar);

			List<T> results = new ArrayList<>(values.length);

			for (String v : values) {
				results.add(convert.apply(v));
			}

			return results;
		}

		return Collections.emptyList();
	}

	/**
	 * 分隔成指定结果集
	 *
	 * @param value     原始字符串
	 * @param splitChar 分隔字符
	 * @param convert   转换器
	 * @param <T>       指定类型
	 * @return List&lt;T&gt;
	 */
	public static <T> List<T> splitBiTo(final String value, final String splitChar, final BiFunction<Integer, String, T> convert) {

		if (SpecialUtil.isNotEmpty(value)) {
			String[] values = value.split(splitChar);
			int length = values.length;

			List<T> results = new ArrayList<>(length);

			for (int i = 0; i < length; i++) {
				results.add(convert.apply(i, values[i]));
			}

			return results;
		}

		return Collections.emptyList();
	}

	/**
	 * 分隔为Long结果集
	 *
	 * @param value     原始字符串
	 * @param splitChar 分隔字符
	 * @return List&lt;Long&gt;
	 */
	public static List<Long> splitToLong(final String value, final String splitChar) {
		return splitTo(value, splitChar, Long::parseLong);
	}

	/**
	 * 分隔为Long结果集
	 * 默认使用`,`分隔
	 *
	 * @param value 原始字符串
	 * @return List&lt;Long&gt;
	 */
	public static List<Long> splitToLong(final String value) {
		return splitToLong(value, ",");
	}

	/**
	 * 按指定大小分隔字符串
	 *
	 * @param value 原始字符串
	 * @param size  分隔大小&lt;&lt;+值从左向右分隔，-值从右向左分隔&gt;&gt;
	 * @return List&lt;String&gt;
	 */
	public static List<String> splitBySize(final String value, final int size) {

		return SplitAdapter.STRING.splitBySize(value, size);
	}

	/**
	 * 按指定房间数分隔数据
	 *
	 * @param value    原始数据
	 * @param roomSize 分隔房间总数&lt;&lt;+值多余数量置于首个房间，-值则多余数量置于尾部房间&gt;&gt;
	 * @return List&lt;E&gt;
	 */
	public static List<String> splitByRooms(final String value, final int roomSize) {

		return SplitAdapter.STRING.splitByRooms(value, roomSize);
	}

	/**
	 * 下划线转驼峰
	 *
	 * @param value 下划线字符串
	 * @return 驼峰字符串
	 */
	public static String hump(final String value) {
		if (Objects.isNull(value) || value.isEmpty()) {
			return value;
		}
		return String.join(EMPTY, StringValueUtil.splitTo(value, "_(?<=\\S)", k -> !k.isEmpty() ? Character.toUpperCase(k.charAt(0)) + k.substring(1) : k));
	}

	/**
	 * 下划线转驼峰,忽略首项
	 *
	 * @param value 下划线字符串
	 * @return 驼峰字符串
	 */
	public static String humpNonFirst(final String value) {
		if (Objects.isNull(value) || value.isEmpty()) {
			return value;
		}
		return String.join(EMPTY, StringValueUtil.splitBiTo(value, "_(?<=\\S)", (i, k) -> i > 0 && !k.isEmpty() ? Character.toUpperCase(k.charAt(0)) + k.substring(1) : k));
	}

	/**
	 * 驼峰转下划线
	 *
	 * @param value 驼峰字符串
	 * @return 下划线字符串
	 */
	public static String underline(final String value) {
		if (Objects.isNull(value) || value.isEmpty()) {
			return value;
		}
		return String.join(EMPTY, StringValueUtil.splitBiTo(value, "(?=[A-Z])", (i, k) -> !k.isEmpty() ? (i > 0 ? "_" : "") + Character.toLowerCase(k.charAt(0)) + k.substring(1) : k));
	}

	/**
	 * 驼峰转下划线,忽略首项
	 *
	 * @param value 驼峰字符串
	 * @return 下划线字符串
	 */
	public static String underlineNonFirst(final String value) {
		if (Objects.isNull(value) || value.isEmpty()) {
			return value;
		}
		return String.join(EMPTY, StringValueUtil.splitBiTo(value, "(?=[A-Z])", (i, k) -> i > 0 && !k.isEmpty() ? "_" + Character.toLowerCase(k.charAt(0)) + k.substring(1) : k));
	}

	/**
	 * Trim leading and trailing whitespace from the given String.
	 * @param str -
	 * @return -
	 */
	public static String trimWhitespace(String str) {
		if (!StringUtils.hasLength(str)) {
			return str;
		}

		return str.strip();
	}
}
