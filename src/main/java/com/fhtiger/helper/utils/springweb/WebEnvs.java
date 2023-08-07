package com.fhtiger.helper.utils.springweb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WebEnvs
 * 标记需要加载为web中environment 参数的属性文件
 * @author LFH
 * @since 2020年12月11日 09:03
 */
@SuppressWarnings({ "unused" })
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebEnvs {

	/**
	 * classpath下的属性文件名，名称中添加classpath:前缀；
	 * 目前支持：yaml,yml,props,prop,properties 几类文件;
	 * 默认会加载 env.extension.properties文件
	 * @return -
	 */
	String [] value() default {"env.extension.properties"};
}
