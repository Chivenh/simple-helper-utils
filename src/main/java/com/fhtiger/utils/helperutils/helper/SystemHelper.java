package com.fhtiger.utils.helperutils.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 系统设置
 * 
 * @author LFH
 * @since 2019/7/30 17:22
 * @version 0.0.1
 */
public class SystemHelper {

	private static Logger logger = LoggerFactory.getLogger(SystemHelper.class);

	/** 加载过的所有配置 */
	private static final Map<String, Properties> CACHES = new HashMap<String, Properties>();

	/** 默认的配置文件,默认为system.cfg.properties */
	private static final String DEFAULT_RESOURCE = System.getProperty("system.defaultResource", "system.cfg.properties");

	/** 读取的所有配置 */
	private static final Properties props = getProperties(DEFAULT_RESOURCE);

	/** 系统代码 */
	public static final String SYSTEM_CODE = getProperty("system.code", "cargo");

	/** 是否为调试模式 */
	public static final boolean DEBUG = StringHelper.parseBoolean(getProperty("system.debug"));

	/** ascii编码 */
	public static final String ENCODING_ASCII = StandardCharsets.US_ASCII.name();// "ascii";

	/** iso-8859-1编码 */
	public static final String ENCODING_ISO_8859_1 = StandardCharsets.ISO_8859_1.name(); // "iso-8859-1";

	/** utf-8编码 */
	public static final String ENCODING_UTF_8 = StandardCharsets.UTF_8.name(); // "utf-8";

	/** gbk编码 */
	public static final String ENCODING_GBK = "GBK";

	/** gb2312编码 */
	public static final String ENCODING_GB2312 = "GB2312";

	/** big5编码 */
	public static final String ENCODING_BIG5 = "BIG5";

	/** 系统使用的默认编码.未指定则使用utf-8.可通过配置文件system.encoding指定 */
	public static final String ENCODING = getProperty("system.encoding", ENCODING_UTF_8);

	/** 1.有效数据 */
	public static final String STATUS_VALID = getProperty("status.valid", "1");
	/** 0.无效数据 */
	public static final String STATUS_INVALID = getProperty("status.invalid", "0");

	/** 系统的换行符 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	/** windows下的换行符，r\n,与LINE_SEPARATOR可能不一致 */
	public static final String ENTER = "\r\n";

	/** 配置数组属性的分隔符.默认为逗号 */
	public static final String ARRAY_SEPARATOR = ",";
	/**
	 * 数组分隔符的正则表达式：\s*${separator}\s*
	 * 
	 * @see String#split(String)
	 */
	public static final String ARRAY_SEPARATOR_REGEX = "\\s*(" + ARRAY_SEPARATOR + ")\\s*";

	/**
	 * 读取文件,转换为InputStream
	 * 
	 * @param resource 资源文件路径
	 * @return InputStream
	 */
	public static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = SystemHelper.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = SystemHelper.class.getClassLoader().getResourceAsStream(stripped);
		}
		return stream;
	}

	/**
	 * 读取配置文件
	 * 
	 * @param resource 资源文件名
	 * @return 配置文件的键值,此方法不会返回null,如果文件不存在或者为空,则返回一个空的Properties对象
	 */
	public static Properties getProperties(String resource) {
		Properties props = CACHES.get(resource);
		if (props != null && !props.isEmpty()) return props;

		if (props == null) {
			props = new Properties();
			CACHES.put(resource, props);
		}
		try (InputStream in = getResourceAsStream(resource);) {
			if (in == null) return props;
			props.load(in);
		} catch (Exception e) {
			logger.error("error:{0}",e);
		}

		String include = props.getProperty("include");
		if (include == null || include.length() <= 0) return props;

		// 读取include的文件
		String[] includes = include.trim().split(ARRAY_SEPARATOR_REGEX);
		for (String res : includes) {
			if (res == null || res.length() <= 0) continue;
			props.putAll(getProperties(res));
		}
		return props;
	}

	/**
	 * 加载配置文件
	 * 
	 * @param resource 配置资源路径
	 * @return {@link Properties}
	 */
	public static Properties load(String resource) {
		Properties props = getProperties(resource);
		if (!props.isEmpty()) {
			// 将读取到的配置增加到当前默认的配置中去
			SystemHelper.props.putAll(props);
		}
		return props;
	}

	/**
	 * 取得配置属性key的值
	 * 
	 * @param key 配置Key
	 * @return String 配置属性值
	 */
	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * 取得配置属性key的值,如果没有则返回默认值defaultValue
	 * 
	 * @param key 配置Key
	 * @param defaultValue 如果key未找到则返回defaultValue
	 * @return String 配置属性值
	 */
	public static String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	/**
	 * 得到key配置的数组
	 * 
	 * @param key 配置Key
	 * @param defaultArray 如果key对应的配置为空，则返回defaultArray
	 * @return String[] 配置属性值
	 */
	public static String[] getArrayProperty(String key, String[] defaultArray) {
		String prop = getProperty(key);
		if (StringHelper.isEmpty(prop)) return defaultArray;
		return prop.split(ARRAY_SEPARATOR_REGEX);
	}

	/**
	 * 
	 * 得到key配置的数组
	 * 
	 * @param key 配置Key
	 * @return String[] 配置属性值
	 */
	public static String[] getArrayProperty(String key) {
		return getArrayProperty(key, (String[]) null);
	}

	/**
	 * 得到key配置的数组
	 * 
	 * @param key 配置Key
	 * @param defaultValue 如果key对应的配置为空，则返回defaultValue.split
	 * @return String[] 配置属性值
	 */
	public static String[] getArrayProperty(String key, String defaultValue) {
		String prop = getProperty(key, defaultValue);
		if (StringHelper.isEmpty(prop)) return null;
		return prop.split(ARRAY_SEPARATOR_REGEX);
	}

	/**
	 * 得到boolean类型的参数
	 * 
	 * @param key 配置Key
	 * @return Boolean 配置属性值
	 */
	public static Boolean getBoolean(String key) {
		String prop = props.getProperty(key);
		if (StringHelper.isEmpty(prop)) return null;
		return StringHelper.parseBoolean(prop);
	}

	/**
	 * 得到int类型的参数
	 *
	 * @param key 配置Key
	 * @return Integer 配置属性值
	 */
	public static Integer getInt(String key) {
		String prop = props.getProperty(key);
		if (StringHelper.isEmpty(prop)) return null;
		return NumberHelper.intValue(prop);
	}

	/**
	 * 得到long类型的参数
	 * 
	 * @param key 配置Key
	 * @return Long 配置属性值
	 */
	public static Long getLong(String key) {
		String prop = props.getProperty(key);
		if (StringHelper.isEmpty(prop)) return null;
		return NumberHelper.longValue(prop);
	}

	/**
	 * 得到double类型的参数
	 * 
	 * @param key 配置Key
	 * @return Double 配置属性值
	 */
	public static Double getDouble(String key) {
		String prop = props.getProperty(key);
		if (StringHelper.isEmpty(prop)) return null;
		return NumberHelper.doubleValue(prop);
	}

}
