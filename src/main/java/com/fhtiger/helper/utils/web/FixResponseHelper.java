package com.fhtiger.helper.utils.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * FixResponseHelper
 * 处理响应 {@link HttpServletResponse}
 * @author Chivenh
 * @since 2020年04月26日 14:01
 */
@SuppressWarnings({ "unused","WeakerAccess" })

public final class FixResponseHelper {

	private FixResponseHelper()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 设置响应头，消除响应缓存
	 * @param httpResponse 响应对象
	 */
	public static void noCache(@NotNull HttpServletResponse httpResponse){

		ResponseCacheBuilder builder = ResponseCacheBuilder.create().setExpires(ResponseCacheBuilder.IN_VALID_DATE).setNoCache(true).setNoStore(true);

		cache(httpResponse,builder);
	}

	/**
	 * 响应头中cache相关信息设置
	 *
	 * @param httpResponse {@link HttpServletResponse} 请求响应对象
	 * @param cacheBuilder {@link ResponseCacheBuilder#create()}
	 */
	public static void cache(HttpServletResponse httpResponse, ResponseCacheBuilder cacheBuilder){
		Objects.requireNonNull(cacheBuilder,"缓存配置对象不能为空!");

		Map<String,Object> headerMap = cacheBuilder.build().getHeaderMap();

		headerMap.forEach((k,v)->{
			if(v instanceof Date){
				httpResponse.setDateHeader(k,((Date)v).getTime());
			}else if(ResponseCacheBuilder.CACHE_CONTROL_KEY.equals(k)){
				@SuppressWarnings("unchecked")
				List<String> cacheControl = (List<String>)v;

				boolean setted=false;

				for (String cv : cacheControl) {
					if(!setted){
						setted=true;
						httpResponse.setHeader(ResponseCacheBuilder.CACHE_CONTROL_KEY,cv);
					}else{
						httpResponse.addHeader(ResponseCacheBuilder.CACHE_CONTROL_KEY,cv);
					}
				}
			}else{
				httpResponse.setHeader(k,v.toString());
			}
		});
	}

}
