package com.fhtiger.helper.utils.adapter;

import com.fhtiger.helper.utils.helpful.SimpleRuntimeException;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * ImplementAdapter
 *
 * @author Chivenh
 * @since 2020年08月04日 15:15
 */
@SuppressWarnings({ "unused" })

public class ImplementAdapter {

	private ImplementAdapter() {
	}

	public static  <T> List<Class<T>> getAllImplements(Class<T> interfaceClass){
		ClassLoader classLoader = interfaceClass.getClassLoader();
		Field field;
		try {
			Class<?> classOfClassLoader = classLoader.getClass();
			while (ClassLoader.class!=classOfClassLoader) {
				classOfClassLoader = classOfClassLoader.getSuperclass();
			}
			field = ClassLoader.class.getDeclaredField("classes");
		} catch (NoSuchFieldException e) {
			throw new SimpleRuntimeException(
					"无法获取到当前线程的类加载器的classes域!");
		}

		try {
			@SuppressWarnings("unchecked")
			List<Class<?>> loaders = new ArrayList<>( (Vector<Class<?>>)field.get(classLoader));
			List<Class<T>> results = new ArrayList<>(loaders.size());
			for (Class<?> loader : loaders) {

				if(interfaceClass.isAssignableFrom(loader)&&!interfaceClass.equals(loader)&&!Modifier.isAbstract(loader.getModifiers())){
					//noinspection unchecked
					results.add((Class<T>) loader);
				}
			}
			return results;
		} catch (IllegalAccessException e) {
			LogFactory.getLog(ImplementAdapter.class).error("error:",e);
		}

		return Collections.emptyList();
	}
}
