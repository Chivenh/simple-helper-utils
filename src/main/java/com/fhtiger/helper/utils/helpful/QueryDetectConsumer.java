package com.fhtiger.helper.utils.helpful;

import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * QueryDetectConsumer
 *
 * @author Chivenh
 * @apiNote When convert the value to null,The queryDetect will call voidAppend to only append the conditionStr and without param.
 * @created 2021年06月29日 10:22
 */
@SuppressWarnings({ "unused" })

public final class QueryDetectConsumer<T> {

	private final QueryDetect conditionalDetect;

	private final T value;

	private final boolean pass;

	private boolean dead;

	QueryDetectConsumer(QueryDetect conditionalDetect, T value, boolean pass) {
		this.conditionalDetect = conditionalDetect;
		this.value = value;
		this.pass = pass;
	}

	private void dead() {
		this.dead = true;
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 * @return {@link QueryDetectConsumer}
	 */
	QueryDetectConsumer<T> done(Supplier<String> conditionalStr, Supplier<T> reValue) {
		if (this.dead) {
			return this;
		}
		if (this.pass) {
			this.conditionalDetect.append(reValue.get(), conditionalStr.get());
			this.dead();
		}
		return this;
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 * @return {@link QueryDetectConsumer}
	 */
	QueryDetectConsumer<T> doneMulti(Supplier<String> conditionalStr, Supplier<List<T>> reValue) {
		if (this.dead) {
			return this;
		}
		if (this.pass) {
			this.conditionalDetect.append(reValue.get(), conditionalStr.get());
			this.dead();
		}
		return this;
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> done(String conditionalStr, T reValue) {
		if (this.dead) {
			return this;
		}

		if (this.pass) {
			this.conditionalDetect.append(reValue, conditionalStr);
			this.dead();
		}
		return this;
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 */
	public QueryDetectConsumer<T> done(String conditionalStr) {

		return this.done(conditionalStr, this.value);
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> done(String conditionalStr, List<T> reValue) {
		if (this.dead) {
			return this;
		}

		if (this.pass) {
			this.conditionalDetect.append(reValue, conditionalStr);
			this.dead();
		}
		return this;
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param valueConverter 值转换器
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> done(String conditionalStr, Converter<T, T> valueConverter) {

		return this.done(() -> conditionalStr, () -> valueConverter.convert(this.value));
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param valueConverter 值转换器
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> doneMulti(String conditionalStr, Converter<T, List<T>> valueConverter) {

		return this.doneMulti(() -> conditionalStr, () -> valueConverter.convert(this.value));
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 */
	public QueryDetectConsumer<T> done(Function<T, String> conditionalStr) {
		return this.done(() -> conditionalStr.apply(this.value), () -> this.value);
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> done(Function<T, String> conditionalStr, T reValue) {
		return this.done(() -> conditionalStr.apply(this.value), () -> reValue);
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> done(Function<T, String> conditionalStr, List<T> reValue) {
		return this.doneMulti(() -> conditionalStr.apply(this.value), () -> reValue);
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param valueConverter 值转换器
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> done(Function<T, String> conditionalStr, Converter<T, T> valueConverter) {

		return this.done(() -> conditionalStr.apply(this.value), () -> valueConverter.convert(this.value));
	}

	/**
	 * 校验通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param valueConverter 值转换器
	 * @return {@link QueryDetectConsumer}
	 */
	public QueryDetectConsumer<T> doneMulti(Function<T, String> conditionalStr, Converter<T, List<T>> valueConverter) {

		return this.doneMulti(() -> conditionalStr.apply(this.value), () -> valueConverter.convert(this.value));
	}

	/**
	 * 校验未通过，消费
	 *
	 * @param conditionalStr 条件语句
	 */
	public QueryDetectConsumer<T> skip(String conditionalStr) {

		return this.skip(conditionalStr, this.value);
	}

	/**
	 * 校验未通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param reValue        重赋值
	 */
	public QueryDetectConsumer<T> skip(String conditionalStr, T reValue) {

		if (this.dead) {
			return this;
		}

		if (!this.pass) {
			this.conditionalDetect.append(reValue, conditionalStr);
			this.dead();
		}
		return this;
	}

	/**
	 * 校验未通过，消费
	 *
	 * @param conditionalStr 条件语句
	 * @param valueConverter 值转换器
	 */
	public QueryDetectConsumer<T> skip(String conditionalStr, Converter<T, T> valueConverter) {

		return this.skip(conditionalStr, valueConverter.convert(this.value));
	}

	/**
	 * 校验未通过，消费(无参数conditional)
	 *
	 * @param conditionalStr 条件语句
	 */
	public QueryDetectConsumer<T> voidSkip(String conditionalStr) {

		if (this.dead) {
			return this;
		}

		if (!this.pass) {
			this.conditionalDetect.voidAppend(conditionalStr);
			this.dead();
		}
		return this;
	}

	/**
	 * 继续下一个条件语句组装
	 *
	 * @param value 参数值
	 * @param test  参数校验器
	 * @param <R>   -
	 * @return 查询条件组装消费
	 */
	public <R> QueryDetectConsumer<R> then(R value, Predicate<R> test) {
		return this.conditionalDetect.detect(value, test);
	}

	public QueryDetect it() {
		return this.conditionalDetect;
	}
}
