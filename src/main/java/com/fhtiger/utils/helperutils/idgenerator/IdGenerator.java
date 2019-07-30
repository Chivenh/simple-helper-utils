package com.fhtiger.utils.helperutils.idgenerator;

/**
 * 主键生成接口
 * 
 * @author LFH
 *
 * @param <ID> ID泛型约束
 */
public interface IdGenerator<ID> {

	/**
	 * 生成主键
	 * 
	 * @return ID
	 */
	ID generate();
}
