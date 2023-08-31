package com.fhtiger.helper.utils.helpful;

import org.springframework.lang.NonNull;

/**
 * CodeGeneratorHelper
 *
 * @author Chivenh
 * @since 2021年02月02日 14:51
 */
@SuppressWarnings({ "unused","WeakcerAccess" })

public final class CodeGeneratorHelper {

	private CodeGeneratorHelper()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	private static final char[] CODE_MAPPER_NUMBER= new char[10];

	static {
		char first = 'A';

		int i=0;

		while (i<CODE_MAPPER_NUMBER.length){
			CODE_MAPPER_NUMBER[i]=(char) (first+i);
			i++;
		}
	}

	public static String codeById(@NonNull Long id){

		StringBuilder code = new StringBuilder();

		char [] originalId = id.toString().toCharArray();
		char start='0';

		for (char c : originalId) {
			code.append(CODE_MAPPER_NUMBER[c-start]);
		}

		return code.toString();

	}

	public static String lowCaseCodeById(Long id){
		return codeById(id).toLowerCase();
	}

	public static Long idByCode(@NonNull String code){
		code = code.toUpperCase();
		StringBuilder id = new StringBuilder();
		char first = CODE_MAPPER_NUMBER[0];
		char [] originalId = code.toCharArray();
		int index;
		for (char c : originalId) {
			index = c-first;
			if(index>9){
				return -1L;
			}
			id.append(index);
		}

		return Long.parseLong(id.toString());
	}
}
