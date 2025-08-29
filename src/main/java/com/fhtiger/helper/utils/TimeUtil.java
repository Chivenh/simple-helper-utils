package com.fhtiger.helper.utils;


import com.fhtiger.helper.utils.helpful.AssertDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * DUtil
 * 日期相关工具
 *
 * @author Chivenh
 * @since 2020年04月26日 15:24
 */
@SuppressWarnings({ "unused" })

public final class TimeUtil {

	private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);

	private TimeUtil() throws IllegalAccessException {
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 线程级缓存引擎
	 */
	private static final InheritableThreadLocal<Engine> ENGINE_LOCAL = new InheritableThreadLocal<>() {
		@Override
		protected Engine initialValue() {
			return Engine.ES;
		}
	};

	/**
	 * 临时指定引擎进行操作并返回指定类型值
	 * e.g. <br>DUtil.use(DUtil.Engine.ED, ()-&gt;{
	 * System.out.println(DUtil.getNow(type));
	 * System.out.println(DUtil.getNowDate());
	 * System.out.println(DUtil.getNowDate(type));
	 * <p>
	 * return null;
	 * });
	 *
	 * @param engine 引擎{@link Engine}
	 * @param doing  操作
	 * @param <R>    返回值
	 * @return R
	 */
	public static <R> R use(Engine engine, Supplier<R> doing) {
		try {
			ENGINE_LOCAL.set(engine);
			return doing.get();
		}
		finally {
			ENGINE_LOCAL.remove();
		}
	}

	/* 日期对象分段*/

	/**
	 * 对一个时间区间进行分段
	 *
	 * @param start    起始时间
	 * @param separate 分段大小.
	 * @param unit     分段单位
	 * @param end      结束时间
	 * @return 时间分段列表
	 */
	static List<Date[]> segmentsDateRange(Date start, int separate, final ChronoUnit unit, Date end) {
		AssertDetect.notNull(new Object[] { start, end }, "开始时间不能为空!", "结束时间不能为空!");
		AssertDetect.test(separate != 0, "分段大小不能为0");
		final List<Date[]> segments = new ArrayList<>();
		LocalDateTime startTime = toLocalDateTime(start);
		LocalDateTime endTime = toLocalDateTime(end);
		LocalDateTime it;
		Date itd;
		/*时间区间方向校正*/
		if (startTime.isAfter(endTime)) {
			it = startTime;
			itd = start;
			startTime = endTime;
			start = end;
			endTime = it;
			end = itd;
		}
		/*取绝对值*/
		separate = Math.abs(separate);
		Date[] segment;
		/*暂存段起始位*/
		itd = start;

		/*分段*/
		while ((it = startTime.plus(separate, unit)).isBefore(endTime)) {
			segment = new Date[2];
			/*起始位为前一段结束位*/
			segment[0] = itd;
			/*暂存段结束位*/
			segment[1] = itd = toDate(startTime = it);
			segments.add(segment);
		}
		int size = segments.size();
		if (size > 0) {
			/*最后一个段的截止时间直接置换为原始end*/
			segments.get(size - 1)[1] = end;
		} else {
			segments.add(new Date[] { start, end });
		}

		return segments;
	}

	/**
	 * 对一个时间区间进行分段,默认结束时间为当前时间
	 *
	 * @param start    起始时间
	 * @param separate 分段大小.
	 * @param unit     分段单位
	 * @return 时间分段列表
	 */
	static List<Date[]> segmentsDateRange(Date start, int separate, ChronoUnit unit) {
		Date end = new Date();
		return segmentsDateRange(start, separate, unit, end);
	}

	/**
	 * 对一个时间区间进行分段,默认结束时间为当前时间
	 *
	 * @param start 起始时间
	 * @return 时间分段列表
	 */
	static List<Date[]> segmentsDateRange(Date start) {
		Date end = new Date();
		return segmentsDateRange(start, 1, ChronoUnit.MONTHS, end);
	}

	/**
	 * 对一个时间区间进行分段,默认结束时间为当前时间
	 *
	 * @param start 起始时间
	 * @param end   截止时间
	 * @return 时间分段列表
	 */
	static List<Date[]> segmentsDateRange(Date start, Date end) {
		return segmentsDateRange(start, 1, ChronoUnit.MONTHS, end);
	}

	/**
	 * 对一个时间区间进行分段,默认结束时间为当前时间
	 *
	 * @param start    起始时间
	 * @param separate 分段大小.
	 * @param unit     分段单位
	 * @param end      结束时间
	 * @return 时间分段列表
	 */
	public static List<Date[]> dateRange2Segments(Date start, int separate, ChronoUnit unit, Date end) {
		return segmentsDateRange(start, separate, unit, end);
	}

	/**
	 * 对一个时间区间进行分段,默认结束时间为当前时间
	 *
	 * @param start    起始时间
	 * @param separate 分段大小.
	 * @param unit     分段单位
	 * @return 时间分段列表
	 */
	public static List<Date[]> dateRange2Segments(Date start, int separate, ChronoUnit unit) {
		return segmentsDateRange(start, separate, unit);
	}

	/**
	 * 对一个时间区间进行分段,默认结束时间为当前时间，默认分段大小为1月
	 *
	 * @param start 起始时间
	 * @return 时间分段列表
	 */
	public static List<Date[]> dateRange2Segments(Date start) {
		return segmentsDateRange(start);
	}

	/**
	 * 对一个时间区间进行分段,默认分段大小为1月
	 *
	 * @param start 起始时间
	 * @param end   截止时间
	 * @return 时间分段列表
	 */
	public static List<Date[]> dateRange2Segments(Date start, Date end) {
		return segmentsDateRange(start, end);
	}

	/* 日期对象类型转换 */

	/**
	 * Date to LocalDateTime
	 *
	 * @param date Date
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		Class<? extends Date> valueClass = date.getClass();
		if (java.sql.Date.class.isAssignableFrom(valueClass)) {
			return LocalDateTime.of(((java.sql.Date) date).toLocalDate(), LocalTime.MIN);
		} else if (Time.class.isAssignableFrom(valueClass)) {
			return LocalDateTime.of(LocalDate.of(0, 1, 1), ((Time) date).toLocalTime());
		}
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	/**
	 * Date to LocalDate
	 *
	 * @param date Date
	 * @return {@link LocalDate}
	 */
	public static LocalDate toLocalDate(Date date) {
		Class<? extends Date> valueClass = date.getClass();
		if (java.sql.Date.class.isAssignableFrom(valueClass)) {
			return ((java.sql.Date) date).toLocalDate();
		} else if (Time.class.isAssignableFrom(valueClass)) {
			return LocalDate.of(0, 1, 1);
		}
		return LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault()));
	}

	/**
	 * LocalDateTime to Date
	 *
	 * @param localDateTime LocalDateTime
	 * @return {@link Date}
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * LocalDate to Date
	 *
	 * @param localDate LocalDate
	 * @return {@link Date}
	 */
	public static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 当前日期
	 *
	 * @return {@link LocalDate}
	 */
	public static LocalDate nowLocalDate() {
		return LocalDate.now(ZoneId.systemDefault());
	}

	/**
	 * 当前日期时间
	 *
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime nowLocalDateTime() {
		return LocalDateTime.now(ZoneId.systemDefault());
	}

	/**
	 * 今天最后时间（明日起始时间）
	 *
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime nowLocalDateEndTime() {
		return LocalDate.now(ZoneId.systemDefault()).plusDays(1).atStartOfDay();
	}

	/**
	 * 今天最大时间
	 *
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime nowLocalDateLastTime() {
		return LocalDate.now(ZoneId.systemDefault()).atTime(LocalTime.MAX);
	}

	/**
	 * 当前日期
	 *
	 * @return {@link Date}
	 */
	public static Date nowDate() {
		return toDate(nowLocalDate());
	}

	/**
	 * 当前日期时间
	 *
	 * @return {@link Date}
	 */
	public static Date nowDateTime() {
		return toDate(nowLocalDateTime());
	}

	/**
	 * 今天最后时间（明日起始时间）
	 *
	 * @return {@link Date}
	 */
	public static Date nowDateEndTime() {
		return toDate(nowLocalDateEndTime());
	}

	/**
	 * 今天最大时间
	 *
	 * @return {@link Date}
	 */
	public static Date nowDateLastTime() {
		return toDate(nowLocalDateLastTime());
	}

	private static Date parseDate(Object formatter, String dateStr) {
		return ENGINE_LOCAL.get().parseDate(formatter, dateStr);
	}

	private static Object createFormatter(String pattern, Locale locale) {
		return ENGINE_LOCAL.get().createFormatter(pattern, locale);
	}

	/**
	 * 获取当前时间[yyyy-MM-dd]
	 * @param type {@link DateType}
	 * @return 日期字符串
	 * @author Chivenh
	 * @since 2017年4月24日 上午10:27:40
	 */
	public static String getNow(DateType type) {
		return strDate(type, getNowDate(type));
	}

	public static Date getNowDate() {
		return getNowDate(null);
	}

	public static Date getNowDate(DateType type) {
		Date now = null;
		try {
			type = type == null ? DateType.A : type;
			Object formatter = createFormatter(type.value, type.locale);
			now = parseDate(formatter, ENGINE_LOCAL.get().formatDate(formatter, new Date()));
		} catch (Exception e) {
			logger.error("TimeUtil error: ",e);
		}
		return now;
	}

	/**
	 * 将字符串以指定格式转化为日期.
	 *
	 * @param sdf  格式化字符串
	 * @param date 日期字符串
	 * @return {@link Date}
	 * @author Chivenh
	 * @since 2017年12月9日 上午8:51:11
	 */
	public static Date getDate(String sdf, String date) {
		return parseDate(createFormatter(sdf, null), date);
	}

	/**
	 * 将字符串以指定格式转化为日期(指定国际化)
	 *
	 * @param sdf  格式化字符串
	 * @param locale 国际化标识
	 * @param date 日期字符串
	 * @return {@link Date}
	 * @author Chivenh
	 * @since 2017年12月9日 上午8:51:11
	 */
	public static Date getDate(String sdf, Locale locale, String date) {
		return parseDate(createFormatter(sdf, locale), date);
	}

	/**
	 * 得到日期字符串
	 *
	 * @param sdf  日期字符串 {@link DateType}
	 * @param date {@link Date}
	 * @return 字符串日期
	 * @author Chivenh
	 * @since 2017年11月2日 上午11:23:31
	 */
	public static String strDate(DateType sdf, Date date) {
		if (date == null) {
			return "";
		}
		return ENGINE_LOCAL.get().formatDate(createFormatter(sdf.value, sdf.locale), date);
	}

	/**
	 * 判断该字符串是否为日期类型
	 *
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean isDateType(String str) {
		boolean b = false;
		String dateType1 = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d*";
		String dateType2 = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
		String dateType3 = "\\d{4}-\\d{2}-\\d{2}";
		if (Pattern.matches(dateType1, str) || Pattern.matches(dateType2, str) || Pattern.matches(dateType3, str)) {
			b = true;
		}
		return b;
	}

	/**
	 * 返回字符串所属日期格式
	 *
	 * @param str 字符串
	 * @return 日期格式化字符串
	 */
	public static String getDateType(String str) {
		String dateType1 = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d*";
		String dateType2 = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
		String dateType3 = "\\d{4}-\\d{2}-\\d{2}";
		if (Pattern.matches(dateType1, str) || Pattern.matches(dateType2, str)) {
			return "yyyy-MM-dd HH:mm:ss";
		}
		if (Pattern.matches(dateType3, str)) {
			return "yyyy-MM-dd";
		}
		return null;
	}

	public static Date getDate(Object o) {
		return getDate(o, null);
	}

	/**
	 * 默认转换一个字符串成yyyy-MM-dd日期格式.
	 *
	 * @param o    参数对象
	 * @param type {}
	 * @return {@link Date}
	 * @author Chivenh
	 * @since 2017年4月25日 下午5:36:05
	 */
	public static Date getDate(Object o, DateType type) {
		if (o == null) {
			return null;
		} else {
			if (!SpecialUtil.isNull(o)) {
				type = type == null ? DateType.A : type;
				return parseDate(createFormatter(type.value, type.locale), o.toString());
			} else {
				return null;
			}
		}
	}

	/**
	 * 尝试对日期字符串的毫秒位进行3位补齐。
	 * 注意日期解析格式在使用引擎{@link Engine#ED}时，建议毫秒位使用[.SSS]来可选匹配.
	 *
	 * @param strDate 日期字符串
	 * @return 修复日期字符串的毫秒位为标准3位。
	 */
	public static String fillDateStr(String strDate) {
		String dotStr = ".";
		int dot = -1;
		if ((dot = strDate.lastIndexOf(dotStr)) > -1) {
			if (dot == 0) {
				strDate = strDate.substring(0, strDate.length() - 1);
			} else {
				String left = strDate.substring(0, dot);
				String right = strDate.substring(dot + 1);
				strDate = left + dotStr + StringValueUtil.fillIn(right, '0', -3);
			}
		}
		return strDate;
	}

	/**
	 * 新建 {@link DateType}
	 *
	 * @param value  格式化字符串
	 * @param locale 方言
	 * @return {@link DateType}
	 */
	public static DateType createDateType(String value, Locale locale) {
		return DateType.g(value, locale);
	}

	/**
	 * 新建 {@link DateType}
	 *
	 * @param value 格式化字符串
	 * @return {@link DateType}
	 */
	public static DateType createDateType(String value) {
		return DateType.g(value);
	}

	/**
	 * 转换日期类型
	 *
	 * @author Chivenh
	 * @since 2018年2月28日
	 */
	public static final class DateType {
		// @formatter:off
		/*** yyyy-MM-dd */
		public static final DateType A = g("yyyy-MM-dd"),

		/*** yyyy-M-d */
		A1 = g("yyyy-M-d"),

		/*** yyyy-MM */
		A2 = g("yyyy-MM"), /*** yyyy-M */
		A3 = g("yyyy-M"),

		/*** yyyy/MM/dd */
		B = g("yyyy/MM/dd"),

		/*** yyyy/M/d */
		B1 = g("yyyy/M/d"),

		/*** yyyy/M/d HH:mm:ss */
		B2 = g("yyyy/MM/dd HH:mm:ss"),

		/*** yyyy/M/d HH:mm:ss */
		B3 = g("yyyy/M/d HH:mm:ss"),

		/*** yyyy-MM-dd HH:mm:ss */
		C = g("yyyy-MM-dd HH:mm:ss"),

		/*** yyyy-M-d HH:mm:ss */
		C1 = g("yyyy-M-d HH:mm:ss"),

		/*** yyyy-MM-dd HH:mm */
		C2 = g("yyyy-MM-dd HH:mm"),

		/*** d-MMM {@link Locale#ENGLISH} */
		D = g("d-MMM", Locale.ENGLISH),

		/*** d-MMM-yyyy {@link Locale#ENGLISH} */
		D1 = g("d-MMM-yyyy", Locale.ENGLISH),

		/*** d/MMM/yyyy {@link Locale#ENGLISH} */
		D2 = g("d/MMM/yyyy", Locale.ENGLISH),

		/*** d/MMM/yyyy HH:mm:ss {@link Locale#ENGLISH} */
		D3 = g("d/MMM/yyyy HH:mm:ss", Locale.ENGLISH),

		/*** MMM.dd,yyyy {@link Locale#ENGLISH} */
		D4 = g("MMM.dd,yyyy", Locale.ENGLISH),

		/*** MMM dd,yyyy {@link Locale#ENGLISH} */
		D5 = g("MMM dd,yyyy", Locale.ENGLISH),

		/*** yyyy年M月d日 */
		E0 = g("yyyy年M月d日"),

		/*** yyyy年MM月dd */
		E = g("yyyy年MM月dd"),

		/*** yyyy年MM月dd HH:mm:ss */
		E1 = g("yyyy年MM月dd HH:mm:ss"),

		/*** yyyy年MM月dd日 */
		E2 = g("yyyy年MM月dd日"),

		/*** yyyy.MM.dd HH.mm */
		F = g("yyyy.MM.dd HH.mm"),

		/*** yyyyMMdd */
		F1 = g("yyyyMMdd"),

		/*** yyyy */
		YE = g("yyyy"), /*** yyyy */
		YE1 = g("yyyyMM");

		// @formatter:on

		/**
		 * Pattern
		 */
		private final String value;

		/**
		 * {@link Locale}
		 */
		private Locale locale;

		static DateType g(String value) {
			return new DateType(value);
		}

		static DateType g(String value, Locale locale) {
			return new DateType(value, locale);
		}

		private DateType(String value) {
			this.value = value;
		}

		private DateType(String value, Locale locale) {
			this.value = value;
			this.locale = locale;
		}

	}

	public enum Engine implements TimeTransferEngine<Object> {
		/**
		 * 基于1.8新增的{@link DateTimeFormatter}
		 */
		ED(new TimeTransferEngine<DateTimeFormatter>() {
			@Override
			public Date parseDate(DateTimeFormatter formatter, String strDate) {
				TemporalAccessor temp = formatter.parse(strDate);

				LocalDate dateArea = LocalDate.of(temp.isSupported(ChronoField.YEAR) ? temp.get(ChronoField.YEAR) : 1970, temp.isSupported(ChronoField.MONTH_OF_YEAR) ? temp.get(ChronoField.MONTH_OF_YEAR) : 1, temp.isSupported(ChronoField.DAY_OF_MONTH) ? temp.get(ChronoField.DAY_OF_MONTH) : 1);

				LocalTime timeArea = LocalTime
						.of(temp.isSupported(ChronoField.HOUR_OF_DAY) ? temp.get(ChronoField.HOUR_OF_DAY) : 0, temp.isSupported(ChronoField.MINUTE_OF_HOUR) ? temp.get(ChronoField.MINUTE_OF_HOUR) : 0, temp.isSupported(ChronoField.SECOND_OF_MINUTE) ? temp.get(ChronoField.SECOND_OF_MINUTE) : 0,
								temp.isSupported(ChronoField.NANO_OF_SECOND) ? temp.get(ChronoField.NANO_OF_SECOND) : 0);

				return toDate(LocalDateTime.of(dateArea, timeArea));
			}

			@Override
			public DateTimeFormatter createFormatter(String pattern, Locale locale) {
				return locale == null ? DateTimeFormatter.ofPattern(pattern) : DateTimeFormatter.ofPattern(pattern, locale);
			}

			@Override
			public String formatDate(DateTimeFormatter formatter, Date date) {
				return formatter.format(toLocalDateTime(date));
			}
		}),
		/**
		 * 基于{@link SimpleDateFormat},此实现有线程安全风险，推荐使用{@link #ED}
		 */
		ES(new TimeTransferEngine<SimpleDateFormat>() {
			@Override
			public Date parseDate(SimpleDateFormat formatter, String strDate) {
				try {
					return formatter.parse(strDate);
				} catch (ParseException e) {
					logger.error("TimeUtil error: ",e);
					return null;
				}
			}

			@Override
			public SimpleDateFormat createFormatter(String pattern, Locale locale) {
				return locale == null ? new SimpleDateFormat(pattern) : new SimpleDateFormat(pattern, locale);
			}

			@Override
			@SuppressWarnings("PMD")
			public String formatDate(SimpleDateFormat formatter, Date date) {
				return formatter.format(date);
			}
		});
		final TimeTransferEngine<Object> engine;

		@SuppressWarnings("unchecked")
		Engine(TimeTransferEngine<?> engine) {
			this.engine = (TimeTransferEngine<Object>) engine;
		}

		@Override
		public Date parseDate(Object formatter, String strDate) {
			return this.engine.parseDate(formatter, strDate);
		}

		@Override
		public Object createFormatter(String pattern, Locale locale) {
			return this.engine.createFormatter(pattern, locale);
		}

		@Override
		public String formatDate(Object formatter, Date date) {
			return this.engine.formatDate(formatter, date);
		}

		/**
		 * 临时指定引擎进行操作并返回指定类型值
		 * e.g. <br>DUtil.use(DUtil.Engine.ED, ()-&gt;{
		 * System.out.println(DUtil.getNow(type));
		 * System.out.println(DUtil.getNowDate());
		 * System.out.println(DUtil.getNowDate(type));
		 * <p>
		 * return null;
		 * });
		 * @param doing 操作
		 * @param <R>   返回值
		 * @return R
		 */
		public <R> R use(Supplier<R> doing) {
			return TimeUtil.use(this, doing);
		}
	}
}

/**
 * 日期处理引擎
 *
 * @param <T> -{@link SimpleDateFormat} or {@link DateTimeFormatter}
 */
interface TimeTransferEngine<T> {
	/**
	 * 将字符串日期转为{@link Date}
	 *
	 * @param formatter 格式化对象
	 * @param strDate   字符串日期
	 * @return Date
	 */
	Date parseDate(T formatter, String strDate);

	/**
	 * 初始化一个日期格式化对象
	 *
	 * @param pattern 格式化字符串
	 * @param locale  {@link Locale}
	 * @return Formatter
	 */
	T createFormatter(String pattern, Locale locale);

	/**
	 * 格式化日期对象
	 *
	 * @param formatter 格式化对象
	 * @param date      日期{@link Date}
	 * @return String
	 */
	String formatDate(T formatter, Date date);
}
