package com.fhtiger.helper.utils.helpful;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * QueryDetect
 * 查询中的各条件检测与注入,非线程安全类
 *
 * @author LFH
 * @created 2021年06月29日 10:07
 */
@SuppressWarnings({ "unused" })

public final class QueryDetect {

	static final String WHERE_PREFIX = "where";
	static final String AND_PREFIX = "and";

	public static QueryDetect builder(StringBuilder conditional, List<Object> params) {
		return new QueryDetect(conditional, params);
	}

	private StringBuilder conditional;

	private List<Object> params;

	private QueryDetect(StringBuilder conditional, List<Object> params) {
		this.conditional = conditional;
		this.params = params;
	}

	private void checkEnd() {
		Assert.notNull(this.conditional, "This `ConditionalDetect` is already ended! ");
	}

	/**
	 * 开始条件语句组装
	 *
	 * @param value 参数值
	 * @param test  参数校验器
	 * @param <T>   -
	 * @return 查询条件组装消费
	 */
	public <T> QueryDetectConsumer<T> detect(T value, Predicate<T> test) {
		this.checkEnd();

		return new QueryDetectConsumer<T>(this, value, test.test(value));
	}

	<T> void append(T value, String conditionalStr) {
		if(value == null){
			voidAppend(conditionalStr);
			return ;
		}
		this.checkEnd();

		this.params.add(value);
		this.conditional.append(conditionalStr);
	}

	<T> void append(List<T> value, String conditionalStr) {
		if(value == null){
			voidAppend(conditionalStr);
			return ;
		}
		this.checkEnd();

		this.params.addAll(value);
		this.conditional.append(conditionalStr);
	}

	void  voidAppend(String conditionalStr){
		this.checkEnd();
		this.conditional.append(conditionalStr);
	}

	/**
	 * 自动处理成 where 开头语句
	 */
	public void endAsWhere() {
		this.end(() -> {
			if (this.conditional.length() > 0) {
				String conditionalStr = this.fix(this.conditional.toString(), AND_PREFIX, WHERE_PREFIX);
				this.conditional.replace(0, this.conditional.length(), conditionalStr);
			}
			return true;
		});
	}

	/**
	 * 自动处理成 and 开头语句
	 */
	public void endAsAnd() {
		this.end(() -> {
			if (this.conditional.length() > 0) {
				String conditionalStr = this.fix(this.conditional.toString(), WHERE_PREFIX, AND_PREFIX);
				this.conditional.replace(0, this.conditional.length(), conditionalStr);
			}
			return true;
		});
	}

	String fix(String conditionalStr, String check, String replace) {
		return QueryDetect.fixPrefix(conditionalStr, check, replace);
	}

	public static String fixPrefix(String conditionalStr, String check, String replace) {
		conditionalStr = StringUtils.trimWhitespace(conditionalStr);
		if(conditionalStr.length()<1){
			return conditionalStr;
		}
		if (!conditionalStr.startsWith(replace) && !conditionalStr.startsWith(replace.toUpperCase())) {
			if (conditionalStr.startsWith(check) || conditionalStr.startsWith(check.toUpperCase())) {
				conditionalStr = replace + " " + conditionalStr.substring(check.length());
			} else {
				conditionalStr = replace + " " + conditionalStr;
			}
		}
		return " " + conditionalStr + " ";
	}

	public static String fixWhereConditional(String conditionalStr) {
		return QueryDetect.fixPrefix(conditionalStr, AND_PREFIX, WHERE_PREFIX);
	}

	public static String fixAndConditional(String conditionalStr) {
		return QueryDetect.fixPrefix(conditionalStr, WHERE_PREFIX, AND_PREFIX);
	}

	public void end() {
		this.conditional = null;
		this.params = null;
	}

	private void end(Supplier<Boolean> doing) {
		this.checkEnd();
		doing.get();
		this.end();
	}
}
