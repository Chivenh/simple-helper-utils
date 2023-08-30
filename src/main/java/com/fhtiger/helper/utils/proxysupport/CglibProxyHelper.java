package com.fhtiger.helper.utils.proxysupport;

import org.springframework.cglib.proxy.Enhancer;

/**
 * CglibProxyHelper
 * 基于Cglib的代理工具
 *
 * @author LFH
 * @since 2021年01月29日 12:55
 */
@SuppressWarnings({ "unused", "WeakerAccess" })

public final class CglibProxyHelper {

	private CglibProxyHelper() throws IllegalAccessException {
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 使用Cglib生成目标类型的代理类
	 *
	 * @param clazz             代理目标类型
	 * @param cglibProxyOptions 代理参数对象
	 * @param <T>               -
	 * @return 代理类
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<? extends T> clazz, CglibProxyOptions cglibProxyOptions) {
		Enhancer enhancer = new Enhancer();

		enhancer.setClassLoader(clazz.getClassLoader());

		enhancer.setSuperclass(clazz);

		return (T) cglibProxyOptions.customize(enhancer);
	}
}
