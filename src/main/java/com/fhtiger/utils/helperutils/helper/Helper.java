package com.fhtiger.utils.helperutils.helper;

import com.fhtiger.utils.helperutils.util.Tutil;
import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 帮助工具类
 * @author LFH
 * @since 2019/7/30 15:04
 * @version 0.0.1
 */
public class Helper {
	/**
	 * 判断数组arr是否全部为空值
	 * 
	 * @param arr 要校验的数组
	 * @return boolean
	 */
	public static boolean isAllEmpty(Object[] arr) {
		if (arr == null || arr.length == 0) return true;
		for (Object o : arr) {
			if (!Helper.isEmpty(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断Object对象是否为空
	 * 
	 * @param value 要校验的对象
	 * @return boolean
	 */
	public static boolean isEmpty(Object value) {
		if (value == null) return true;
		if (value instanceof CharSequence) return StringHelper.isEmpty(value.toString());
		if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		}
		if (value instanceof Collection) {
			return ((Collection<?>) value).isEmpty();
		}
		if (value instanceof Map) {
			return ((Map<?, ?>) value).isEmpty();
		}
		if (value instanceof Iterable) {
			return !((Iterable<?>) value).iterator().hasNext();
		}
		if (value instanceof Iterator) {
			return !((Iterator<?>) value).hasNext();
		}
		return false;
	}

	/**
	 * 判断obj是否不为空
	 * 
	 * @param obj 要校验的对象
	 * @return boolean
	 * @see #isEmpty(Object)
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	/**
	 * 得到obj的长度
	 * 
	 * @param obj 数组/Collection/Map/Iterable
	 * @return obj的长度或者-1(obj不为数组/Collection/Map/Iterable)
	 */
	public static int getLength(Object obj) {
		if (obj == null) return 0;
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		}

		if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		}

		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		}
		if (obj instanceof Iterable) {
			int length = 0;
			Iterator<?> it = ((Iterable<?>) obj).iterator();
			while (it.hasNext()) {
				it.next();
				length++;
			}
			return length;
		}

		return -1;
	}

	/**
	 * 获取索引数据结构指定index值
	 * 
	 * @param source 数组/List/Iterable/Iterator
	 *
	 * @param index  数组下标索引,从0开始
	 *
	 * @return Object
	 */
	public static Object getValue(Object source, int index) {
		if (isEmpty(source)) return null;
		try {
			if (source.getClass().isArray()) {
				// 数组
				return Array.get(source, index);
			}
			if (source instanceof List) {
				// List
				return ((List<?>) source).get(index);
			}
		} catch (IndexOutOfBoundsException e) {
			// 下标越界则返回null
			return null;
		}

		if (source instanceof Iterable) {
			// Iterable
			source = ((Iterable<?>) source).iterator();
		}
		if (source instanceof Iterator) {
			// Iterator
			Iterator<?> it = (Iterator<?>) source;
			Object v=null;
			while (it.hasNext()) {
				if (index-- == 0) {
					v=it.next();
					break;
				}
				it.next();
			}
			return  v;
		}
		return null;
	}

	/**
	 * 得到source对象的name属性的值
	 * 
	 * @param source 对象
	 * @param name 字段名
	 * @return Object 字段值
	 */
	public static Object getValue(Object source, String name) {
		if (source == null) return null;
		PropertyDescriptor pd = getPropertyDescriptor(source.getClass(), name);
		if (pd == null) return null;
		return getValue(source, pd);
	}

	/**
	 * 得到source对象pd属性的值.
	 * 
	 * @param source 对象
	 * @param pd 字段 {@link PropertyDescriptor}
	 * @return pd的值，如果没有getter方法则会返回null
	 */
	public static Object getValue(Object source, PropertyDescriptor pd) {
		if (source == null) return null;
		Method readMethod = pd.getReadMethod();
		if (readMethod == null) return null;
		if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
			readMethod.setAccessible(true);
		}
		try {
			return readMethod.invoke(source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对source的pd属性进行赋值
	 * 
	 * @param source 对象
	 * @param pd 字段 {@link PropertyDescriptor}
	 * @param value 值
	 */
	public static void setValue(Object source, PropertyDescriptor pd, Object value) {
		if (source == null) return;
		Method writeMethod = pd.getWriteMethod();
		if (writeMethod == null) return;
		if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
			writeMethod.setAccessible(true);
		}
		try {
			writeMethod.invoke(source, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置source的name属性的值为value
	 * 
	 * @param source 对象
	 * @param name 字段名
	 * @param value 值
	 */
	public static void setValue(Object source, String name, Object value) {
		PropertyDescriptor pd = Helper.getPropertyDescriptor(source.getClass(), name);
		if (pd == null) return;
		setValue(source, pd, value);
	}

	/**
	 * 设置obj对象的多个值,数据从values中读取
	 * 
	 * @param obj    对象
	 * @param values Map/POJO对象,应该是一个{}类型的JSON数据
	 * @return obj
	 * @param <T> 返回对象
	 */
	public static <T> T setValues(T obj, Object values) {
		if (obj == null) return obj;
		if (values == null) return obj;
		if (values instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) values;
			for (Object key : map.keySet()) {
				setValue(obj, StringHelper.trim(key), map.get(key));
			}
		} else {
			Helper.copyTo(values, obj);
		}
		return obj;
	}

	/** 基础数据类型的class */
	private static final Class<?>[] BASIC_CLASSES = new Class<?>[] { Number.class, Boolean.class, String.class, Date.class,
			CharSequence.class, Character.class };

	/**
	 * 判断clazz是否为Boolean/Number/String/Date等基础数据类型
	 * 
	 * @param clazz class
	 * @return boolean
	 * @see #BASIC_CLASSES
	 */
	public static boolean isBasicClass(Class<?> clazz) {
		if (clazz.isPrimitive()) return true;
		for (Class<?> cls : BASIC_CLASSES) {
			if (cls.isAssignableFrom(clazz)) return true;
		}
		return false;
	}

	/**
	 * 转换o的类型
	 * 
	 * @param cls 目标类型
	 * @param obj 要转换的对象
	 * @return Object 结果对象
	 */
	public static Object cast(Class<?> cls, Object obj) {
		if (obj == null) {
			return null;
		}
		if (cls == null) {
			return obj;
		}
		Class<?> objCls = getRealClass(obj);

		if (cls == obj.getClass() || cls.isAssignableFrom(objCls)
				|| (cls.isArray() && objCls.isArray() && cls.getComponentType() == objCls.getComponentType())) {
			// obj是cls的子类
			return obj;
		}

		if (cls == Byte.TYPE || cls == Byte.class) {
			return (byte) NumberHelper.intValue(obj);
		} else if (cls == Character.TYPE || cls == Character.class) {
			// 单字节,obj必须是数字或者String
			if (obj instanceof CharSequence) {
				if (((CharSequence) obj).length() < 1) {
					return (char) 0;
				}
				return ((CharSequence) obj).charAt(0);
			}
			return ((char) NumberHelper.intValue(obj));
		} else if (cls == Short.TYPE || cls == Short.class) {
			return ((short) NumberHelper.intValue(obj));
		} else if (cls == Integer.TYPE || cls == Integer.class) {
			return (NumberHelper.intValue(obj));
		} else if (cls == Long.TYPE || cls == Long.class) {
			return (NumberHelper.longValue(obj));
		} else if (cls == Float.TYPE || cls == Float.class) {
			return (NumberHelper.floatValue(obj));
		} else if (cls == Double.TYPE || cls == Double.class) {
			return (NumberHelper.doubleValue(obj));
		} else if (cls == Boolean.class || cls == Boolean.TYPE) {
			return StringHelper.parseBoolean(obj);
		} else if (cls == String.class) {
			return StringHelper.trim(obj);
		} else if (cls == Date.class) {
			obj = DateHelper.parse(obj.toString());
		} else if (cls.isArray() && objCls.isArray() && cls.getComponentType() != objCls.getComponentType()) {
			int l = Array.getLength(obj);
			Class<?> clazz = cls.getComponentType();
			Object array = Array.newInstance(cls.getComponentType(), l);
			for (int i = 0; i < l; i++) {
				if (!clazz.isPrimitive()) {
					Array.set(array, i, cast(cls.getComponentType(), Array.get(obj, i)));
				} else if (clazz == Double.TYPE) {
					Array.setDouble(array, i, NumberHelper.doubleValue(Array.get(obj, i)));
				} else if (clazz == Float.TYPE) {
					Array.setFloat(array, i, NumberHelper.floatValue(Array.get(obj, i)));
				} else if (clazz == Long.TYPE) {
					Array.setLong(array, i, NumberHelper.longValue(Array.get(obj, i)));
				} else if (clazz == Integer.TYPE) {
					Array.setInt(array, i, NumberHelper.intValue(Array.get(obj, i)));
				} else if (clazz == Short.TYPE) {
					Array.setShort(array, i, (short) NumberHelper.intValue(Array.get(obj, i)));
				} else if (clazz == Byte.TYPE) {
					Array.setByte(array, i, (byte) NumberHelper.intValue(Array.get(obj, i)));
				} else if (clazz == Character.TYPE) {
					Array.setChar(array, i, (Character) cast(clazz, Array.get(obj, i)));
				} else if (clazz == Boolean.TYPE) {
					Array.setBoolean(array, i, (Boolean) cast(clazz, Array.get(obj, i)));
				}
			}
			obj = array;
		} else if (cls.isArray() && !objCls.isArray()) {
			Class<?> clazz = cls.getComponentType();
			Object[] array = (Object[]) Array.newInstance(clazz, 1);
			array[0] = cast(clazz, obj);
			obj = array;
		}
		return obj;
	}

	/*--- Java Bean 相关方法 ---*/

	/**
	 * copy数据
	 *
	 * @param source 源对象
	 * @param ignoreProperties 不需要包含的属性
	 * @return 结果对象
	 * @param <T> 泛型约束
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T source, String... ignoreProperties) {
		return copyTo(source, (T) BeanUtils.instantiateClass(getRealClass(source)), ignoreProperties);
	}

	/**
	 * 仅复制source的基础类型数据
	 *
	 * @param source           源对象
	 * @param ignoreProperties 忽略属性
	 * @return 结果对象
	 * @see #isBasicClass(Class)
	 * @param <T> 泛型约束
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyBasic(T source, String... ignoreProperties) {
		return copyTo(source, (T) BeanUtils.instantiateClass(getRealClass(source)), true, true, ignoreProperties);
	}

	/**
	 * 将source的基础类型数据复制到target中
	 *
	 * @param source 源对象
	 * @param target 目标对象
	 * @param ignoreProperties 忽略属性
	 * @return 结果对象
	 * @param <T> 泛型约束
	 */
	public static <T> T copyBasicTo(Object source, T target, String... ignoreProperties) {
		return Helper.copyTo(source, target, true, true, ignoreProperties, (String[]) null);
	}

	/**
	 * 将source的值复制到target中
	 *
	 * @param source           源对象
	 * @param target			目标对象
	 * @param ignoreProperties 忽略属性
	 * @return 目标对象
	 * @param <T> 泛型约束
	 */
	public static <T> T copyTo(Object source, T target, String... ignoreProperties) {
		return Helper.copyTo(source, target, true, ignoreProperties, (String[]) null);
	}

	/**
	 * 将source的值复制到target中
	 *
	 * @param source           源对象
	 * @param target			目标对象
	 * @param includeNull 是否复制null值
	 * @param ignoreProperties 不需要复制的属性
	 * @param properties 要复制的属性
	 * @return 目标对象
	 * @param <T> 泛型约束
	 */
	public static <T> T copyTo(Object source, T target, boolean includeNull, String[] ignoreProperties,
			String... properties) {
		return Helper.copyTo(source, target, includeNull, false, ignoreProperties, properties);
	}

	/**
	 * 复制source的属性到target中
	 *
	 * @param source 源对象
	 * @param target 被复制的对象，不能为null
	 * @param includeNull 是否复制source中值为null的属性
	 * @param basicOnly 是否仅复制基础数据类型
	 * @param ignoreProperties 不需要复制的属性
	 * @param properties 要复制的属性
	 * @return 目标对象
	 * @see #isBasicClass(Class)
	 * @param <T> 泛型约束
	 */
	public static <T> T copyTo(Object source, T target, boolean includeNull, boolean basicOnly,
			String[] ignoreProperties, String... properties) {
		if (source == null) return includeNull ? null : target;

		Class<?> cls = getRealClass(target);
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(cls);

		Set<String> includes = new HashSet<>();
		if (properties != null) Helper.add(includes, properties);
		Set<String> excludes = new HashSet<>();
		if (ignoreProperties!=null) Helper.add(excludes, ignoreProperties);

		String name;
		Object value;
		for (PropertyDescriptor pd : pds) {
			name = pd.getName();
			if (!WildcardHelper.include(excludes, includes, name)) continue;

			if (basicOnly && !isBasicClass(pd.getPropertyType())) continue; // 不是基本数据类型

			value = Helper.getValue(source, name);
			if (value == null && !includeNull) continue;
			Helper.setValue(target, pd, value);
		}
		return target;
	}

	/** 根据class缓存的数据 */
	private static final Map<Class<?>, PropertyDescriptor[]> PROPERTY_DESCRIPTORS_CACHE = new HashMap<>();

	/**
	 * 得到cls的所有属性描述
	 *
	 * @param cls 类class
	 * @return {@link PropertyDescriptor[]}
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> cls) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(cls);
		List<PropertyDescriptor> c = new ArrayList<>();
		for (PropertyDescriptor pd : pds) {
			if ("class".equals(pd.getName())) continue; // 不需要getClass属性
			c.add(pd);
		}
		pds = c.toArray(new PropertyDescriptor[c.size()]);
		PROPERTY_DESCRIPTORS_CACHE.put(cls, pds);
		return pds;
	}

	/**
	 * 得到cls的属性,得到父类定义的属性时,遍历到stopCls为止.
	 *
	 * @param cls     class
	 * @param stopCls 所有在stopCls定以的对象不包含在返回字段中
	 * @return {@link PropertyDescriptor[]}
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> cls, Class<?> stopCls) {
		try {
			return Introspector.getBeanInfo(cls, stopCls).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 得到obj对象的propertyName属性
	 *
	 * @param obj          对象
	 * @param propertyName 属性名
	 * @return {@link PropertyDescriptor}
	 */
	public static PropertyDescriptor getPropertyDescriptor(Object obj, String propertyName) {
		if (obj instanceof Class) return Helper.getPropertyDescriptor((Class<?>) obj, propertyName);
		return BeanUtils.getPropertyDescriptor(getRealClass(obj), propertyName);
	}

	/**
	 * 得到cls的propertyName属性
	 *
	 * @param cls          对象class属性
	 * @param propertyName 属性名
	 * @return {@link PropertyDescriptor}
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> cls, String propertyName) {
		return BeanUtils.getPropertyDescriptor(cls, propertyName);
	}

	/**
	 * 读取cls中属性propertyName的注解annotationClass.
	 * 优先读取getter方法的注解，其次读取setter方法，最后读取属性定义的注解
	 *
	 * @param cls             对象class属性
	 * @param propertyName 属性名
	 * @param annotationClass 注解class属性
	 * @return 注解对象
	 * @param <T> 泛型约束
	 */
	public static <T extends Annotation> T getAnnotation(Class<?> cls, String propertyName, Class<T> annotationClass) {
		PropertyDescriptor pd = Helper.getPropertyDescriptor(cls, propertyName);
		if (pd == null) return null;
		return Helper.getAnnotation(cls, pd, annotationClass);
	}

	/**
	 * 读取cls中属性pd的注解annotationClass. 优先读取getter方法的注解，其次读取setter方法，最后读取属性定义的注解
	 *
	 * @param cls             对象class属性
	 * @param pd 				属性名
	 * @param annotationClass 注解class属性
	 * @return 注解
	 * @param <T> 泛型约束
	 */
	public static <T extends Annotation> T getAnnotation(Class<?> cls, PropertyDescriptor pd,
			Class<T> annotationClass) {
		/* 首先从getter方法中读取注解 */
		Method method = pd.getReadMethod();
		T annotation = method == null ? null : method.getAnnotation(annotationClass);
		if (annotation == null) { // 从setter方法读取注解
			method = pd.getWriteMethod();
			annotation = method == null ? null : method.getAnnotation(annotationClass);
		}
		if (annotation == null) { // 从Field中读取注解
			try {
				Field field = cls.getDeclaredField(pd.getName());
				if (field != null) annotation = field.getAnnotation(annotationClass);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return annotation;
	}

	/**
	 * 得到cls下的除excludes外的所有字段名,得到的这些字段必然有getter/setter方法
	 *
	 * @param cls      对象class属性
	 * @param excludes 不需要的字段
	 * @return {@link String[]}
	 */
	public static String[] getPropertyNames(Class<?> cls, String... excludes) {
		List<String> a = excludes == null ? null : Arrays.asList(excludes);
		PropertyDescriptor[] pds = Helper.getPropertyDescriptors(cls);
		List<String> names = new ArrayList<>();
		for (PropertyDescriptor pd : pds) {
			if (pd.getReadMethod() == null || pd.getWriteMethod() == null) continue;
			if (a != null && a.contains(pd.getName())) continue;
			names.add(pd.getName());
		}
		return names.toArray(new String[names.size()]);
	}

	/*--- Java Bean 相关方法 End ---*/

	/*--- 数组集合相关方法 ---*/

	/**
	 * 将数组o的每个值分别加到集合中
	 *
	 * @param c      集合
	 * @param values 数组或列表等
	 * @return int 增加的数据个数
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int add(Collection c, Object values) {
		if (Helper.isEmpty(values)) return 0;
		int count = Helper.getLength(values);
		int result = 0;
		for (int i = 0; i < count; i++) {
			if (c.add(Helper.getValue(values, i))) result++;
		}
		return result;
	}

	/**
	 *
	 * 将数组o的每个值分别加到集合中
	 *
	 * @param c 集合对象
	 * @param values 数组
	 * @return 增加的数据个数
	 * @param <V> V
	 */
	public static <V> int add(Collection<V> c, V[] values) {
		if (Helper.isEmpty(values)) return 0;
		int count = 0;
		for (V value : values) {
			if (c.add(value)) count++;
		}
		return count;
	}

	/**
	 * 将迭代对象中的数据全部增加到集合中
	 *
	 * @param c        集合
	 * @param iterator 迭代器
	 * @return 增加数据的个数
	 * @param <V> V
	 */
	public static <V> int add(Collection<V> c, Iterator<V> iterator) {
		if (iterator == null) return 0;

		int count = 0;
		while (iterator.hasNext()) {
			if (c.add(iterator.next())) count++;
		}
		return count;
	}

	/**
	 *
	 * @param c        集合
	 * @param iterable 可迭代对象
	 * @return int 增加的数据个数
	 * @see #add(Collection, Iterator)
	 * @param <V> V
	 */
	public static <V> int add(Collection<V> c, Iterable<V> iterable) {
		if (iterable == null) return 0;
		return add(c, iterable.iterator());
	}

	/*--- 数组集合相关方法 End ---*/

	/**
	 * 将JavaBean转换为Map对象,JavaBean下的所有字段应是普通java对象
	 *
	 * @param bean 要转换的JavaBean
	 * @param excludeProperties 排除的属性
	 * @param includeProperties 包含的属性
	 * @return {@link Map}
	 */
	public static Map<String, Object> toMap(Object bean, String[] excludeProperties, String... includeProperties) {
		if (bean == null) return null;
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(getRealClass(bean));

		Set<String> includes = new HashSet<>();
		if (includeProperties != null) Helper.add(includes, includeProperties);
		Set<String> excludes = new HashSet<>();
		if (excludeProperties != null) Helper.add(excludes, excludeProperties);

		String name;
		Object value;
		for (PropertyDescriptor pd : pds) {
			name = pd.getName();
			if (!WildcardHelper.include(excludes, includes, name)) continue;
			value = getValue(bean, pd);
			if (value == null) continue;
			map.put(name, value);
		}
		return map;
	}

	/**
	 * 将JavaBean转换为Map对象,JavaBean下的所有字段应是普通java对象
	 *
	 * @param bean 要转换的JavaBean
	 * @param excludeProperties 排除的属性
	 * @return {@link Map}
	 */
	public static Map<String, Object> toMap(Object bean, String... excludeProperties) {
		return Helper.toMap(bean, excludeProperties, (String[]) null);
	}

	/**
	 * 将Map数据转换为type的实体类
	 *
	 * @param map 数据
	 * @param type 要实例化的Class类型
	 * @return 结果对象
	 * @param <T> 泛型约束
	 */
	public static <T> T toObject(Map<?, ?> map, Class<T> type) {
		return Helper.setValues((T) BeanUtils.instantiateClass(type), map);
	}

	/**
	 * 将普通数组对象转换为Object[]
	 *
	 * @param obj 普通数组对象,如int[]
	 * @return Object[]
	 */
	public static Object[] toArray(Object obj) {
		if (obj instanceof Object[]) return (Object[]) obj;
		if (obj instanceof Collection) return ((Collection<?>) obj).toArray();
		int length = Helper.getLength(obj);
		Object[] arr = new Object[length];
		for (int i = 0; i < length; i++) {
			arr[i] = Helper.getValue(obj, i);
		}
		return arr;
	}

	/** org.hibernate.proxy.HibernateProxyHelper */
	private static final String HibernateProxyHelperClassName = "org.hibernate.proxy.HibernateProxyHelper";

	/**
	 * 得到对象obj的真实class，主要用于Hiberntae实体对象取得真实实体类的class
	 *
	 * @param obj 对象
	 * @return 对象实际class
	 */
	public static Class<?> getRealClass(Object obj) {
		try {
			// 判断obj是否为Hibernate延迟加载对象
			Class<?> cls = Class.forName(HibernateProxyHelperClassName);
			Method method = MethodHelper.get(cls, "getClassWithoutInitializingProxy", Object.class);
			return (Class<?>) MethodHelper.invoke(null, method, obj);
		} catch (ClassNotFoundException e) {
			return obj.getClass();
		}
	}

}
