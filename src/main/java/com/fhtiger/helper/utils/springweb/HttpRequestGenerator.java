package com.fhtiger.helper.utils.springweb;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

/**
 * HttpRequestGenerator
 *
 * @author Chivenh
 * @since 2021年05月12日 17:15
 */
@SuppressWarnings({ "unused", "UnnecessaryLocalVariable" })

public final class HttpRequestGenerator {

	private HttpRequestGenerator() throws IllegalAccessException{
		throw new IllegalAccessException("此工具类无需初始化!");
	}

	public static HttpEntity<MultiValueMap<String, Object>> formPostRequest(MultiValueMap<String, Object> requestData) {

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, Object>> httpRequest = new HttpEntity<>(requestData, httpHeaders);

		return httpRequest;

	}

	public static HttpEntity<MultiValueMap<String, Object>> formMultiRequest(MultiValueMap<String, Object> requestData) {

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> httpRequest = new HttpEntity<>(requestData, httpHeaders);

		return httpRequest;

	}

	public static <T> HttpEntity<T> jsonPostRequest(T requestData) {

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<T> httpRequest = new HttpEntity<>(requestData, httpHeaders);

		return httpRequest;

	}
}
