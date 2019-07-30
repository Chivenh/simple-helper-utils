package com.fhtiger.utils.helperutils.idgenerator;

import com.fhtiger.utils.helperutils.helper.DateHelper;
import com.fhtiger.utils.helperutils.helper.FileHelper;
import com.fhtiger.utils.helperutils.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.Map;

/**
 * 用于生成Java类.
 * 为fromPackage包下的所有class生成到toPackage包下对应的类.
 * 
 * @author LuoGang
 *
 */
public abstract class JavyClassGenerator {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public JavyClassGenerator() {
		super();
	}

	/**
	 * @param src 源文件目录
	 * @param fromPackage 要处理的包
	 * @param toPackage 生成的新的包
	 */
	public JavyClassGenerator(String src, String fromPackage, String toPackage) {
		super();
		this.src = src;
		this.toPackage = toPackage;
		this.fromPackage = fromPackage;
	}

	/** 源文件目录,此参数必须 */
	protected String src;

	/** 要生成对应类的来源包 */
	protected String fromPackage;

	/** 生成的java文件的包 */
	protected String toPackage;

	/** 生成的Java类前缀 */
	protected String prefix;
	/** 生成的Java类后缀 */
	protected String suffix;

	/**
	 * 生成单个文件
	 * 
	 * @param file 要生成的JAVA文件
	 * @param fromClass 来源的class
	 * @param context 已有的属性,用于FreeMarker生成模板文件使用的属性
	 */
	protected abstract void generate(File file, Class<?> fromClass, Map<String, Object> context);

	/**
	 * 得到fromClass要生成的简单类名，不含package
	 * 
	 * @param fromClass 类
	 * @return String
	 */
	protected String toClassSimpleName(Class<?> fromClass) {
		String name = fromClass.getSimpleName();
		if (this.prefix != null) name = this.prefix + name;
		if (this.suffix != null) name = name + this.suffix;
		return name;
	}

	/**
	 * 列出所有要处理的源文件名
	 * 
	 * @return Stirng[]
	 */
	protected String[] listSrc() {
		File from = new File(src, fromPackage.replace(".", File.separator));
		return from.list((dir, name) -> {
			if (!name.endsWith(".java")) return false;
			if (name.endsWith("Id.java")) return false; // 复合主键
			return !name.equals("package-info.java");
		});
	}

	/** 生成JAVA文件 */
	public void generate() {

		String[] fileNames = this.listSrc();

		File parent = new File(src, toPackage.replace(".", File.separator));
		if (!parent.exists()) parent.mkdirs(); // 新的包，创建目录

		Map<String, Object> context = Helper.toMap(this, "class");
		context.put("packageName", toPackage);
		context.put("prefix", prefix);
		context.put("suffix", suffix);
		context.put("now", DateHelper.format(new Date(), DateHelper.DEFAULT_DATETIME_FORMAT));

		String fromClassSimpleName;
		String fromClassName;
		Class<?> fromClass;
		String classSimpleName;
		String className;
		// File classFile;
		for (String fileName : fileNames) {
			fromClassSimpleName = FileHelper.getSimpleFileName(fileName);
			fromClassName = fromPackage + "." + fromClassSimpleName;
			try {
				fromClass = Thread.currentThread().getContextClassLoader().loadClass(fromClassName);
			} catch (Exception e) {
				 logger.warn(fileName + "(" + fromClassSimpleName + ")处理失败!", e);
				continue;
			}
			if (!this.include(fromClass)) continue;
			context.put("fromClassSimpleName", fromClassSimpleName);
			context.put("fromClassName", fromClassName);
			classSimpleName = toClassSimpleName(fromClass);
			className = toPackage + "." + classSimpleName;
			context.put("classSimpleName", classSimpleName);
			context.put("className", className);

			generate(new File(parent, classSimpleName + ".java"), fromClass, context);
		}
	}

	/**
	 * fromClass是否需要处理，默认有StaticMetamodel注解的均不处理
	 * 
	 * @param fromClass 类
	 * @return false不需要处理fromClass
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean include(Class<?> fromClass) {
		Class cls;
		try {
			cls = Class.forName("javax.persistence.metamodel.StaticMetamodel");
		} catch (ClassNotFoundException e) {
			// 没有使用JPA StaticMetamodel
			return true;
		}

		// 有了JPA StaticMetamodel的注释均不需要
		return null == fromClass.getAnnotation(cls);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getFromPackage() {
		return fromPackage;
	}

	public void setFromPackage(String fromPackage) {
		this.fromPackage = fromPackage;
	}

	public String getToPackage() {
		return toPackage;
	}

	public void setToPackage(String toPackage) {
		this.toPackage = toPackage;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}
