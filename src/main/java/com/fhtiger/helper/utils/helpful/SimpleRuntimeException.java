package com.fhtiger.helper.utils.helpful;

/**
 * SimpleRuntimeException
 *
 * @author Chivenh
 * @since 2023年09月01日 15:54
 */
@SuppressWarnings({ "unused" })

public class SimpleRuntimeException extends RuntimeException{
	public SimpleRuntimeException() {
	}

	public SimpleRuntimeException(String message) {
		super(message);
	}

	public SimpleRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimpleRuntimeException(Throwable cause) {
		super(cause);
	}

	public SimpleRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
