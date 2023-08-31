package com.fhtiger.helper.utils.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * SplitAdapter
 * 使用适配器模式来定义一组可分隔数据的操作工具
 *
 * @author Chivenh
 * @since 2020年07月22日 14:29
 */
@SuppressWarnings({ "unused", "rawtypes" })

public enum SplitAdapter {

	/**
	 * List 之分隔处理器
	 */
	LIST(new SplitHandler<List>() {
		@Override
		public int valueLength(List value) {
			return value.size();
		}

		@Override
		public List subFromTo(List value, int beginIndex, int endIndex) {
			return value.subList(beginIndex, endIndex);
		}
	}, List.class::isAssignableFrom),
	/**
	 * Array 之分隔处理器
	 */
	ARRAY(new SplitHandler<Object[]>() {

		@Override
		public int valueLength(Object[] value) {
			return value.length;
		}

		@Override
		public Object[] subFromTo(Object[] value, int beginIndex, int endIndex) {
			return Arrays.copyOfRange(value, beginIndex, endIndex);
		}
	}, Object[].class::isAssignableFrom),
	/**
	 * String 之分隔处理器
	 */
	STRING(new SplitHandler<String>() {

		@Override
		public int valueLength(String value) {
			return value.length();
		}

		@Override
		public String subFromTo(String value, int beginIndex, int endIndex) {
			return value.substring(beginIndex, endIndex);
		}

		@Override
		public String subFrom(String value, int beginIndex) {
			return value.substring(beginIndex);
		}
	}, String.class::isAssignableFrom),
	/**
	 *
	 */
	INTEGER(new SplitHandler<Integer>() {
		@Override
		public int valueLength(Integer value) {
			return value;
		}

		@Override
		public Integer subFromTo(Integer value, int beginIndex, int endIndex) {
			return endIndex;
		}
	}, Integer.class::isAssignableFrom);

	private final SplitHandler splitHandler;

	private final Predicate<Class<?>> ableSplit;

	SplitAdapter(SplitHandler splitHandler, Predicate<Class<?>> ableSplit) {
		this.splitHandler = splitHandler;
		this.ableSplit = ableSplit;
	}

	/**
	 * 按指定大小分隔数据
	 *
	 * @param value 原始数据
	 * @param size  分隔大小&lt;&lt;+值从左向右分隔，-值从右向左分隔&gt;&gt;
	 * @param <E>   数据类型
	 * @return List&lt;E&gt;
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> splitBySize(final E value, int size) {

		Class<?> valueClass = value.getClass();

		if (!this.ableSplit.test(valueClass)) {
			throw new RuntimeException(valueClass.getName() + ",不支持此分隔操作!");
		}
		return splitBySize((SplitHandler<E>) this.splitHandler, value, size);
	}

	/**
	 * 按指定房间数分隔数据
	 *
	 * @param value    原始数据
	 * @param roomSize 分隔房间总数&lt;&lt;+值多余数量置于首个房间，-值则多余数量置于尾部房间&gt;&gt;
	 * @param <E>      数据类型
	 * @return List&lt;E&gt;
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> splitByRooms(final E value, int roomSize) {

		Class<?> valueClass = value.getClass();

		if (!this.ableSplit.test(valueClass)) {
			throw new RuntimeException(valueClass.getName() + ",不支持此分隔操作!");
		}
		return splitByRooms((SplitHandler<E>) this.splitHandler, value, roomSize);
	}

	/**
	 * 根据值类型获取匹配的{@link SplitAdapter}
	 *
	 * @param valueClass 值类型
	 * @return Optional&lt;{@link SplitAdapter}&gt;
	 */
	private static SplitAdapter getMatch(Class<?> valueClass) {

		if (ARRAY.ableSplit.test(valueClass)) {
			return ARRAY;
		}
		if (LIST.ableSplit.test(valueClass)) {
			return LIST;
		}
		if (STRING.ableSplit.test(valueClass)) {
			return STRING;
		}
		if (INTEGER.ableSplit.test(valueClass)) {
			return INTEGER;
		}

		return null;
	}

	/**
	 * 按指定大小分隔数据
	 *
	 * @param splitHandler {@link SplitHandler} 分隔处理器
	 * @param value        原始数据
	 * @param size         分隔大小&lt;&lt;+值从左向右分隔，-值从右向左分隔&gt;&gt;
	 * @param <E>          数据类型
	 * @return List&lt;E&gt;
	 */
	private static <E> List<E> splitBySize(final SplitHandler<E> splitHandler, final E value, final int size) {

		if (splitHandler.isNotEmpty(value) && size != 0) {

			int valueLength = splitHandler.valueLength(value);

			/* 分隔大小的绝对值 */
			int splitSize = Math.abs(size);

			/* 值长度取模余量 */
			int leaveLength = valueLength % splitSize;

			/* 分隔结果集长度 */
			int splitLength = valueLength / splitSize + (leaveLength > 0 ? 1 : 0);

			List<E> results = new ArrayList<>(splitLength);

			int i = 0, start;

			/* 向右分隔 */
			if (size > 0) {
				while (i < splitLength) {
					start = i * splitSize;
					if (i == splitLength - 1) {
						results.add(splitHandler.subFromTo(value, start, valueLength));
					} else {
						results.add(splitHandler.subFromTo(value, start, start + splitSize));
					}
					i++;
				}
			} else {
				while (i < splitLength) {
					if (i == splitLength - 1) {
						results.add(splitHandler.subFromTo(value, 0, leaveLength));
					} else {
						start = leaveLength + (splitLength - i - 2) * splitSize;
						results.add(splitHandler.subFromTo(value, start, start + splitSize));
					}
					i++;
				}
			}

			return results;
		}

		return Collections.emptyList();
	}

	/**
	 * 按指定房间数分隔数据
	 *
	 * @param splitHandler {@link SplitHandler} 分隔处理器
	 * @param value        原始数据
	 * @param roomSize     分隔房间总数&lt;&lt;+值多余数量置于首个房间，-值则多余数量置于尾部房间&gt;&gt;
	 * @param <E>          数据类型
	 * @return List&lt;E&gt;
	 */
	private static <E> List<E> splitByRooms(final SplitHandler<E> splitHandler, final E value, final int roomSize) {

		if (splitHandler.isNotEmpty(value) && roomSize != 0) {

			int valueLength = splitHandler.valueLength(value);

			boolean remainderLast = roomSize < 0;

			/* 房间数的绝对值 */
			int allRoomSize = Math.abs(roomSize);

			/* 房间数多于数据长度时，只占用数据长度的房间数量 */
			if (allRoomSize > valueLength) {
				allRoomSize = valueLength;
			}

			/* 余量计算 */
			int leaveLength = valueLength % allRoomSize;

			/* 分隔结果集第个房间数量 */
			int splitLength = valueLength / allRoomSize;

			List<E> results = new ArrayList<>(allRoomSize);

			int i = 0, start, end, offset = 0;

			while (i < allRoomSize) {
				start = i * splitLength + offset;
				end = start + splitLength;
				if (i == 0 && !remainderLast) {
					results.add(splitHandler.subFromTo(value, start, end + leaveLength));
					offset = leaveLength;
				} else if (i == allRoomSize - 1 && remainderLast) {
					results.add(splitHandler.subFromTo(value, start, end + leaveLength));
				} else {
					results.add(splitHandler.subFromTo(value, start, end));
				}
				i++;
			}

			return results;
		}

		return Collections.emptyList();
	}

	/**
	 * 按指定大小分隔数据
	 *
	 * @param value 原始数据
	 * @param size  分隔大小&lt;&lt;+值从左向右分隔，-值从右向左分隔&gt;&gt;
	 * @param <E>   数据类型
	 * @return List&lt;E&gt;
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> List<E> autoSplitBySize(final E value, final int size) {

		Class<?> valueClass = value.getClass();

		SplitAdapter adapter = getMatch(valueClass);

		if (adapter == null) {

			throw new RuntimeException(valueClass.getName() + ",不支持此分隔操作!");
		}

		SplitHandler<E> splitHandler = (SplitHandler<E>) adapter.splitHandler;

		return splitBySize(splitHandler, value, size);
	}

	/**
	 * 按指定房间数分隔数据
	 *
	 * @param value    原始数据
	 * @param roomSize 分隔房间总数&lt;&lt;+值多余数量置于首个房间，-值则多余数量置于尾部房间&gt;&gt;
	 * @param <E>      数据类型
	 * @return List&lt;E&gt;
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> List<E> autoSplitByRooms(final E value, final int roomSize) {

		Class<?> valueClass = value.getClass();

		SplitAdapter adapter = getMatch(valueClass);

		if (adapter == null) {

			throw new RuntimeException(valueClass.getName() + ",不支持此分隔操作!");
		}

		SplitHandler<E> splitHandler = (SplitHandler<E>) adapter.splitHandler;

		return splitByRooms(splitHandler, value, roomSize);
	}

	/**
	 * 可分隔数据的处理器
	 *
	 * @param <E> 指定数据类型
	 */
	interface SplitHandler<E> {

		/**
		 * 数据不为空
		 *
		 * @param value 数据
		 * @return boolean
		 */
		default boolean isNotEmpty(E value) {
			return value != null && this.valueLength(value) > 0;
		}

		/**
		 * 返回数据长度
		 *
		 * @param value 数据
		 * @return int
		 */
		int valueLength(E value);

		/**
		 * 返回截取数据
		 *
		 * @param value      数据
		 * @param beginIndex 起始位置 (inclusive)
		 * @param endIndex   终止位置 (exclusive)
		 * @return E
		 */
		E subFromTo(E value, int beginIndex, int endIndex);

		/**
		 * 返回截取数据
		 *
		 * @param value      数据
		 * @param beginIndex 起始位置 (inclusive)
		 * @return E
		 */
		default E subFrom(E value, int beginIndex) {
			return this.subFromTo(value, beginIndex, this.valueLength(value));
		}
	}
}
