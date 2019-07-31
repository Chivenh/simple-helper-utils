package com.fhtiger.utils.helperutils.json;

/**
 * 将JSON字符串反序列化
 * 
 * @author LFH
 * @since 2019/7/30 18:16
 * @version 0.0.1
 */
public interface JsonDeserializer {

	/**
	 * 
	 * 将JSON字符串反序列化
	 * 
	 * @param json json字符串
	 * @param type 返回的数据类型,可以为List,Map,或实体类
	 * @param <T> T
	 * @return T
	 */
	 <T> T deserialize(String json, Class<T> type);
}
