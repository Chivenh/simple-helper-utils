package com.fhtiger.utils.helperutils.helper;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 主要是对日期/时间的处理,包括对传入的字符串进行特定格式的转换
 * 
 * @author LFH
 * @since 2019/7/30 12:57
 * @version 0.0.1
 */
public class DateHelper {
	/** 系统默认的日期格式 yyyy-MM-dd */
	public static final String DEFAULT_DATE_FORMAT = SystemHelper.getProperty("date.format", "yyyy-MM-dd");

	/** 默认的时间格式 HH:mm:ss */
	public static final String DEFAULT_TIME_FORMAT = SystemHelper.getProperty("time.format", "HH:mm:ss");

	/** 默认的日期时间格式 yyyy-MM-dd HH:mm:ss */
	public static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;

	/** 默认的带毫秒的日期时间格式 yyyy-MM-dd HH:mm:ss.SSS */
	public static final String DEFAULT_TIMESTAMP_FORMAT = DEFAULT_DATETIME_FORMAT + ".SSS";

	/** yyyy-MM-dd'T'HH:mm:ss */
	public static final String RFC3339_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * 所有可能进行转换的日期格式
	 */
	private static final Set<String> ALL_FORMATS = new LinkedHashSet<String>();

	static {
		ALL_FORMATS.add(RFC3339_DATETIME + ".SSS");
		ALL_FORMATS.add(RFC3339_DATETIME);
		ALL_FORMATS.add("yyyy-MM-dd HH:mm:ss.SSS");
		ALL_FORMATS.add("yyyy-MM-dd HH:mm:ss");
		ALL_FORMATS.add("yyyy-MM-dd HH:mm");
		ALL_FORMATS.add("yyyy-MM-dd HH");
		ALL_FORMATS.add("yyyy-MM-dd");
		ALL_FORMATS.add("yyyy-MM");
		ALL_FORMATS.add("yyyy/MM/dd HH:mm:ss.SSS");
		ALL_FORMATS.add("yyyy/MM/dd HH:mm:ss");
		ALL_FORMATS.add("yyyy/MM/dd HH:mm");
		ALL_FORMATS.add("yyyy/MM/dd HH");
		ALL_FORMATS.add("yyyy/MM/dd");
		ALL_FORMATS.add("yyyy/MM");
		ALL_FORMATS.add("yyyy\\MM\\dd HH:mm:ss.SSS");
		ALL_FORMATS.add("yyyy\\MM\\dd HH:mm:ss");
		ALL_FORMATS.add("yyyy\\MM\\dd HH:mm");
		ALL_FORMATS.add("yyyy\\MM\\dd HH");
		ALL_FORMATS.add("yyyy\\MM\\dd");
		ALL_FORMATS.add("yyyy\\MM");
		ALL_FORMATS.add("yyyy.MM.dd HH:mm:ss.SSS");
		ALL_FORMATS.add("yyyy.MM.dd HH:mm:ss");
		ALL_FORMATS.add("yyyy.MM.dd HH:mm");
		ALL_FORMATS.add("yyyy.MM.dd HHmmssSSS");
		ALL_FORMATS.add("yyyy.MM.dd HHmmss");
		ALL_FORMATS.add("yyyy.MM.dd HHmm");
		ALL_FORMATS.add("yyyy.MM.dd HH");
		ALL_FORMATS.add("yyyy.MM.dd");
		ALL_FORMATS.add("yyyy.MM");
		ALL_FORMATS.add("yyyyMMdd:HHmmssSSS");
		ALL_FORMATS.add("yyyyMMdd:HHmmss");
		ALL_FORMATS.add("yyyyMMdd:HHmm");
		ALL_FORMATS.add("yyyyMMdd:HH");
		ALL_FORMATS.add("yyyyMMdd HHmmssSSS");
		ALL_FORMATS.add("yyyyMMdd HHmmss");
		ALL_FORMATS.add("yyyyMMdd HHmm");
		ALL_FORMATS.add("yyyyMMdd HH");
		ALL_FORMATS.add("yyyyMMddHHmmssSSS");
		ALL_FORMATS.add("yyyyMMddHHmmss");
		ALL_FORMATS.add("yyyyMMddHHmm");
		ALL_FORMATS.add("yyyyMMdd");
		ALL_FORMATS.add("yyyyMM");
		ALL_FORMATS.add("yyyy年MM月dd日HH时mm分ss秒SSS毫秒");
		ALL_FORMATS.add("yyyy年MM月dd日HH时mm分ss秒");
		ALL_FORMATS.add("yyyy年MM月dd日HH时mm分");
		ALL_FORMATS.add("yyyy年MM月dd日HH时");
		ALL_FORMATS.add("yyyy年MM月dd日");
		ALL_FORMATS.add("yyyy年MM月");
		ALL_FORMATS.add("yyyy年");
	}

	/**
	 * 将Date类型日期转换成新的LocalDate
	 * @param date 要转换的{@link Date}日期
	 * @return {@link LocalDate}
	 */
	public static LocalDate DateToLocaleDate(Date date) {

		Instant instant = date.toInstant();

		ZoneId zoneId  = ZoneId.systemDefault();

		return instant.atZone(zoneId).toLocalDate();

	}

	/**
	 * 将LocalDate日期转换为Date日期对象
	 * @param localDate 要转换的{@link LocalDate}日期
	 * @return {@link Date}
	 */
	public static Date LocalDateToDate(LocalDate localDate) {

		ZoneId zoneId = ZoneId.systemDefault();

		ChronoZonedDateTime<LocalDate> zonedDateTime = localDate.atStartOfDay(zoneId);

		return Date.from(zonedDateTime.toInstant());

	}

	/**
	 * 
	 * 将日期字符串source按指定的格式pattern转换为Date类型
	 * 
	 * @param source 要转换的日期字符串
	 * @param pattern 日期格式字符串
	 * @return 转换后的日期 {@link Date}
	 * @throws ParseException 无法按指定格式转换
	 */
	public static Date parse(String source, String pattern) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		Date date = new SimpleDateFormat(pattern).parse(source, pos);
		ALL_FORMATS.add(pattern);
		if (pos.getIndex() == 0 || pos.getIndex() < source.length()) {// 必须是全字匹配
			throw new ParseException("Unparseable date: \"" + source + "\"", pos.getErrorIndex());
		}
		return date;
	}

	/**
	 * 转换source为日期类型,尝试所有默认的日期格式
	 * 
	 * @param source 日期字符串
	 * @return 转换后的日期 {@link Date}
	 * @see #parse(String)
	 */
	public static Date parse(Object source) {
		if (source == null) return null;
		if (source instanceof Date) return (Date) source;
		return parse(source.toString());
	}

	/**
	 * 转换source为日期类型,尝试所有默认的日期格式
	 * 
	 * @param source 日期字符串
	 * @return 转换后的日期 {@link Date}
	 * @see #parse(String, String)
	 */
	public static Date parse(String source) {
		if (StringHelper.isEmpty(source)) return null;
		Date date = null;
		for (String pattern : ALL_FORMATS) {
			ParsePosition pos = new ParsePosition(0);
			date = new SimpleDateFormat(pattern).parse(source, pos);
			if (pos.getIndex() == 0 || pos.getIndex() < source.length()) {
				// 匹配不正确，则继续下一种格式
				continue;
			}
			return date;
		}
		return null;
	}

	/**
	 * 格式化date为pattern格式的字符串
	 * 
	 * @param date Date 日期
	 * @param pattern String 日期时间格式
	 * @return 转换后的日期时间字符串
	 * @see SimpleDateFormat#SimpleDateFormat(String)
	 * @see SimpleDateFormat#format(Date)
	 */
	public static String format(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 将source表示的日期转换为pattern格式的字符串
	 * 
	 * @param source 表示日期的字符串
	 * @param pattern 转换后的格式
	 * @return 转换后的日期时间字符串
	 */
	public static String format(String source, String pattern) {
		Date date = parse(source);
		if (date == null) return "";
		return format(date, pattern);
	}

	/**
	 * 
	 * 将date日期转换为默认日期格式yyyy-MM-dd的字符串
	 * 
	 * @param date 要转换的日期
	 * @return {@link String}
	 * @see #DEFAULT_DATE_FORMAT
	 */
	public static String format(Date date) {
		return format(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 
	 * 将source表示的日期转换为默认日期格式yyyy-MM-dd的字符串
	 * 
	 * @param source 字符串表示的日期
	 * @return {@link String}
	 */
	public static String format(String source) {
		return format(source, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 格式化当前时间
	 * 
	 * @return {@link String}
	 * @see #format(Date)
	 * @see #DEFAULT_DATETIME_FORMAT
	 */
	public static String now() {
		return format(new Date(), DEFAULT_DATETIME_FORMAT);
	}

	/**
	 * 格式化当前日期
	 * 
	 * @return {@link String}
	 * @see #format(Date)
	 */
	public static String nowDate() {
		return format(new Date());
	}

	/**
	 * 获取当前年份
	 * 
	 * @return int
	 */
	public static int getYear() {
		return get(Calendar.YEAR);
	}

	/**
	 * 获取当前月份,从1开始
	 * 
	 * @return int
	 */
	public static int getMonth() {
		return get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当前日期值
	 * 
	 * @return int
	 */
	public static int getDay() {
		return get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取当前小时,24小时制
	 * 
	 * @return int
	 */
	public static int getHour() {
		return get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 得到当前时间中指定的参数
	 * 
	 * @param field 指定参数
	 * @return int
	 * @see Calendar#get(int)
	 */
	public static int get(int field) {
		return get(new Date(), field);
	}

	/**
	 * 根据日期得到对应的Calendar对象.如果date为null,则返回的是当前日期
	 * 
	 * @param date 日期
	 * @return {@link Calendar}
	 */
	public static Calendar getCalendar(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) c.setTime(date);
		return c;
	}

	/**
	 * 得到日期date中的key数据段
	 * 
	 * @param date 日期
	 * @param field 参考Calendar.get()的参数
	 * @return int
	 */
	public static int get(Date date, int field) {
		Calendar c = getCalendar(date);
		return c.get(field);
	}

	/**
	 * 在日期对象上增加对应字段.
	 * 
	 * @param date 要改变的日期
	 * @param field 表示增加年/月/日/时/分/秒等
	 * @param amount 可以为负数
	 * @return {@link Date} 新的日期对象
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar c = getCalendar(date);
		c.add(field, amount);
		return date = c.getTime();
	}

	/**
	 * 在日期date上增加days天
	 * 
	 * @param date 日期
	 * @param days 要增加的天数,可以为负数
	 * @return {@link Date} 新的日期对象
	 */
	public static Date add(Date date, int days) {
		return add(date, Calendar.DAY_OF_MONTH, days);
	}

	/**
	 * 改变date中指定自读的值
	 * 
	 * @param date 要修改的日期
	 * @param field 字段
	 * @param amount 值,可以为负数
	 * @return {@link Date} 新的日期对象
	 */
	public static Date set(Date date, int field, int amount) {
		Calendar c = getCalendar(date);
		c.set(field, amount);
		return date = c.getTime();
	}

	private static final int[] FIELDS = { Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
			Calendar.SECOND, Calendar.MILLISECOND };

	/**
	 * 根据参数得到一个日期
	 * 
	 * @param dates 依次为年/月(从0开始)/日(从1开始)/时/分/秒/毫秒.为负数则表示使用当前的时间
	 * @return {@link Date}
	 */
	public static Date date(int... dates) {
		Calendar c = Calendar.getInstance();
		if (dates != null && dates.length > 0) {
			int[] a = { 0, 0, 1, 0, 0, 0, 0 };
			System.arraycopy(dates, 0, a, 0, dates.length);
			for (int i = 0, l = a.length; i < l; i++) {
				if (a[i] < 0) continue;
				c.set(FIELDS[i], a[i]);
			}
		}
		return c.getTime();
	}

	/**
	 * 清除date中指定的字段
	 * 
	 * @param date 要清除的日期
	 * @param field 指定字段
	 * @return {@link Date} 清理后的日期对象
	 * @see Calendar#clear(int)
	 */
	public static Date clear(Date date, int field) {
		Calendar c = getCalendar(date);
		c.clear(field);
		return c.getTime();
	}

	/** 删除日期 */
	public static final char CLEAR_DATE = 'D';
	/** 删除时间 */
	public static final char CLEAR_TIME = 'T';
	/** 全部删除 */
	public static final char CLEAR_ALL = 'A';

	/**
	 * @param date  要清除的日期
	 * @param field D/d清理日期,T/t:清理时间,其他则全部清理
	 * @return 清理后的日期
	 * @see Calendar#clear()
	 * @see Calendar#clear(int)
	 */
	public static Date clear(Date date, char field) {
		Calendar c = getCalendar(date);
		field = field>'Z'?(char)(field-32):field;
		switch (field) {
		case CLEAR_DATE:
			// 清理日期
			c.clear(Calendar.ERA);
			c.clear(Calendar.YEAR);
			c.clear(Calendar.MONTH);
			c.clear(Calendar.WEEK_OF_YEAR);
			c.clear(Calendar.WEEK_OF_MONTH);
			c.clear(Calendar.DATE);
			// c.clear(Calendar.DAY_OF_MONTH);
			c.clear(Calendar.DAY_OF_YEAR);
			c.clear(Calendar.DAY_OF_WEEK);
			c.clear(Calendar.DAY_OF_WEEK_IN_MONTH);
			break;
		case CLEAR_TIME:
			// 清理时间
			c.clear(Calendar.AM_PM);
			c.clear(Calendar.HOUR);
			c.clear(Calendar.HOUR_OF_DAY);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.SECOND);
			c.clear(Calendar.MILLISECOND);
			c.clear(Calendar.DST_OFFSET);
			c.clear(Calendar.ZONE_OFFSET);
			break;
		case CLEAR_ALL:
			c.clear();
		}
		return c.getTime();
	}

	/**
	 * 清除date的时间
	 * 
	 * @param date 要清除的日期
	 * @return 返回纯日期(不含时间)
	 */
	public static Date clear(Date date) {
		return clear(date, CLEAR_TIME);
	}

	/**
	 * 将date的时间变为23:59:59
	 * 
	 * @param date 要清除的日期
	 * @return {@link Date}
	 */
	public static Date clearToEnd(Date date) {
		try {
			if (date == null) return null;
			return parse(format(date, DEFAULT_DATE_FORMAT) + " 23:59:59", DEFAULT_DATETIME_FORMAT);
		} catch (ParseException ingore) {
			return null;
		}
	}

	/**
	 * 一天的毫秒数
	 */
	public static final int DAY = 24 * 60 * 60 * 1000;

	/**
	 * 判断cls是否是一个日期类型
	 * 
	 * @param cls 对象class
	 * @return boolean
	 */
	public static boolean isDate(Class<?> cls) {
		return cls != null && Date.class.isAssignableFrom(cls);
	}

	/**
	 * 判断date是否为日期对象
	 * 
	 * @param date 日期
	 * @return boolean
	 */
	public static boolean isDate(Object date) {
		try {
			return date instanceof Date;
		} catch (Exception e) {
			return isDate(StringHelper.trim(date));
		}
	}

	/**
	 * 判断指定的日期date与日期格式dateformat是否匹配
	 * 
	 * @param date    日期
	 * @param pattern 格式字符串
	 * @see #parse(String, String)
	 * @return ture 如果date能解析为pattern的日期格式
	 */
	public static boolean isDate(String date, String pattern) {
		try {
			return null != parse(date, pattern);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * date是否能解析为日期
	 * 
	 * @param date 日期
	 * @see #parse(String)
	 * @return true 如果date为已知的日期格式
	 */
	public static boolean isDate(String date) {
		return null != parse(date);
	}

	/**
	 * year, month, day是否日期
	 * 
	 * @param year  年
	 * @param month 月
	 * @param day 日
	 * @return boolean
	 */
	public static boolean isDate(int year, int month, int day) {
		if ((month < 0 || month > 12) || (day < 0 || day > 31)) {
			return false;
		}

		return day < getDays(year, month);
	}

	/**
	 * 是否闰年
	 * 
	 * @param year 年
	 * @return bollean
	 */
	public static boolean isLeap(int year) {
		return (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0));
	}

	/**
	 * 返回一个月的天数
	 * 
	 * @param year 年
	 * @param month 月
	 * @return Int
	 */
	public static int getDays(int year, int month) {
		return (month == 2) ? (isLeap(year) ? 29 : 28)
				: ((((month < 7) && (month % 2 == 0)) || ((month > 8) && (month % 2 == 1))) ? 30 : 31);
	}

	/**
	 * 取较小的日期
	 * 
	 * @param date 日期
	 * @param another 日期
	 * @return 小的日期
	 */
	public static Date min(Date date, Date another) {
		return date.before(another) ? date : another;
	}

	/**
	 * 取较大的日期
	 * 
	 * @param date    日期
	 * @param another 日期
	 * @return 大的日期
	 */
	public static Date max(Date date, Date another) {
		return another.after(date) ? another : date;
	}

}