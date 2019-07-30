package com.fhtiger.utils.helperutils.util;

/**
 * 运行时异常.可用于将一些不抛出异常的方法中的异常捕获后，转换为此异常抛出
 * 
 * @author LFH
 * @since 2019/7/30 14:58
 * @version 0.0.1
 */
public class HelperException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** 异常代码 */
	private String code;

	public HelperException() {
		super();
	}

	public HelperException(String code, String message) {
		super(message);
		this.code = code;
	}

	public HelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HelperException(String message, Throwable cause) {
		super(message, cause);
	}

	public HelperException(String message) {
		super(message);
	}

	public HelperException(Throwable cause) {
		super(cause);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
