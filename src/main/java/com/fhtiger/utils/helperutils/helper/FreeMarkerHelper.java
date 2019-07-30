package com.fhtiger.utils.helperutils.helper;

import com.fhtiger.utils.helperutils.util.HelperException;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 使用FreeMarkert进行模板文件的转换
 * 
 * @author LFH
 * @since 2019/7/30 14:53
 * @version 0.0.1
 */
public class FreeMarkerHelper {

	private static Logger logger = LoggerFactory.getLogger(FreeMarkerHelper.class);

	/** 模板文件后缀 */
	public static final String TEMPLATE_FILE_SUFFIX = ".ftl";

	/** FreeMarker的配置对象，默认为class根目录下的ftl目录 */
	private static Configuration configuration;

	/**
	 * 得到当前的FTL模板配置信息
	 * 
	 * @return {@link Configuration}
	 */
	public static Configuration getDefaultConfiguration() {
		return configuration;
	}

	/**
	 * 更改配置模板
	 * 
	 * @param configuration {@link Configuration}
	 */
	public static void setDefaultConfiguration(Configuration configuration) {
		FreeMarkerHelper.configuration = configuration;
	}

	/**
	 * 更改FreeMarker的模板目录
	 * 
	 * @param dir ftl模板存放目录
	 */
	public static void setDefaultConfiguration(File dir) {
		configuration = configure(dir);
	}

	/**
	 * 设定配置文件目录
	 * 
	 * @param dir 配置文件目录
	 * @return {@link Configuration}
	 */
	public static Configuration configure(File dir) {
		Version ver = Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;
		Configuration configuration = new Configuration(ver);
		try {
			//配置模板加载的目录
			configuration.setDirectoryForTemplateLoading(dir);
			configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
			configuration.setTemplateUpdateDelayMilliseconds(0);
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		} catch (IOException e) {
			throw new HelperException(e);
		}
		configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(ver).build());

		return configuration;
	}

	/**
	 * 处理模板数据
	 * 
	 * @param name 模板文件名,包括全路径和后缀名
	 * @param rootMap 数据
	 * @param file    文件
	 */
	public static void process(String name, Object rootMap, File file) {
		try (Writer out = new OutputStreamWriter(new FileOutputStream(file), SystemHelper.ENCODING)) {
			Template template = configuration.getTemplate(name, SystemHelper.ENCODING);
			template.process(rootMap, out);
		} catch (Exception e) {
			throw new HelperException(e);
		}
	}

	/**
	 * 处理模板数据
	 * 
	 * @param name 模板文件名,包括全路径和后缀名
	 * @param rootMap 数据
	 * @param out 输出流
	 */
	public static void process(String name, Object rootMap, Writer out) {
		try {
			Template template = configuration.getTemplate(name, SystemHelper.ENCODING);
			template.process(rootMap, out);
		} catch (Exception e) {
			throw new HelperException(e);
		}
	}

	/**
	 * 得到模板数据
	 * 
	 * @param name 模板文件名
	 * @param rootMap 数据
	 * @return 转换后的文件内容
	 */
	public static String get(String name, Object rootMap) {
		StringWriter out = new StringWriter();
		try {
			Template template = configuration.getTemplate(name, SystemHelper.ENCODING);
			template.process(rootMap, out);
		} catch (FileNotFoundException e) {
			logger.warn("{},文件不存在!",name);
			return null;
		} catch (Exception e) {
			throw new HelperException(e);
		}
		return out.toString();
	}
}
