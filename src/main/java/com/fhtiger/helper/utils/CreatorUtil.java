package com.fhtiger.helper.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * CUtil
 * {@link java.util.Collection}容器的便捷创建工厂类
 *
 * @author Chivenh
 * @since 2023年05月19日 16:55
 */
@SuppressWarnings({ "unused", "AlibabaClassNamingShouldBeCamel" })

public final class CreatorUtil {
	private CreatorUtil() throws IllegalAccessException {
		throw new IllegalAccessException("此类不能被实例化!");
	}

	/**
	 * 自动计算合理(initialCapacity)来初始化Map容器,默认使用{@link HashMap#HashMap(int)}
	 *
	 * @param size 初始化容器大小,自动计算合理的Map:initialCapacity
	 * @param <K>  Key
	 * @param <V>  Value
	 * @return Map
	 */
	public static <K, V> Map<K, V> newMap(int size) {
		return newMap(size, HashMap::new);
	}

	/**
	 * 自动计算合理(initialCapacity)来初始化Map容器
	 *
	 * @param size    初始化容器大小,自动计算合理的Map:initialCapacity
	 * @param creator 容器创建器{@link HashMap#HashMap(int)},{@link java.util.LinkedHashMap#LinkedHashMap(int)}等.
	 * @param <K>     Key
	 * @param <V>     Value
	 * @return Map
	 */
	public static <K, V> Map<K, V> newMap(int size, IntFunction<Map<K, V>> creator) {
		int initialCapacity = size < 1 ? 0 : size / 3 * 4;
		return creator.apply(initialCapacity);
	}
}
