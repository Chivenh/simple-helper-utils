package com.fhtiger.helper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 专用调试与小工具类
 * @author Chivenh:
 * @version 1.0
 * @since 完善日期2017-4-13 时间11:23:02
 */
@SuppressWarnings({ "unused" })
public final class SpecialUtil {

	private static final Logger logger = LoggerFactory.getLogger(SpecialUtil.class);

	private SpecialUtil()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 写json文件
	 *
	 * @param filePath 文件地址
	 * @param sets json字符串
	 * @throws IOException -
	 * @author Chivenh
	 * @since 2017年12月9日 上午9:40:11
	 */
	public static void writeFile(String filePath, String sets) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		PrintWriter out = new PrintWriter(fw);
		out.write(sets);
		out.println();
		fw.close();
		out.close();
	}

	private static final String[] BIG_NUMBER = new String[] { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };

	/**
	 * 获取1-10的数字汉字表示.
	 *
	 * @param index 数字
	 * @return String
	 * @author Chivenh
	 * @since 2017年12月11日 下午3:39:17
	 */
	public static String getBigNumber(int index) {
		if (index > 0 && index < BIG_NUMBER.length) {
			return BIG_NUMBER[index - 1];
		} else {
			return "";
		}
	}

	/**
	 * 通用toString()方法
	 * @param obj 对象
	 * @return String
	 * @author Chivenh
	 * @since 2017年4月21日 下午1:11:53
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String toString(Object obj) {
		ArrayList<Object> visited = new ArrayList<>();
		if (obj == null) {
			return null;
		}
		if (visited.contains(obj)) {
			return "...";
		}
		visited.add(obj);
		Class c1 = obj.getClass();
		if (c1 == String.class) {
			return (String) obj;
		}
		if (c1.isArray()) {
			StringBuilder r = new StringBuilder(c1.getComponentType() + "[]{");
			for (int i = 0; i < Array.getLength(obj); i++) {
				if (i > 0) {
					r.append(",");
				}
				Object val = Array.get(obj, i);
				if (c1.getComponentType().isPrimitive()) {r.append(val);}
				else {r.append(toString(val));}
			}
			return r + "}";
		}
		StringBuilder r = new StringBuilder(c1.getName());
		r.append("{");
		do {
			r.append("[");
			Field[] fields = c1.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (Field f : fields) {
				if (!Modifier.isStatic(f.getModifiers())) {
					if (!r.toString().endsWith("[")) {
						r.append(",\n");
					}
					r.append(f.getName()).append(" = ");
					try {
						Class t = f.getType();
						Object val = f.get(obj);
						if (t.isPrimitive()) {
							r.append(val);
						} else {
							r.append(toString(val));
						}
					} catch (Exception e) {
						logger.error("TimeUtil error: ",e);
					}
				}
			}
			r.append("]");
			c1 = c1.getSuperclass();
		} while (c1 != null);
		r.append("}");
		return r.toString().replaceAll("\\[]", "");
	}

	/**
	 * 得到字符串
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return String
	 */
	public static String getStr(Object o, String... v) {
		return o == null ? (v.length < 1 ? "" : v[0]) : o.toString().trim();
	}

	/**
	 * 得到整数
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return Integer
	 */
	public static int getInt(Object o, int... v) {
		int t;
		try {
			o = o == null || o.toString().trim().isEmpty() ? (v.length < 1 ? 0 : v[0]) : o.toString().trim();
			t = Integer.parseInt(o.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return t;
	}

	/**
	 * 得到BigDecimal.
	 *
	 * @param o 原始值
	 * @return BigDecimal
	 */
	public static BigDecimal getBigDecimal(Object o) {
		try {
			if (o == null || o.toString().trim().isEmpty()) {
				return null;
			} else {
				return new BigDecimal(getStr(o));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 得到整数
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return Long
	 */
	public static long getLong(Object o, int... v) {
		long t = 0L;
		try {
			o = o == null || o.toString().trim().isEmpty() ? (v.length < 1 ? 0 : v[0]) : o.toString().trim();
			t = Long.parseLong(o.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return t;
	}

	/**
	 * 得到浮点数.
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return Double
	 */
	public static double getDouble(Object o, double... v) {
		double t;
		try {
			o = o == null || o.toString().trim().isEmpty() ? (v.length < 1 ? 0.0 : v[0]) : o.toString().trim();
			t = Double.parseDouble(o.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return t;
	}

	/**
	 * 得到排序字符串.
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return String
	 */
	public static String getOrder(Object o, String... v) {
		return o == null || o.toString().trim().isEmpty()
				? (v.length < 1 ? "ASC" : (v[0].length() < 3 ? "ASC" : ("asc,desc".contains(v[0])) ? v[0] : "ASC"))
				: ("asc,desc".contains(o.toString().trim()) ? o.toString().trim() : "ASC");
	}

	/**
	 * 对多个字符串值进行数值求和
	 * <b>不限整数或浮点数</b>
	 *
	 * @param v1 必填参数1
	 * @param v2 可选参数列表2
	 * @return 和
	 * @author Chivenh
	 * @since 2020-04-26 15:42
	 */
	public Object getSum(String v1, String... v2) {
		String a1 = v1;
		String[] a2={};
		a1 = a1 == null || a1.isEmpty() || "null".equalsIgnoreCase(a1) ? "0" : a1.trim();
		if (v2 != null && v2.length > 0) {
			a2 = v2.clone();
			int a2Length = a2.length;
			for (int i = 0; i < a2Length; i++) {
				a2[i] = a2[i] == null || "".equals(a2[i]) || "null".equalsIgnoreCase(a2[i]) ? "0" : a2[i].trim();
			}
		}
		Object s = "";
		double x1 = 0.0, x2 = 0.0, xs = 0.0;
		int y1 = 0, y2 = 0, ys = 0;
		try {
			final String splitSpot=".";
			if (a1.indexOf(splitSpot) > 0) {
				x1 += Double.parseDouble(a1);
			} else {
				y1 += Integer.parseInt(a1);
			}
			for (String x : a2) {
				if (x.indexOf(splitSpot) > 0) {
					x2 += Double.parseDouble(x);
				} else {
					y2 += Integer.parseInt(x);
				}
			}
			xs = x1 + x2;
			ys = y1 + y2;
			if (xs > 0 && ys > 0) {
				s = (xs + ys);
			} else if (xs > 0 && ys == 0) {
				s =  xs;
			} else {
				s =  ys;
			}
		} catch (Exception e) {
			logger.error("TimeUtil error: ",e);
		}
		return s;
	}

	/**
	 * 简单判断是否为空(空则真|反之假.)
	 *
	 * @param o 值
	 * @return Boolean
	 */
	public static boolean isNull(Object o) {
		if (o == null) {
			return true;
		} else {
			return o instanceof String && o.toString().trim().isEmpty();
		}
	}
	/**
	 * <p>Checks if a String is empty ("") or null.</p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>NOTE: This method changed in Lang version 2.0.
	 * It no longer trims the String.
	 * That functionality is available in isBlank().</p>
	 *
	 * @param str  the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || str.isEmpty();
	}

	/**
	 * <p>Checks if a String is not empty ("") and not null.</p>
	 *
	 * <pre>
	 * StringUtils.isNotEmpty(null)      = false
	 * StringUtils.isNotEmpty("")        = false
	 * StringUtils.isNotEmpty(" ")       = true
	 * StringUtils.isNotEmpty("bob")     = true
	 * StringUtils.isNotEmpty("  bob  ") = true
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null
	 */
	public static boolean isNotEmpty(CharSequence str) {
		return !SpecialUtil.isEmpty(str);
	}

	/**
	 * <p>Checks if a String is whitespace, empty ("") or null.</p>
	 *
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 * @since 2.0
	 */
	public static boolean isBlank(CharSequence str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((!Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
	 *
	 * <pre>
	 * StringUtils.isNotBlank(null)      = false
	 * StringUtils.isNotBlank("")        = false
	 * StringUtils.isNotBlank(" ")       = false
	 * StringUtils.isNotBlank("bob")     = true
	 * StringUtils.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @return <code>true</code> if the String is
	 *  not empty and not null and not whitespace
	 * @since 2.0
	 */
	public static boolean isNotBlank(CharSequence str) {
		return !SpecialUtil.isBlank(str);
	}

	/**
	 * 调用序列化方法，深克隆一个对象副本
	 * @param preObj 前置对象
	 * @return 结果对象
	 * @author Chivenh
	 * @since 2020-04-26 15:30
	 * @param <T> 参数类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T preObj) {
		Object nowObj = null;
		Closeable[] streams = new Closeable[4];
		try {
			/* 将对象写到流里*/
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			streams[0]=bos;
			streams[1]=oos;
			oos.writeObject(preObj);
			/* 从流里读回来*/
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois =  new ObjectInputStream(bis);
			streams[2]=bis;
			streams[3]=ois;
			nowObj = ois.readObject();
		} catch (Exception e) {
			logger.error("TimeUtil error: ",e);
		}finally {
			try {
				for (Closeable stream : streams) {
					if(stream!=null){
						stream.close();
					}
				}
			} catch (IOException e) {
				logger.error("TimeUtil error: ",e);
			}
		}
		return (T)nowObj;
	}

	/**
	 * 自调用空构造器以前置对象为模板创建新对象
	 * @param pre 前置对象
	 * @return 结果对象
	 * @param <T> 结果类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cloneByProps(T pre) {
		Class<?> type = pre.getClass();
		Object cur;
		try {
			cur = type.getDeclaredConstructor().newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			/*获取属性数组*/
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				/*获取属性名*/
				String propertyName = pd.getName();
				if (!"class".equalsIgnoreCase(propertyName)) {
					/*得到读属性方法(get...())*/
					Method get = pd.getReadMethod();
					Method set = pd.getWriteMethod();
					if (get != null) {
						/*获取属性值*/
						Object value = get.invoke(pre);
						if (value != null && set != null) {
							set.invoke(cur, value);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(type.getName() + " 没有可用构造器,克隆对象失败!" + e.getMessage());
		}
		return (T)cur;
	}

	/**
	 * 以读写属性的方法复制对象.
	 *
	 * @param pre 前置对象
	 * @param cur 当前对象
	 * @return 当前对象
	 * @author Chivenh
	 * @since 2020-04-26 15:28
	 * @param <T> 结果类型
	 */
	public static <T> T copyProps(T pre, T cur) {
		if (pre.getClass() != cur.getClass()) {
			return cur;
		}
		Class<?> type = pre.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			/*获取属性数组*/
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				/*获取属性名*/
				String propertyName = pd.getName();
				if (!"class".equalsIgnoreCase(propertyName)) {
					/*得到读属性方法(get...())*/
					Method get = pd.getReadMethod();
					Method set = pd.getWriteMethod();
					if (get != null) {
						/*获取属性值*/
						Object value = get.invoke(pre);
						if (value != null && set != null) {
							set.invoke(cur, value);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(type.getName() + " 没有可用构造器,克隆对象失败!" + e.getMessage());
		}
		return cur;
	}

	/**
	 * 对象转Map输出
	 *
	 * @param obj 对象
	 * @return {@link Map} 对应对象属性名和值
	 * @author Chivenh
	 * @since 2020-04-26 15:27
	 */
	public static Map<String, Object> objToMap(Object obj) {
		Map<String, Object> map = new HashMap<>(0);
		try {
			Class<?> type = obj.getClass();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			/*获取属性数组*/
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				/*获取属性名*/
				String propertyName = pd.getName();
				if (!"class".equalsIgnoreCase(propertyName)) {
					/*得到读属性方法(get...())*/
					Method method = pd.getReadMethod();
					if(method==null){
						continue;
					}
					/*获取属性值*/
					Object o = method.invoke(obj);
					/*填充进map*/
					map.put(propertyName, o);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return map;
	}

	/**
	 * {@link Map} 转为对应对象
	 * .反之Object转Map 有方法:#objToMap(Object)
	 * @param type Class
	 * @param map Map
	 * @return 结果对象
	 * @author Chivenh
	 * @since 2017年5月31日 下午3:07:43
	 * @param <T> 结果类型
	 */
	public static <T> T mapToObj(Class<T> type, Map<String, Object> map) {
		T newObj;
		try {
			newObj = type.getDeclaredConstructor().newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			/*获取属性数组*/
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				/*获取属性名*/
				String propertyName = pd.getName();
				if (!"class".equalsIgnoreCase(propertyName)&&map.containsKey(propertyName)) {
					/*得到写属性方法(set...())*/
					Method method = pd.getWriteMethod();
					if(method==null){
						continue;
					}
					/* 从map中获取对应属性值*/
					Object value = map.get(propertyName);
					/* 写入属性值.*/
					method.invoke(newObj, value);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return newObj;
	}

	/**
	 * 判断该字符串是否为整型
	 *
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean isInt(String str) {
		return Pattern.matches("\\d+", str);
	}
}