package com.fhtiger.helper.utils;

/**
 * SimpleResult
 * 简单返回值对象
 * 
 * @author Chivenh
 * @since 2020年04月26日 14:46
 * @param <T> 数据类型
 */
@SuppressWarnings({ "unused","WeakerAccess" })

public final class SimpleResult<T> {

	private final String message;

	private final boolean success;

	private final String code;

	private final T data;

	private SimpleResult(String message, boolean success,String code, T data) {
		this.message = message;
		this.success = success;
		this.code=code;
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	public String getCode() {
		return code;
	}

	/**
	 * {@link SimpleResult} -Builder
	 */
	public static class SimpleResultBuilder<T> {

		private String message;

		private Boolean success;

		private String code;

		private T data;

		/**
		 * 根据初始化状态得到 {@link SimpleResult} -Builder
		 * @param success 初始化状态
		 * @return {@link SimpleResultBuilder}
		 * @param <T> 数据类型
		 */
		public static <T> SimpleResultBuilder<T> of(boolean success) {
			SimpleResultBuilder<T> builder = new SimpleResultBuilder<>();
			builder.success = success;
			return builder;
		}

		public SimpleResultBuilder<T> success() {
			this.success = true;
			return this;
		}

		public SimpleResultBuilder<T> fail() {
			this.success = false;
			return this;
		}

		public SimpleResultBuilder<T> message(String message) {
			this.message = message;
			return this;
		}

		public SimpleResultBuilder<T> code(String code){
			this.code = code;
			return this;
		}

		public SimpleResultBuilder<T> data(T data) {
			this.data = data;
			return this;
		}

		public SimpleResult<T> build(){
			return new SimpleResult<>(this.message,this.success,this.code,this.data);
		}

	}
}
