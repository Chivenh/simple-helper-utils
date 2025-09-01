package com.fhtiger.helper.utils.web;


import com.fhtiger.helper.utils.TimeUtil;
import com.fhtiger.helper.utils.optional.OptionalConsumer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * HttpCacheBuilder
 *
 * @author Chivenh
 * @since 2021年01月26日 09:02
 */
@SuppressWarnings({ "unused","WeakerAccess" })

public class ResponseCacheBuilder {

	/* 和缓存相关的Head值Key */

	public static final String LAST_MODIFIED_KEY = "Last-Modified";
	public static final String IF_MODIFIED_SINCE_KEY = "If-Modified-Since";
	public static final String IF_NONE_MATCH_KEY = "If-None-Match";
	public static final String ETAG_KEY = "ETag";
	public static final String EXPIRES_KEY = "Expires";
	public static final String CACHE_CONTROL_KEY = "Cache-Control";
	public static final Date IN_VALID_DATE= TimeUtil.toDate( LocalDateTime.ofEpochSecond(1,1, ZoneOffset.UTC));

	private Date lastModified;
	private String etag;
	private Date expires;
	private Long maxAgeSeconds;
	private Long sMaxAgeSeconds;
	private boolean noCache;
	private boolean noStore;
	private boolean signPublic;
	private boolean signPrivate;

	public ResponseCacheBuilder setLastModified(final Date lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	public ResponseCacheBuilder setEtag(final String etag) {
		this.etag = etag;
		return this;
	}

	public ResponseCacheBuilder setExpires(final Date expires) {
		this.expires = expires;
		return this;
	}

	public ResponseCacheBuilder setMaxAgeSeconds(final Long maxAgeSeconds) {
		this.maxAgeSeconds = maxAgeSeconds;
		return this;
	}

	public ResponseCacheBuilder setSMaxAgeSeconds(final Long sMaxAgeSeconds) {
		this.sMaxAgeSeconds = sMaxAgeSeconds;
		return this;
	}

	public ResponseCacheBuilder setNoCache(final boolean noCache) {
		this.noCache = noCache;
		return this;
	}

	public ResponseCacheBuilder setNoStore(final boolean noStore) {
		this.noStore = noStore;
		return this;
	}

	public ResponseCacheBuilder setSignPublic(final boolean signPublic) {
		this.signPublic = signPublic;
		return this;
	}

	public ResponseCacheBuilder setSignPrivate(final boolean signPrivate) {
		this.signPrivate = signPrivate;
		return this;
	}

	ResponseCache build() {
		return new ResponseCache(this.lastModified, this.etag, this.expires, this.maxAgeSeconds, this.sMaxAgeSeconds, this.noCache, this.noStore, this.signPublic, this.signPrivate);
	}

	public static ResponseCacheBuilder create() {
		return new ResponseCacheBuilder();
	}

	static class ResponseCache {

		/**
		 * @see #LAST_MODIFIED_KEY
		 */
		private final Date lastModified;

		/**
		 * @see #ETAG_KEY
		 */
		private final String etag;

		/**
		 * @see #EXPIRES_KEY
		 */
		private final Date expires;

		/**
		 * max-age=秒
		 *
		 * @see #CACHE_CONTROL_KEY
		 */
		private final Long maxAgeSeconds;

		/**
		 * s-maxage=秒
		 *
		 * @see #CACHE_CONTROL_KEY
		 */
		private final Long sMaxAgeSeconds;
		/**
		 * no-cache
		 *
		 * @see #CACHE_CONTROL_KEY
		 */
		private final boolean noCache;
		/**
		 * no-store
		 *
		 * @see #CACHE_CONTROL_KEY
		 */
		private final boolean noStore;
		/**
		 * public
		 *
		 * @see #CACHE_CONTROL_KEY
		 */
		private final boolean signPublic;
		/**
		 * private
		 *
		 * @see #CACHE_CONTROL_KEY
		 */
		private final boolean signPrivate;

		private Map<String, Object> headerMap;

		private ResponseCache(Date lastModified, String etag, Date expires, Long maxAgeSeconds, Long sMaxAgeSeconds,
				boolean noCache, boolean noStore, boolean signPublic, boolean signPrivate) {
			this.lastModified = lastModified;
			this.etag = etag;
			this.expires = expires;
			this.maxAgeSeconds = maxAgeSeconds;
			this.sMaxAgeSeconds = sMaxAgeSeconds;
			this.noCache = noCache;
			this.noStore = noStore;
			this.signPublic = signPublic;
			this.signPrivate = signPrivate;
			this.fixHeaders();
		}

		public Map<String, Object> getHeaderMap() {
			return headerMap;
		}

		private void fixHeaders() {
			this.headerMap = new HashMap<>();

			final List<String> cacheControl = new ArrayList<>();

			Date now = TimeUtil.nowDateTime();

			OptionalConsumer.nonNullConsume(this.lastModified, lm -> {

				if(lm.before(now)){
					this.headerMap.put(LAST_MODIFIED_KEY, IN_VALID_DATE);
					return ;
				}

				this.headerMap.put(LAST_MODIFIED_KEY, lm);
			});

			OptionalConsumer.nonEmptyConsume(this.etag, et -> this.headerMap.put(ETAG_KEY, String.format("W/\"%s\"", et)));

			OptionalConsumer.nonNullConsume(this.expires, ex -> {
				if(ex.before(now)){
					this.headerMap.put(EXPIRES_KEY, IN_VALID_DATE);
					return ;
				}

				this.headerMap.put(EXPIRES_KEY, ex);
			});

			OptionalConsumer.nonNullConsume(this.maxAgeSeconds, maxAge -> cacheControl.add("max-age=" + maxAge));

			OptionalConsumer.nonNullConsume(this.sMaxAgeSeconds, maxAge -> cacheControl.add("s-maxage=" + maxAge));

			if (this.noCache) {
				cacheControl.add("no-cache");
			}

			if (this.noStore) {
				cacheControl.add("no-store");
			}

			if (this.signPublic) {
				cacheControl.add("public");
			}

			if (this.signPrivate) {
				cacheControl.add("private");
			}

			if (!cacheControl.isEmpty()) {
				this.headerMap.put(CACHE_CONTROL_KEY, cacheControl);
			}

		}
	}
}
