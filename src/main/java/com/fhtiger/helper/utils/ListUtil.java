package com.fhtiger.helper.utils;

import com.fhtiger.helper.utils.adapter.SplitAdapter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * ListUtil
 * List操作相关工具方法
 *
 * @author Chivenh
 * @since 2020年07月22日 10:58
 */
@SuppressWarnings({ "unused" })

public final class ListUtil {

	private ListUtil() throws IllegalAccessException {
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 按指定大小分隔集合
	 *
	 * @param value 原始字符串
	 * @param size  分隔大小&lt;&lt;+值从左向右分隔，-值从右向左分隔&gt;&gt;
	 * @param <E>   元素类型
	 * @return List&lt;List&lt;E&gt;&gt;
	 */
	public static <E> List<List<E>> splitBySize(List<E> value, int size) {

		return SplitAdapter.LIST.splitBySize(value, size);
	}

	/**
	 * 按指定房间数分隔数据
	 *
	 * @param value    原始数据
	 * @param roomSize 分隔房间总数&lt;&lt;+值多余数量置于首个房间，-值则多余数量置于尾部房间&gt;&gt;
	 * @param <E>      元素类型
	 * @return List&lt;List&lt;E&gt;&gt;
	 */
	public static <E> List<List<E>> splitByRooms(List<E> value, int roomSize) {

		return SplitAdapter.LIST.splitByRooms(value, roomSize);
	}

	/**
	 * 集合排序
	 *
	 * @param value        集合数据
	 * @param keyExtractor 获取比较值的Function
	 * @param <E>          元素类型
	 * @param <U>          比较值类型
	 */
	public static <E, U extends Comparable<U>> void sort(List<E> value, Function<E, U> keyExtractor) {
		value.sort(Comparator.comparing(keyExtractor));
	}

	/**
	 * 集合排序
	 *
	 * @param value         集合数据
	 * @param keyExtractor  获取比较值的Function
	 * @param keyComparator 比较器
	 * @param <E>           元素类型
	 * @param <U>           比较值类型
	 */
	public static <E, U> void sort(List<E> value, Function<E, U> keyExtractor, Comparator<U> keyComparator) {
		value.sort(Comparator.comparing(keyExtractor, keyComparator));
	}

	/**
	 * 按提取器返回Key对集合进行分组
	 *
	 * @param value        集合数据
	 * @param keyExtractor 分组key提取器
	 * @param <K>          分组值类型
	 * @param <E>          元素类型
	 * @return Map&lt;K,List&lt;E&gt;&gt;
	 */
	public static <K, E> Map<K, List<E>> group(List<E> value, Function<E, K> keyExtractor) {
		return value.stream().collect(Collectors.groupingBy(keyExtractor));
	}

	/**
	 * 按提取器返回Key对集合进行分组
	 *
	 * @param value        集合数据
	 * @param keyExtractor 分组key提取器
	 * @param downstream   a {@code Collector} implementing the downstream reduction
	 * @return Map&lt;K,List&lt;E&gt;&gt;
	 * @param <K> 分组值类型
	 * @param <E> 元素类型
	 * @param <R> 目标元素类型
	 */
	public static <K, E, R> Map<K, List<R>> group(List<E> value, Function<E, K> keyExtractor, Collector<E, ?, List<R>> downstream) {
		return value.stream().collect(Collectors.groupingBy(keyExtractor, downstream));
	}

	/**
	 * 按提取器返回Key对集合进行分组
	 *
	 * @param value        集合数据
	 * @param keyExtractor 分组key提取器
	 * @param mapFactory   a function which, when called, produces a new empty
	 *                     {@code Map} of the desired type
	 * @param downstream   a {@code Collector} implementing the downstream reduction
	 * @return Map&lt;K,List&lt;R&gt;&gt;
	 * @param <K> 分组值类型
	 * @param <E> 元素类型
	 * @param <R> 结果值类型
	 * @param <M> 结果map类型
	 */
	public static <K, E, R, M extends Map<K, List<R>>> M group(List<E> value, Function<E, K> keyExtractor, Supplier<M> mapFactory, Collector<E, ?, List<R>> downstream) {
		return value.stream().collect(Collectors.groupingBy(keyExtractor, mapFactory, downstream));
	}

	/**
	 * 将集合中每个元素使用转换器转换后返回新集合
	 *
	 * @param list      原始集合
	 * @param converter 转换器
	 * @param <E>       E 元素值类型
	 * @param <R>       R 结果值类型
	 * @return List&lt;R&gt;
	 */
	public static <E, R> List<R> convert(Collection<E> list, Function<E, R> converter) {
		Objects.requireNonNull(converter, "converter不能为NULL!");
		if (list == null) {
			return null;
		}
		if (list.isEmpty()) {
			return Collections.emptyList();
		}

		List<R> result = new ArrayList<>(list.size());
		for (E e : list) {
			result.add(converter.apply(e));
		}
		return result;
	}
}
