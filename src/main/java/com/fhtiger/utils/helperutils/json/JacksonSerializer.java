package com.fhtiger.utils.helperutils.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fhtiger.utils.helperutils.helper.DateHelper;
import com.fhtiger.utils.helperutils.util.HelperException;

import java.text.SimpleDateFormat;

/**
 * 使用Jackson的ObjectMapper进行JSON序列化
 * 
 * @author LFH
 * @since 2019/7/30 18:16
 * @version 0.0.1
 */
public class JacksonSerializer extends ObjectMapper implements JsonSerializer {

	private static final long serialVersionUID = 1L;

	public JacksonSerializer() {
		super();
		configure(SerializationFeature.INDENT_OUTPUT, true);
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true); // 允许关键字不带引号
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 输出日期
		configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);

		setDateFormat(new SimpleDateFormat(DateHelper.DEFAULT_DATETIME_FORMAT));

		setSerializationInclusion(Include.NON_NULL);
	}

	@Override
	public String serialize(Object obj) {
		if (obj == null) return null;
		try {
			return writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new HelperException(e);
		}
	}

}
