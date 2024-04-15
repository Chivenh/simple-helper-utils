package com.fhtiger.helper.utils.web;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * 输出名生成器
 *
 * @author Chivenh
 * @since 2020年04月17日 19:22
 */
@SuppressWarnings({"unused"})
public enum OutputNameGenerator {

	/**
	 * 浏览器环境下自动根据不同浏览器进行命名转换
	 */
	BROWSER((arg)->{

		assert arg instanceof HttpServletRequest : "浏览器命名生成器需要(HttpServletRequest)参数";

		HttpServletRequest request = (HttpServletRequest)arg;

		final String userAgent = request.getHeader("USER-AGENT");

		return (name)->{
			String finalFileName = name;
			final String browserTagiE="MSIE",browserTagMoz="Mozilla";

			try {
				if(userAgent.contains(browserTagiE)){
					/*IE浏览器*/
					finalFileName = URLEncoder.encode(name,StandardCharsets.UTF_8.displayName());
				}else if(userAgent.contains(browserTagMoz)){
					/*google,火狐浏览器*/
					finalFileName = new String(name.getBytes("GBK"), StandardCharsets.ISO_8859_1);
				}else{
					finalFileName = URLEncoder.encode(name,StandardCharsets.UTF_8.displayName());//其他浏览器
				}
			} catch (UnsupportedEncodingException e) {
				/**/
			}
			return finalFileName;
		};
	}),
	/**
	 * 自然模式，只trim
	 */
	NATIVE((arg)-> String::trim);

	public static final Function<String,String> NATIVE_NAME_GENERATOR=NATIVE.getGenerator(null);

	private Function<Object,Function<String,String>>  generator;

	OutputNameGenerator(Function<Object, Function<String, String>> generator) {
		this.generator = generator;
	}

	public Function<String, String> getGenerator(Object arg){
		return this.generator.apply(arg);
	}
}
