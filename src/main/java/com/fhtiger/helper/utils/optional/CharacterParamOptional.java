package com.fhtiger.helper.utils.optional;


import com.fhtiger.helper.utils.SpecialUtil;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * CharacterParamOptional
 *
 * @author Chivenh
 * @since 2020年04月07日 19:22
 */
@SuppressWarnings("unused")
public final class CharacterParamOptional<T extends CharSequence> {

	private static final CharacterParamOptional<?> EMPTY = new CharacterParamOptional<>();

	public static <T extends CharSequence> CharacterParamOptional<T> empty() {
		@SuppressWarnings("unchecked") CharacterParamOptional<T> t = (CharacterParamOptional<T>) EMPTY;
		return t;
	}

	private final T value;

	private CharacterParamOptional() {
		this.value = null;
	}

	private CharacterParamOptional(T value) {
		this.value = Objects.requireNonNull(value);
	}

	public static <T extends CharSequence> CharacterParamOptional<T> ofEmptyAble(T value){
		return SpecialUtil.isEmpty(value) ? empty() : new CharacterParamOptional<>(value);
	}

	/**
	 * Return the value if present, otherwise return {@code other}.
	 *
	 * @param other the value to be returned if there is no value present, may
	 * be null
	 * @return the value, if present, otherwise {@code other}
	 */
	public T orElse(T other) {
		return this.isNotEmpty() ? value : other;
	}

	/**
	 * Return the value if present, otherwise invoke {@code other} and return
	 * the result of that invocation.
	 *
	 * @param other a {@code Supplier} whose result is returned if no value
	 * is present
	 * @return the value if present otherwise the result of {@code other.get()}
	 * @throws NullPointerException if value is not present and {@code other} is
	 * null
	 */
	public T orElseGet(Supplier<? extends T> other) {
		return this.isNotEmpty() ? value : other.get();
	}

	/**
	 * Return the contained value, if present, otherwise throw an exception
	 * to be created by the provided supplier.
	 * A method reference to the exception constructor with an empty
	 * argument list can be used as the supplier. For example,
	 * {@code IllegalStateException::new}
	 *
	 * @param <X> Type of the exception to be thrown
	 * @param exceptionSupplier The supplier which will return the exception to
	 * be thrown
	 * @return the present value
	 * @throws X if there is no value present
	 * @throws NullPointerException if no value is present and
	 * {@code exceptionSupplier} is null
	 */
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (value != null) {
			return value;
		} else {
			throw exceptionSupplier.get();
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this CharacterParamOptional. The
	 * other object is considered equal if:
	 * <ul>
	 * <li>it is also an {@code CharacterParamOptional} and;
	 * <li>both instances have no value present or;
	 * <li>the present values are "equal to" each other via {@code equals()}.
	 * </ul>
	 *
	 * @param obj an object to be tested for equality
	 * @return {code true} if the other object is "equal to" this object
	 * otherwise {@code false}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CharacterParamOptional)) {
			return false;
		}

		CharacterParamOptional<?> other = (CharacterParamOptional<?>) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean isNotEmpty(){
		return SpecialUtil.isNotEmpty(this.value);
	}

	public void ifNotEmpty(Consumer<? super T> consumer){
		if(this.isNotEmpty()){
			consumer.accept(this.value);
		}
	}

	public static <T extends CharSequence> void valueConsumer(T value,Consumer<? super T> consumer){
		if(SpecialUtil.isNotEmpty(value)){
			consumer.accept(value);
		}
	}
}
