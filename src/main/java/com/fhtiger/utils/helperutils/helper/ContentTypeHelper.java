package com.fhtiger.utils.helperutils.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 主要文件后缀名和contentType的对应关系处理
 * 
 * @author LFH
 * @since 2019/7/30 12:56
 */
public class ContentTypeHelper {
	/** logger对象 */
	 private static final Logger logger = LoggerFactory.getLogger(ContentTypeHelper.class);
	 /** 配置文件名称 */
	 public static final String DEFAULT_RESOURCE = SystemHelper.getProperty("contentTypes.resource");

	/** 所有文件类型与contentype的对应关系,后缀名应该以.开头,全小写 */
	private static final Properties CONTENT_TYPES = new Properties();

	/** .文件后缀名的点 */
	public static final String POINT = ".";

	static {
		// 加载配置文件类型的映射文件
		Properties props = new Properties();
		try (InputStream in = ContentTypeHelper.class.getResourceAsStream("contentTypes.properties")) {
			if (in != null) props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String DEFAULT_RESOURCE = SystemHelper.getProperty("contentTypes.resource");
		if (StringHelper.isNotEmpty(DEFAULT_RESOURCE)) {
//			 logger.info("加载contentTypes配置文件:{}", DEFAULT_RESOURCE);
			props.putAll(SystemHelper.getProperties(DEFAULT_RESOURCE));
		}
		// 将key全部转为小写
		String lowerCaseKey;
		for (Object key : props.keySet().toArray()) {
			lowerCaseKey = StringHelper.trim(key).toLowerCase();
			if (!lowerCaseKey.startsWith(POINT)) {
				// 文件后缀名应该以点号.开头
				lowerCaseKey = POINT + lowerCaseKey;
			} else if (lowerCaseKey.equals(key)) continue;
			CONTENT_TYPES.put(lowerCaseKey, props.get(key));
		}
	}

	/** XML类型 */
	public static final String XML = "text/xml";
	/** HTML文档类型 */
	public static final String HTML = "text/html";
	/** 纯文本 */
	public static final String PLAINTEXT = "text/plaintext";
	/** JSON类型 */
	public static final String JSON = "application/json";
	/** JS类型 */
	public static final String JS = "application/javascript";

	/** 没有指定content-type时默认的content-type，默认为application/octet-stream */
	public static final String DEFAULT_CONTENT_TYPE = StringHelper.trim(ContentTypeHelper.get(".*"),
			"application/octet-stream");

	/**
	 * 获取fileSuffix对应的类型
	 * 
	 * @param fileSuffix 文件后缀名,已点.开头
	 * @return contentType
	 */
	public static String get(String fileSuffix) {
		if (fileSuffix == null) return null;
		String lowerCaseKey = StringHelper.trim(fileSuffix).toLowerCase();
		if (!lowerCaseKey.startsWith(POINT)) {
			// 文件后缀名应该以点号.开头
			lowerCaseKey = POINT + lowerCaseKey;
		}
		return StringHelper.trim(CONTENT_TYPES.getProperty(lowerCaseKey), DEFAULT_CONTENT_TYPE);
	}

	/**
	 * 得到文件的contentType
	 * 
	 * @param file 文件
	 * @return contentType
	 */
	public static String get(File file) {
		return get(FileHelper.getFileSuffix(file));
	}
}
