package com.fhtiger.helper.utils.springweb;

import java.util.Optional;

/**
 * RestSupportProperties
 * 三方服务调用相关配置
 * @author LFH
 * @since 2021年02月04日 08:59
 */
@SuppressWarnings({ "unused" })
public class RestSupportProperties {

	/**
	 * {@link org.springframework.http.client.SimpleClientHttpRequestFactory#setBufferRequestBody(boolean)}
	 */
	protected boolean bufferRequestBody = true;
	/**
	 * {@link org.springframework.http.client.SimpleClientHttpRequestFactory#setChunkSize(int)}
	 */
	protected int chunkSize = 4096;
	/**
	 * {@link org.springframework.http.client.SimpleClientHttpRequestFactory#setConnectTimeout(int)}
	 */
	protected int connectTimeout = 10000;
	/**
	 * {@link org.springframework.http.client.SimpleClientHttpRequestFactory#setReadTimeout(int)}
	 */
	protected int readTimeout = 10000;
	/**
	 * {@link org.springframework.http.client.SimpleClientHttpRequestFactory#setOutputStreaming(boolean)}
	 */
	protected boolean outputStreaming = true;

	public RestSupportProperties( Boolean bufferRequestBody, Integer chunkSize, Integer connectTimeout,
			Integer readTimeout, Boolean outputStreaming) {
		this.bufferRequestBody = !Boolean.FALSE.equals( bufferRequestBody);
		this.chunkSize = Optional.ofNullable( chunkSize).orElse(this.chunkSize);
		this.connectTimeout = Optional.ofNullable( connectTimeout).orElse(this.connectTimeout);
		this.readTimeout = Optional.ofNullable( readTimeout).orElse(this.readTimeout);
		this.outputStreaming = !Boolean.FALSE.equals( outputStreaming);
	}

	public boolean isBufferRequestBody() {
		return bufferRequestBody;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public boolean isOutputStreaming() {
		return outputStreaming;
	}
}
