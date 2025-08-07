package com.fhtiger.helper.utils.web;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
	BROWSER(OutputNameGenerator::browserFileNameExtracted),
	/**
	 * 自然模式，只trim
	 */
	NATIVE((arg)-> String::trim);

	private static UnaryOperator<String> browserFileNameExtracted(Object arg) {
		assert arg instanceof HttpServletRequest : "浏览器命名生成器需要(HttpServletRequest)参数";

		HttpServletRequest request = (HttpServletRequest) arg;

		final String userAgent = request.getHeader("USER-AGENT");

		return (name) -> {
			String finalFileName = name;
			final String browserTagiE = "MSIE", browserTagMoz = "Mozilla";

			try {
				if (userAgent.contains(browserTagiE)) {
					/*IE浏览器*/
					finalFileName = URLEncoder.encode(name, StandardCharsets.UTF_8.displayName());
				} else if (userAgent.contains(browserTagMoz)) {
					/*google,火狐浏览器*/
					finalFileName = new String(name.getBytes("GBK"), StandardCharsets.ISO_8859_1);
				} else {
					finalFileName = URLEncoder.encode(name, StandardCharsets.UTF_8.displayName());//其他浏览器
				}
			} catch (UnsupportedEncodingException e) {
				/**/
			}
			return finalFileName;
		};
	}

	public static final UnaryOperator<String> NATIVE_NAME_GENERATOR=NATIVE.getGenerator(null);

	private final Function<Object,UnaryOperator<String>>  generator;

	OutputNameGenerator(Function<Object, UnaryOperator<String>> generator) {
		this.generator = generator;
	}

	public UnaryOperator<String> getGenerator(Object arg){
		return this.generator.apply(arg);
	}
}
