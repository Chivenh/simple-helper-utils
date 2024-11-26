package com.fhtiger.helper.utils.helpful;

import java.util.function.Predicate;

import org.springframework.util.Assert;

/**
 * AssertUtil
 *
 * @author Chivenh
 * @since 2021年02月20日 14:08
 */
@SuppressWarnings({ "unused" })

public final class AssertDetect {
	private AssertDetect()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 多参数空值判断方法
	 * @param object 多参数输入
	 * @param message 多参数匹配异常消息
	 */
	public static void notNull(Object[] object,String... message){

		if(object==null ){
			return;
		}

		int oLength=object.length;

		int mLength=0;

		if(message!=null){
			mLength=message.length;
		}else{
			message=new String[0];
		}

		int indexMax  = Math.min(oLength,mLength) - 1;

		String defaultMessage = "object[%d],不能为NULL!";

		String messageFormat ;

		for (int i = 0; i < oLength; i++) {

			if (indexMax < 0) {
				messageFormat= defaultMessage;
			} else {
				if (indexMax >= i) {
					messageFormat = message[i];
				}
				else {
					messageFormat = defaultMessage;
				}
			}

			Assert.notNull(object[i],String.format(messageFormat,i));
		}
	}

	/**
	 * 多参数自定义校验
	 * @param object 多参数输入
	 * @param predicate 校验逻辑
	 * @param message 多参数匹配异常消息
	 */
	public static void test(Object[] object, Predicate<Object> predicate,String... message){

		if(object==null || predicate==null){
			return;
		}

		int oLength=object.length;

		int mLength=0;

		if(message!=null){
			mLength=message.length;
		}

		int indexMax  = Math.min(oLength,mLength) - 1;

		String defaultMessage = "object[%d],校验未通过!";

		String messageFormat ;

		boolean testResult;

		for (int i = 0; i < oLength; i++) {

			messageFormat=indexMax<0?defaultMessage:indexMax>=i?message[i]:defaultMessage;

			testResult= predicate.test(object[i]);

			if(!testResult){
				throw new IllegalArgumentException(String.format(messageFormat,i));
			}
		}
	}

	public static void test(boolean state,String message){
		if(!state){
			throw new IllegalArgumentException(message);
		}
	}
}
