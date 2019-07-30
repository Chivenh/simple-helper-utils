package com.fhtiger.utils.helperutils.helper;

import java.util.*;

/**
 * 构建辅助工具
 *
 * @author LFH
 * @since 2019年01月11日 08:44
 */
public final class BuildHelper {

	/* HashMap构建辅助工具 */

	public static <K, V> Map<K, V> buildMap() {
		return new HashMap<>();
	}

	public static <K, V> Map<K, V> buildMap(int size) {
		return new HashMap<>(size);
	}

	public static <K, V> Map<K, V> buildMap(Map<? extends K, ? extends V> entry) {
		return new HashMap<>(entry);
	}

	public static <K, V> Map<K, V> buildMap(K key, V value) {
		HashMap<K, V> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	public static <K, V> Map<K, V> buildMap(Map<? extends K, ? extends V> entry, K key, V value) {
		Map<K, V> map = buildMap(entry);
		map.put(key, value);
		return map;
	}

	/* ArrayList构建辅助工具 */

	public static <T> List<T> buildList() {
		return new ArrayList<>();
	}

	public static <T> List<T> buildList(int size) {
		return new ArrayList<>(size);
	}

	public static <T> List<T> buildList(List<? extends T> entry) {
		return new ArrayList<>(entry);
	}

	public static <T> List<T> buildList(T entity) {
		ArrayList<T> list = new ArrayList<>();
		list.add(entity);
		return list;
	}

	public static <T> List<T> buildList(List<? extends T> entry,T entity) {
		List<T> list = buildList(entry);
		list.add(entity);
		return list;
	}

	@SafeVarargs
	public static <T> List<T> buildList(T... entities) {
		return new ArrayList<>(Arrays.asList(entities));
	}
}
