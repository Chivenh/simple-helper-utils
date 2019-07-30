package com.fhtiger.utils.helperutils.wrapper;

import java.io.Serializable;

/**
 * 对实体类的封装,可用与VO和PO的转换。
 * 一般的，用于将PO转换为VO
 * 
 * @author LuoGang
 *
 * @param <E> 要封装的数据类型,一般为某个Entity
 */
public interface Wrapper<E> extends Serializable {

	/**
	 * 从T对象obj中读取数据到this
	 * 
	 * @param obj 要封装的对象
	 */
	void wrap(E obj);

	/**
	 * 从this中得返回T对象
	 * 
	 * @return E
	 */
	E unwrap();

}
