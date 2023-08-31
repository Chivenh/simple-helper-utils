package com.fhtiger.helper.utils.optional;


import com.fhtiger.helper.utils.SpecialUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * OptionalConsumer
 * 常用Optional的消费方法
 *
 * @author Chivenh
 * @since 2020年05月16日 14:53
 */
@SuppressWarnings({ "unused" })

public final class OptionalConsumer {

	private OptionalConsumer() throws IllegalAccessException {
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 自定义验证器消费
	 *
	 * @param value    值
	 * @param test     验证器
	 * @param consumer 消费者
	 * @param <T>      -
	 */
	public static <T> void predicateConsume(T value, Predicate<T> test, Consumer<T> consumer) {
		if (test != null && test.test(value)) {
			consumer.accept(value);
		}
	}

	/**
	 * 非空值消费
	 *
	 * @param value 值
	 * @param <T>   -
	 */
	public static <T> void nonNullConsume(T value, Consumer<T> consumer) {
		Optional.ofNullable(value).ifPresent(consumer);
	}

	/**
	 * 非空字符串值消费
	 *
	 * @param value    值
	 * @param consumer 消费者
	 * @param <T>      -
	 */
	public static <T extends CharSequence> void nonEmptyConsume(T value, Consumer<T> consumer) {
		CharacterParamOptional.valueConsumer(value, consumer);
	}

	public static <E, T extends Collection<E>> void nonEmptyConsume(T value, Consumer<T> consumer) {
		if (value != null && value.size() > 0) {
			consumer.accept(value);
		}
	}

	/**
	 * 非0值消费
	 *
	 * @param value    值
	 * @param consumer 消费者
	 */
	public static void nonZeroConsume(BigDecimal value, Consumer<BigDecimal> consumer) {
		Optional.ofNullable(value).ifPresent(v -> {
			if (BigDecimal.ZERO.compareTo(value) != 0) {
				consumer.accept(v);
			}
		});
	}

	/**
	 * 正数值消费
	 *
	 * @param value    值
	 * @param consumer 消费者
	 */
	public static void gtZeroConsume(BigDecimal value, Consumer<BigDecimal> consumer) {
		Optional.ofNullable(value).ifPresent(v -> {
			if (BigDecimal.ZERO.compareTo(value) < 0) {
				consumer.accept(v);
			}
		});
	}

	/**
	 * 负数值消费
	 *
	 * @param value    值
	 * @param consumer 消费者
	 */
	public static void ltZeroConsume(BigDecimal value, Consumer<BigDecimal> consumer) {
		Optional.ofNullable(value).ifPresent(v -> {
			if (BigDecimal.ZERO.compareTo(value) > 0) {
				consumer.accept(v);
			}
		});
	}

	/**
	 * {@link Boolean#TRUE}.equals(value)真时调用
	 *
	 * @param value    Boolean
	 * @param consumer 消费者
	 */
	public static void trueConsume(Boolean value, Consumer<Boolean> consumer) {
		if (Boolean.TRUE.equals(value)) {
			consumer.accept(true);
		}
	}

	/**
	 * {@link Boolean#TRUE}.equals(value)为非时调用
	 *
	 * @param value    Boolean
	 * @param consumer 消费者
	 */
	public static void nonTrueConsume(Boolean value, Consumer<Boolean> consumer) {
		if (!Boolean.TRUE.equals(value)) {
			consumer.accept(value);
		}
	}

	/**
	 * {@link Boolean#FALSE}.equals(value)为真时调用
	 *
	 * @param value    Boolean
	 * @param consumer 消费者
	 */
	public static void falseConsume(Boolean value, Consumer<Boolean> consumer) {
		if (Boolean.FALSE.equals(value)) {
			consumer.accept(false);
		}
	}

	/**
	 * 自定义验证器消费
	 *
	 * @param value 值
	 * @param test  验证器
	 * @param <T>   -
	 * @return {@link OptionalValue}
	 */
	public static <T> OptionalValue<T> predicateConsume(T value, Predicate<T> test) {
		return new OptionalValue<>(value, test != null && test.test(value));
	}

	/**
	 * 非空值消费
	 *
	 * @param value 值
	 * @param <T>   -
	 * @return {@link OptionalValue}
	 */
	public static <T> OptionalValue<T> nonNullConsume(T value) {
		return new OptionalValue<>(value, value != null);
	}

	/**
	 * 非空字符串值消费
	 *
	 * @param value 值
	 * @param <T>   -
	 */
	public static <T extends CharSequence> OptionalValue<T> nonEmptyConsume(T value) {
		return new OptionalValue<>(value, SpecialUtil.isNotEmpty(value));
	}

	public static <E, T extends Collection<E>> OptionalValue<T> nonEmptyConsume(T value) {
		return new OptionalValue<>(value, value != null && value.size() > 0);
	}

	/**
	 * 非0值消费
	 *
	 * @param value 值
	 */
	public static OptionalValue<BigDecimal> nonZeroConsume(BigDecimal value) {
		return new OptionalValue<>(value, value != null && BigDecimal.ZERO.compareTo(value) != 0);
	}

	/**
	 * 非0值消费
	 *
	 * @param value 值
	 */
	public static OptionalValue<BigDecimal> gtZeroConsume(BigDecimal value) {
		return new OptionalValue<>(value, value != null && BigDecimal.ZERO.compareTo(value) < 0);
	}

	/**
	 * 非0值消费
	 *
	 * @param value 值
	 */
	public static OptionalValue<BigDecimal> ltZeroConsume(BigDecimal value) {
		return new OptionalValue<>(value, value != null && BigDecimal.ZERO.compareTo(value) > 0);
	}

	/**
	 * {@link Boolean#TRUE}.equals(value)真时调用
	 *
	 * @param value Boolean
	 */
	public static OptionalValue<Boolean> trueConsume(Boolean value) {
		return new OptionalValue<>(value, Boolean.TRUE.equals(value));
	}

	/**
	 * {@link Boolean#TRUE}.equals(value)为非时调用
	 *
	 * @param value Boolean
	 */
	public static OptionalValue<Boolean> nonTrueConsume(Boolean value) {
		return new OptionalValue<>(value, !Boolean.TRUE.equals(value));
	}

	/**
	 * {@link Boolean#FALSE}.equals(value)为真时调用
	 *
	 * @param value Boolean
	 */
	public static OptionalValue<Boolean> falseConsume(Boolean value) {
		return new OptionalValue<>(value, Boolean.FALSE.equals(value));
	}

}
