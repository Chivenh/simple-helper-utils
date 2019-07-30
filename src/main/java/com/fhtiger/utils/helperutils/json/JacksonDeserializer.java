package com.fhtiger.utils.helperutils.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 * 使用Jackson的ObjectMapper进行JSON反序列化
 * 
 * @author LFH
 * @since 2019/7/30 18:15
 * @version 0.0.1
 */
public class JacksonDeserializer extends JacksonSerializer implements JsonDeserializer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;

	public JacksonDeserializer() {
		super();
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public <T> T deserialize(String json, Class<T> type) {
		try {
			return this.readValue(json, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 反序列化json字符串.
	 * 默认反序列化为Map，如果不反序列化为Map，则尝试反序列化为List
	 * 
	 * @param json json字符串
	 * @return Map/List
	 */
	public Object deserialize(String json) {
		if (json == null) return null;
		if (!json.trim().startsWith("[")) {
			try {
				return this.readValue(json, Map.class);
			} catch (IOException e) {
				logger.error("error:{0}",e);
			}
		}
		try {
			return this.readValue(json, List.class);
		} catch (IOException e) {
			logger.error("error:{0}",e);
		}

		return null;
	}

}
