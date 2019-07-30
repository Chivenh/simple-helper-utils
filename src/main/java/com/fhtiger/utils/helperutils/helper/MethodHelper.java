package com.fhtiger.utils.helperutils.helper;

import com.fhtiger.utils.helperutils.util.HelperException;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 通过Java反射处理Method
 * 
 * @author LFH
 * @since 2019/7/30 15:51
 */
public class MethodHelper {

	/**
	 * 找到一个名为methodName的方法.首先返回不带参数的方法， 如果没有，则返回第一个public方法，还没有则返回找到的第一个方法
	 * 
	 * @param cls        class
	 * @param methodName 方法名
	 * @return {@link Method}
	 */
	public static Method findOne(Class<?> cls, String methodName) {
		Method method = MethodHelper.get(cls, methodName);
		if (method != null) return method;
		Method[] methods = cls.getDeclaredMethods();
		for (Method m : methods) {
			if (methodName.equals(m.getName())) {
				if (Modifier.isPublic(m.getModifiers())) return m;
				if (method == null) method = m;
			}
		}
		return method;
	}

	/**
	 * 得到一个方法
	 * 
	 * @param cls            class
	 * @param methodName     方法名
	 * @param parameterTypes 参数类型那个.注意对应基本类型如long,请使用Long.TYPE,不能用Long.class
	 * @return {@link Method}
	 */
	public static Method get(Class<?> cls, String methodName, Class<?>... parameterTypes) {
		try {
			return cls.getDeclaredMethod(methodName, parameterTypes);
		} catch (SecurityException | NoSuchMethodException e) {
			throw new HelperException(e);
		}
	}

	/**
	 * 得到obj对象的methodName方法
	 * 
	 * @param obj            对象
	 * @param methodName 	方法名
	 * @param parameterTypes	参数描述
	 * @return {@link Method}
	 */
	public static Method get(Object obj, String methodName, Class<?>... parameterTypes) {
		return get(obj.getClass(), methodName, parameterTypes);
	}

	/**
	 * 执行对象o的method方法
	 * 
	 * @param obj    对象
	 * @param method 方法
	 * @param args 参数
	 * @throws IllegalArgumentException 执行异常
	 * @return 方法执行结果
	 */
	public static Object invoke(Object obj, Method method, Object... args) {
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		try {
			return method.invoke(obj, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new HelperException(e);
		}
	}

	/**
	 * 
	 * 执行对象o的名称为methodName的方法
	 * 
	 * @param obj            对象
	 * @param methodName 方法名
	 * @param args 参数
	 * @param parameterTypes 参数类型
	 * @return 方法执行结果
	 */
	public static Object invoke(Object obj, String methodName, Object[] args, Class<?>[] parameterTypes) {
		return invoke(obj, get(obj.getClass(), methodName, parameterTypes), args);
	}

	/**
	 * 执行对象obj的methodName方法
	 * 
	 * @param obj        对象
	 * @param methodName 方法名称
	 * @param args 参数
	 * @return 方法执行结果
	 */
	public static Object invoke(Object obj, String methodName, Object... args) {
		int l = 0;
		Class<?>[] parameterTypes = null;
		if (args != null && (l = args.length) > 0) {
			parameterTypes = new Class[l];
			for (int i = 0; i < l; i++) {
				parameterTypes[i] = (args[i] == null ? null : args[i].getClass());
			}
		}
		return invoke(obj, get(obj.getClass(), methodName, parameterTypes), args);
	}

	/**
	 * 执行对象的close方法
	 * 
	 * @param o {@link Closeable}
	 */
	public static <T extends  Closeable> void close(T o) {
		try {
			o.close();
		} catch (IOException e) {
			throw new  HelperException(e);
		}
	}

}
