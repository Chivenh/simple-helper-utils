package com.fhtiger.utils.helperutils.json;

/**
 * 用于数据库实体类的序列化.可自定义迭代深度.
 * 使用JsonEntitySerializer对数据格式化为JSON字符串,
 * 先将数据转换为map,防止ObjectMapper将对重复数据仅输出ID.
 * 同时必须定义迭代深度,防止出现死循环
 * 
 * @author LFH
 * @since 2019/7/30 18:18
 * @version 0.0.1
 */
public interface JsonSerializer {
	/**
	 * 序列化实体对象entity
	 * 
	 * @param obj 要序列化的对象
	 * @return String
	 */
	 String serialize(Object obj);

}
