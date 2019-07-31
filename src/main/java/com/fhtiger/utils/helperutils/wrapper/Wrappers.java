package com.fhtiger.utils.helperutils.wrapper;

import com.fhtiger.utils.helperutils.helper.Helper;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 封装实体工具
 * 
 * @author LFH
 * @since 2019/7/30 18:11
 * @version 0.0.1
 */
public class Wrappers {

	/**
	 * 创建一个新对象,entity.class的新对象,未初始化任何数据
	 * 
	 * @param entity 实体
	 * @return T
	 * @param <T> T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(T entity) {
		Class<?> clazz = Helper.getRealClass(entity);
		return (T) BeanUtils.instantiateClass(clazz);
	}

	/**
	 * 创建新的Collection对象
	 * 
	 * @param list C
	 * @return C
	 * @param <C> C
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <C extends Collection<?>> C instantiateList(C list) {
		boolean isInterface = false;
		Class<? extends Collection> actualType = list.getClass();
		String className = actualType.getName();
		if (className.startsWith(Collections.class.getName())) {
			// 在Collections下的类
			isInterface = true;
		} else if (!className.startsWith("java.util")) {
			// 不是java.util下的类
			isInterface = true;
		}

		if (!isInterface) {
			try {
				return (C) BeanUtils.instantiateClass(actualType);
			} catch (Exception ignore) {
				// TODO
			}
		}
		if (list instanceof Vector) {
			return (C) new Vector();
		} else if (list instanceof LinkedList) {
			return (C) new LinkedList();
		} else if (list instanceof SortedSet) {
			return (C) new TreeSet(((SortedSet) list).comparator());
		} else if (list instanceof Set) {
			return (C) new LinkedHashSet();
		}
		return (C) new ArrayList();
	}

	/**
	 * 创建新的Map对象
	 * 
	 * @param map C
	 * @return C
	 * @param <C> C
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <C extends Map<?, ?>> C instantiateMap(C map) {
		boolean isInterface = false;
		Class<? extends Map> actualType = map.getClass();
		String className = actualType.getName();
		if (className.startsWith(Collections.class.getName())) {
			// 在Collections下的map类
			isInterface = true;
		} else if (!className.startsWith("java.util")) {
			// 不是java.util下的类
			isInterface = true;
		}

		if (!isInterface) {
			try {
				return (C) BeanUtils.instantiateClass(actualType);
			} catch (Exception ignore) {
				// TODO
			}
		}

		if (map instanceof Properties) {
			return (C) new Properties();
		} else if (map instanceof Hashtable) {
			return (C) new Hashtable();
		} else if (map instanceof SortedMap) {
			return (C) new TreeMap(((SortedMap) map).comparator());
		}

		return (C) new LinkedHashMap();
	}

	/**
	 * 复制一个数组
	 *
	 * @param arr 源数组
	 * @return 复制后的数组
	 * @param <T> T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyArray(T arr) {
		int length = Array.getLength(arr);
		T newArr = (T) Array.newInstance(arr.getClass().getComponentType(), length);
		System.arraycopy(arr, 0, newArr, 0, length);
		return newArr;
	}

	/**
	 * 复制一个列表对象
	 *
	 * @param list 源集合
	 * @return 复制后的集合
	 * @param <T> T
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Collection> T copyList(T list) {
		T newList = instantiateList(list);
		newList.addAll(list);
		return newList;
	}

	/**
	 * 复制一个map对象
	 *
	 * @param map 源Map
	 * @return 复制后的Map
	 * @param <T> T
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Map> T copyMap(T map) {
		T newMap = instantiateMap(map);
		newMap.putAll(map);
		return newMap;
	}
}
