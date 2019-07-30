package com.fhtiger.utils.helperutils.json;

/**
 * 实现了JsonSerializer，JsonDeserializer的继承于ObjectMapper的JSON处理对象
 * 
 * @author LFH
 * @since 2019/7/30 18:17
 * @version 0.0.1
 */
public class JsonMapper extends JacksonDeserializer implements JsonSerializer, JsonDeserializer {

	private static final long serialVersionUID = 1L;

	public JsonMapper() {
		super();
		init();
	}

	/**
	 * 初始化配置
	 */
	protected void init() {
		// TODO ;
	}

}
