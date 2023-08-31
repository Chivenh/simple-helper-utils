package com.fhtiger.helper.utils.servletx;

import javax.servlet.*;
import java.io.IOException;

/**
 * CustomFilterWrapper
 * 包装引用 {@link CustomFilter} 为实际可用的 {@link Filter} 实例
 * @author Chivenh
 * @since 2021年01月28日 09:12
 */
@SuppressWarnings({ "unused" })

public class CustomFilterWrapper implements Filter {

	private final CustomFilter customFilter;

	public CustomFilterWrapper(CustomFilter customFilter) {
		this.customFilter = customFilter;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.customFilter.init(filterConfig);
	}

	@Override
	public void destroy() {
		this.customFilter.destroy();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		this.customFilter.doFilter(request,response,chain);
	}
}
