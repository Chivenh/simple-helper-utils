package com.fhtiger.helper.utils.optional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Optional Test Value Consumer
 *
 * @author Chivenh
 * @since 2022年07月04日 17:28
 */
@SuppressWarnings({ "unused" })

public final class OptionalValue<T> {

	private final T value;

	private final boolean valid;

	OptionalValue(T value, boolean valid) {
		this.value = value;
		this.valid = valid;
	}

	public OptionalValue<T> then(Consumer<? super T> consumer) {
		if (this.valid) {
			consumer.accept(this.value);
		}
		return this;
	}

	public OptionalValue<T> otherwise(Consumer<? super T> consumer) {
		if (!this.valid) {
			consumer.accept(this.value);
		}
		return this;
	}

	public OptionalValue<T> done(Consumer<? super T> validConsumer, Consumer<? super T> invalidConsumer) {
		if (this.valid) {
			validConsumer.accept(this.value);
		} else {
			invalidConsumer.accept(this.value);
		}
		return this;
	}

	public <R> OptionalValue<R> map(Function<T, R> converter) {
		if (this.valid) {
			return new OptionalValue<>(converter.apply(this.value), true);
		}
		return new OptionalValue<>(null, false);
	}

	public <R> OptionalValue<R> map(Function<T, R> converter, Function<T, R> fallbackConverter) {
		if (this.valid) {
			return new OptionalValue<>(converter.apply(this.value), true);
		}
		return new OptionalValue<>(fallbackConverter.apply(this.value), false);
	}

	public <R> OptionalValue<R> map(Function<T, R> converter, R fallbackValue) {
		if (this.valid) {
			return new OptionalValue<>(converter.apply(this.value), true);
		}
		return new OptionalValue<>(fallbackValue, false);
	}
}
