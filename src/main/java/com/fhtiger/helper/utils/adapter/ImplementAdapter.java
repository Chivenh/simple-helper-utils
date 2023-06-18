package com.fhtiger.helper.utils.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * ImplementAdapter
 *
 * @author LFH
 * @since 2020年08月04日 15:15
 */
@SuppressWarnings({ "unused" })

public class ImplementAdapter {

	public static  <T> List<Class<T>> getAllImplements(Class<T> interfaceClass){
		ClassLoader classLoader = interfaceClass.getClassLoader();
		Field field = null;
		try {
			Class<?> classOfClassLoader = classLoader.getClass();
			while (ClassLoader.class!=classOfClassLoader) {
				classOfClassLoader = classOfClassLoader.getSuperclass();
			}
			field = ClassLoader.class.getDeclaredField("classes");
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(
					"无法获取到当前线程的类加载器的classes域!");
		}

		try {
			List<Class<?>> loaders = new ArrayList<>( (Vector<Class<?>>)field.get(classLoader));
			List<Class<T>> results = new ArrayList<>();
			for (Class<?> loader : loaders) {

				if(interfaceClass.isAssignableFrom(loader)&&!interfaceClass.equals(loader)&&!Modifier.isAbstract(loader.getModifiers())){
					results.add((Class<T>) loader);
				}
			}
			return results;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
