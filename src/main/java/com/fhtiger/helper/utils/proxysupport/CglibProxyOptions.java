package com.fhtiger.helper.utils.proxysupport;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CglibProxyOptions
 * 提供给{@link CglibProxyHelper#getProxy(Class, CglibProxyOptions)} 来生成代理类的参数对象
 *
 * @author Chivenh
 * @since 2021年01月29日 12:59
 */
@SuppressWarnings({ "unused" })

public final class CglibProxyOptions {

	/**
	 * 回调-即代理列表
	 */
	private final List<Callback> callbackList;

	/**
	 * 代理匹配返回要执行的代理序号
	 */
	private final CallbackFilter callbackFilter;

	/**
	 * 构造方法的参数列表
	 */
	private final Object[] arguments;

	/**
	 * 构造方法的参数类型列表
	 */
	private final Class<?>[] argumentTypes;

	private CglibProxyOptions(List<Callback> callbackList, CallbackFilter callbackFilter, Object[] arguments, Class<?>[] argumentTypes) {
		this.callbackList = callbackList;
		this.callbackFilter = callbackFilter;
		this.arguments = arguments;
		this.argumentTypes = argumentTypes;
	}

	Object customize(Enhancer enhancer) {

		enhancer.setCallbacks(this.getCallbackList());

		if (this.getCallbackFilter() != null) {
			enhancer.setCallbackFilter(this.getCallbackFilter());
		}

		if (this.getArguments() != null && this.getArguments().length > 0) {
			return enhancer.create(this.getArgumentTypes(), this.getArguments());
		} else {
			return enhancer.create();
		}
	}

	public Callback[] getCallbackList() {
		return callbackList.toArray(new Callback[0]);
	}

	public CallbackFilter getCallbackFilter() {
		return callbackFilter;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}

	public static Builder builder(List<Callback> callbackList) {
		return new Builder(callbackList);
	}

	public static Builder builder(Callback callback) {
		return new Builder(callback);
	}

	public static class Builder {
		private List<Callback> callbackList;
		private CallbackFilter callbackFilter;
		private Object[] arguments;
		private Class<?>[] argumentTypes;

		/**
		 * -
		 *
		 * @param callbackList 代理列表
		 */
		Builder(List<Callback> callbackList) {
			this.callbackList = Optional.ofNullable(callbackList).orElse(new ArrayList<>());
		}

		/**
		 * -
		 *
		 * @param callback 代理
		 */
		Builder(Callback callback) {
			this.addCallback(callback);
		}

		/**
		 * -
		 *
		 * @param callbackList -
		 * @return -
		 */
		public Builder setCallbackList(final List<Callback> callbackList) {
			this.callbackList = callbackList;
			return this;
		}

		/**
		 * -
		 *
		 * @param callback -
		 * @return -
		 */
		public Builder addCallback(final Callback callback) {
			if (this.callbackList == null) {
				this.callbackList = new ArrayList<>();
			}
			if (callback != null) {
				this.callbackList.add(callback);
			}
			return this;
		}

		/**
		 * 添加代理过滤/匹配器,当{@link #callbackList}列表数量大于1时，需要指定
		 *
		 * @param callbackFilter 代理过滤/匹配器
		 * @return -
		 */
		public Builder setCallbackFilter(final CallbackFilter callbackFilter) {
			this.callbackFilter = callbackFilter;
			return this;
		}

		/**
		 * 设置构造函数参数列表
		 *
		 * @param arguments 构造函数参数列表
		 * @return -
		 */
		public Builder setArguments(final Object[] arguments) {
			this.arguments = arguments;
			return this;
		}

		/**
		 * 设置构造函数参数类型列表
		 *
		 * @param argumentTypes 构造函数参数类型列表
		 * @return -
		 */
		public Builder setArgumentTypes(final Class<?>[] argumentTypes) {
			this.argumentTypes = argumentTypes;
			return this;
		}

		/**
		 * 构造函数参数
		 *
		 * @param argumentTypes 构造函数参数类型列表
		 * @param arguments     构造函数参数列表
		 * @return -
		 */
		public Builder withArguments(final Class<?>[] argumentTypes, final Object[] arguments) {
			return this.setArgumentTypes(argumentTypes).setArguments(arguments);
		}

		private void fix() {
			if (this.callbackList == null || this.callbackList.size() < 1) {
				throw new RuntimeException("Empty callbackList is not enable to proxy the target!");
			}
			if(this.callbackList.size()>1 && this.callbackFilter==null){
				throw new RuntimeException("Multiple callback need the callbackFilter specified!");
			}
			if (this.arguments != null && this.arguments.length > 0) {
				int aLength = this.arguments.length;
				if (this.argumentTypes == null || aLength != this.argumentTypes.length) {
					Class<?>[] theArgumentTypes = new Class[aLength];
					Class<?>[] argumentTypes = this.argumentTypes;
					if (argumentTypes == null) {
						argumentTypes = new Class[0];
					}
					int atLength = argumentTypes.length;
					for (int i = 0; i < aLength; i++) {
						if (i < atLength) {
							theArgumentTypes[i] = argumentTypes[i];
						} else {
							theArgumentTypes[i] = this.arguments[i].getClass();
						}
					}
					this.argumentTypes = theArgumentTypes;
				}
			}
		}

		public CglibProxyOptions build() {
			this.fix();
			return new CglibProxyOptions(this.callbackList, this.callbackFilter, this.arguments, this.argumentTypes);
		}
	}
}
