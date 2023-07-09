package com.fhtiger.helper.utils.optional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * OptionalSupport
 * 对Optional的扩展支持
 * @author LFH
 * @since 2020年07月31日 16:38
 */
@SuppressWarnings({ "unused" })

public final class OptionalSupport {

	private OptionalSupport()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 获取值或默认值
	 *
	 * @param value 值
	 * @param defaultValues 默认值列表
	 * @param <T> 类型约束
	 * @return T
	 */
	@SafeVarargs
	public static <T> Optional<T> getOrDefault(final T value,final T... defaultValues){
		return getOrDefault(Objects::nonNull,value,defaultValues);
	}

	/**
	 * 获取值或默认值
	 *
	 * @param checker  值的检查器 {@link Predicate}
	 * @param value 值
	 * @param defaultValues 默认值列表
	 * @param <T> 类型约束
	 * @return T
	 */
	@SafeVarargs
	public static <T> Optional<T> getOrDefault(final Predicate<T> checker,final T value,final T... defaultValues){
		if(checker.test(value)){
			return Optional.ofNullable(value);
		}
		if(defaultValues==null){
			return Optional.empty();
		}
		for (T defaultValue : defaultValues) {
			if(checker.test(defaultValue)){
				return Optional.ofNullable(defaultValue);
			}
		}
		return Optional.empty();
	}
}
