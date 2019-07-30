package com.fhtiger.utils.helperutils.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * 数字处理工具类
 * 
 * @author LFH
 * @since 2019/7/30 16:00
 * @version 0.0.1
 */
public class NumberHelper {

	/**
	 * 数字正则表达式
	 */
	public static final String NUMERIC_REGEX = "[+\\-]?(\\d+|(\\d{1,3}(,\\d{3})*))(\\.[0-9]+)?";

	/**
	 * 判断cls是否为数字类型的class
	 * 
	 * @param cls class
	 * @return boolean
	 */
	public static boolean isNumber(Class<?> cls) {
		if (cls == Long.TYPE || cls == Integer.TYPE || cls == Float.TYPE || cls == Double.TYPE || cls == Byte.TYPE
				|| cls == Short.TYPE)
			return true;
		return Number.class.isAssignableFrom(cls);
	}

	/**
	 * 判断number是否可转换为Number类型数据
	 * 
	 * @param number 要校验的对象
	 * @return boolean
	 */
	public static boolean isNumber(Object number) {
		if (number == null) return false;
		if (number instanceof Number) return true;
		if (isNumber(number.getClass())) return true;
		return number.toString().trim().matches(NUMERIC_REGEX);
	}

	/**
	 * 判断字符串number是否符合小数格式
	 * 
	 * @param number 疑似数字字符串
	 * @return boolean
	 */
	public static boolean isNumber(String number) {
		if (StringHelper.isEmpty(number)) return false;
		return number.trim().matches(NUMERIC_REGEX);
	}

	/**
	 * 将number转换为数字
	 * 
	 * @param number 数字对象
	 * @return {@link Number}
	 * @throws ParseException 转换错误
	 */
	public static Number parse(Object number) throws ParseException {
		if (isNumber(number.getClass())) return (Number) number;
		NumberFormat numberFormat = NumberFormat.getInstance();
		return numberFormat.parse(StringHelper.trim(number));
	}

	/**
	 * 将其他类型返回double,转换失败则返回defaultValue
	 * 
	 * @param number       数字对象
	 * @param defaultValue 如果number不是数字则返回该数字
	 * @return double
	 */
	public static double doubleValue(Object number, double defaultValue) {
		if (number == null) return defaultValue;
		try {
			return parse(number).doubleValue();
		} catch (ParseException ex) {
			return defaultValue;
		}
	}

	/**
	 * 将其他类型返回double,转换失败则返回0
	 * 
	 * @param number 数字对象
	 * @return double
	 */
	public static double doubleValue(Object number) {
		return doubleValue(number, 0);
	}

	/**
	 * 将其他类型返回float
	 * 
	 * @param number 数字对象
	 * @return float
	 */
	public static float floatValue(Object number) {
		return (float) doubleValue(number);
	}

	/**
	 * 将其他类型返回float
	 * 
	 * @param number 数字对象
	 * @param defaultValue number不为数字时的返回值
	 * @return float
	 */
	public static float floatValue(Object number, float defaultValue) {
		return (float) doubleValue(number, defaultValue);
	}

	/**
	 * 将其他类型返回long
	 * 
	 * @param number 数字对象
	 * @return long
	 */
	public static long longValue(Object number) {
		return longValue(number, 0);
	}

	/**
	 * 将其他类型返回long,如果不是数字则返回defaultValue
	 * 
	 * @param number 数字对象
	 * @param defaultValue number不为数字时的返回值
	 * @return long
	 */
	public static long longValue(Object number, long defaultValue) {
		if (number == null) return defaultValue;
		try {
			return parse(number).longValue();
		} catch (ParseException ex) {
			return defaultValue;
		}
	}

	/**
	 * 将其他类型返回int
	 * 
	 * @param number 数字对象
	 * @return int
	 */
	public static int intValue(Object number) {
		return intValue(number, 0);
	}

	/**
	 * 将其他类型返回int
	 * 
	 * @param number 数字对象
	 * @param defaultValue number不为数字时的返回值
	 * @return int
	 */
	public static int intValue(Object number, int defaultValue) {
		if (number == null) return defaultValue;
		try {
			return parse(number).intValue();
		} catch (ParseException ex) {
			return defaultValue;
		}
	}

	/**
	 * 格式化数字number为指定的格式
	 * 
	 * @param number 数字对象
	 * @param pattern 数字转换格式
	 * @return String 格式化后的字符串
	 */
	public static String format(Object number, String pattern) {
		DecimalFormat numberFormat = new DecimalFormat(pattern);
		try {
			return numberFormat.format(parse(number));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 格式化数字
	 * 
	 * @param number 数字对象
	 * @param minFractionDigits 最少保留的小数位数
	 * @param maxFractionDigits 最大保留的小数位数
	 * @param groupingUsed 是否使用千分符
	 * @return 格式化后的字符串
	 */
	public static String format(Object number, int minFractionDigits, int maxFractionDigits, boolean groupingUsed) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(minFractionDigits);
		numberFormat.setMaximumFractionDigits(maxFractionDigits);
		numberFormat.setRoundingMode(RoundingMode.HALF_UP);
		numberFormat.setGroupingUsed(groupingUsed);
		try {
			return numberFormat.format(parse(number));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 格式化小数
	 * 
	 * @param number 数字对象
	 * @param fractionDigits 小数位数
	 * @param groupingUsed 是否使用千分符
	 * @return 格式化后的字符串
	 */
	public static String format(Object number, int fractionDigits, boolean groupingUsed) {
		return format(number, fractionDigits, fractionDigits, groupingUsed);
	}

	/**
	 * 格式化小数,使用默认千分符
	 * 
	 * @param number 数字对象
	 * @param fractionDigits 小数位数
	 * @return String
	 */
	public static String format(Object number, int fractionDigits) {
		return format(number, fractionDigits, true);
	}

	/** 默认保留的小数位数,2位小数 */
	public static final int DEFAULT_FRACTION_DIGITS = 2;

	/**
	 * 格式化小数,保留2位小数
	 * 
	 * @param number 数字对象
	 * @param separator 是否保留分隔符
	 * @see #DEFAULT_FRACTION_DIGITS
	 * @return String
	 */
	public static String format(Object number, boolean separator) {
		return format(number, DEFAULT_FRACTION_DIGITS, separator);
	}

	/**
	 * 格式化小数,保留2位小数
	 * 
	 * @param number 数字对象
	 * @see #DEFAULT_FRACTION_DIGITS
	 * @return String
	 */
	public static String format(Object number) {
		return format(number, DEFAULT_FRACTION_DIGITS, true);
	}

	/**
	 * 
	 * 将number格式化为整数形式
	 * 
	 * @param number 数字对象
	 * @param separator 是否使用千分符
	 * @return String
	 */
	public static String formatInteger(Object number, boolean separator) {
		return format(number, 0, separator);
	}

	/**
	 * 格式化为整数形式
	 * 
	 * @param number 数字对象
	 * @return String
	 */
	public static String formatInteger(Object number) {
		return formatInteger(number, true);
	}

	/**
	 * 将byte数字b转换为16进制数字,注意byte数字仅保留2位数字
	 * 
	 * @param b 字节
	 * @return String
	 */
	public static String toHexString(byte b) {
		String text = Integer.toHexString(b & 0xFF);
		return StringHelper.prefix(text, 2, "0");
	}

	/**
	 * 将bytes数组转换为16进制字符串，每个byte数字仅保留2位数字
	 * 
	 * @param bytes 字节数组
	 * @return String
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder buffer = new StringBuilder();
		for (byte b : bytes) {
			buffer.append(toHexString(b));
		}
		return buffer.toString();
	}

	/**
	 * a+b,null视为0,此方法最少保留2位精度
	 * 
	 * @param a 源数字
	 * @param b 做加法的数字
	 * @return a+b
	 */
	public static BigDecimal add(BigDecimal a, BigDecimal b) {
		if (a == null) return b;
		if (b == null) return a;
		int scale = Math.max(a.scale(), b.scale());
		BigDecimal r = a.add(b);
		if (scale < 2) {
			r= r.setScale(Math.max(scale, 2), RoundingMode.HALF_UP);
		}
		return r;
	}

	/**
	 * a+b,,此方法保留scale位精度
	 * 
	 * @param a 源数字
	 * @param b 做加法的数字
	 * @param scale 精度
	 * @return a+b
	 */
	public static BigDecimal add(BigDecimal a, BigDecimal b, int scale) {
		if (a == null) return b;
		if (b == null) return a;
		BigDecimal r = a.add(b);
		return r.setScale(scale, RoundingMode.HALF_UP);
	}

	/**
	 * a-b,null视为0,此方法最少保留2位精度
	 * 
	 * @param a 源数字
	 * @param b 做减法的数字
	 * @return a-b
	 */
	public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
		if (a == null) return b.negate();
		if (b == null) return a;
		int scale = Math.max(a.scale(), b.scale());
		BigDecimal r = a.subtract(b);
		if (scale < 2) {
			return r.setScale(Math.max(scale, 2), RoundingMode.HALF_UP);
		}
		return r;
	}

	/**
	 * a-b,此方法保留scale位精度
	 * 
	 * @param a 源数字
	 * @param b 做减法的数字
	 * @param scale 精度
	 * @return {{@link #subtract(BigDecimal, BigDecimal)}}
	 */
	public static BigDecimal subtract(BigDecimal a, BigDecimal b, int scale) {
		if (a == null) return b.negate();
		if (b == null) return a;
		BigDecimal r = a.subtract(b);
		return r.setScale(scale, RoundingMode.HALF_UP);
	}

	/**
	 * a*b,null视为0,此方法最少保留2位精度
	 * 
	 * @param a 源数字
	 * @param b 做乘法的数字
	 * @return a*b
	 */
	public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) return BigDecimal.ZERO;
		int scale = a.scale() + b.scale();
		BigDecimal r = a.multiply(b);
		if (scale < 2) {
			return r.setScale(Math.max(scale, 2), RoundingMode.HALF_UP);
		}
		return r;
	}

	/**
	 * a*b,null视为0,此方法保留scale位精度
	 * 
	 * @param a 源数字
	 * @param b 做乘法的数字
	 * @param scale 精度
	 * @return {{@link #multiply(BigDecimal, BigDecimal)}}
	 */
	public static BigDecimal multiply(BigDecimal a, BigDecimal b, int scale) {
		if (a == null || b == null) return BigDecimal.ZERO;
		BigDecimal r = a.multiply(b);
		return r.setScale(scale, RoundingMode.HALF_UP);
	}

	/**
	 * a/b,null视为0,此方法最少保留2位精度
	 * 
	 * @param a 被除数
	 * @param b 除数
	 * @return a/b
	 */
	public static BigDecimal divide(BigDecimal a, BigDecimal b) {
		if (a == null) a = BigDecimal.ZERO;
		if (b == null) b = BigDecimal.ZERO;
		int scale = Math.max(a.scale(), b.scale());
		return a.divide(b, Math.max(scale, 2), RoundingMode.HALF_UP);
	}

	/**
	 * a/b,null视为0,此方法保留scale位精度
	 * 
	 * @param a 被除数
	 * @param b 除数
	 * @param scale 精度
	 * @return {{@link #divide(BigDecimal, BigDecimal)}}
	 */
	public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
		if (a == null) a = BigDecimal.ZERO;
		if (b == null) b = BigDecimal.ZERO;
		return a.divide(b, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 比较a和b两个数字的大小，精度为scale
	 * 
	 * @param a 左数字
	 * @param b 右数字
	 * @param scale 精度
	 * @return a.compareTo(b)
	 */
	public static int compare(BigDecimal a, BigDecimal b, int scale) {
		if (a == null) a = BigDecimal.ZERO;
		if (b == null) b = BigDecimal.ZERO;
		BigDecimal aCopy = a.setScale(scale, BigDecimal.ROUND_HALF_UP);
		BigDecimal bCopy = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
		return aCopy.compareTo(bCopy);
	}

	/**
	 * 比较a和b两个数字的大小，小数位数为2
	 * 
	 * @param a 左数字
	 * @param b 右数字
	 * @return a.comapreTo(b)
	 */
	public static int compare(BigDecimal a, BigDecimal b) {
		return NumberHelper.compare(a, b, 2);
	}

	/**
	 * 比较a和b两个数字的大小，精度为scale
	 * 
	 * @param a 左数字
	 * @param b 右数字
	 * @param scale 精度
	 * @return a.compareTo(b)
	 */
	public static int compare(Number a, Number b, int scale) {
		if (a == null) a = 0;
		if (b == null) b = 0;
		String aStr = NumberHelper.format(a, scale, false);
		String bStr = NumberHelper.format(b, scale, false);
		return compare(new BigDecimal(aStr), new BigDecimal(bStr), scale);
	}

	/**
	 * 取a和b之间的小者
	 * 
	 * @param a 数字a
	 * @param b 数字b
	 * @return min
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T min(T a, T b) {
		if (a instanceof Comparable) {
			Comparable<T> comparable = (Comparable<T>) a;
			return comparable.compareTo(b) < 0 ? a : b;
		}
		return a.doubleValue() < b.doubleValue() ? a : b;
	}

	/**
	 * 取a和b之间的大者
	 * 
	 * @param a 数字a
	 * @param b 数字b
	 * @return max
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T max(T a, T b) {
		if (a instanceof Comparable) {
			Comparable<T> comparable = (Comparable<T>) a;
			return comparable.compareTo(b) > 0 ? a : b;
		}
		return a.doubleValue() > b.doubleValue() ? a : b;
	}

}
