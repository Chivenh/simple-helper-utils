package com.fhtiger.helper.utils.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 清除静态资源中业务模块的缓存,以免开发过程调试麻烦
 * <ul>
 *     <li>1.在过滤器参数中使用exurl，使用正则表达式标记使用缓存的文件或目录</li>
 *     <li>2.在前端请求路径中添加cache=true参数，标记此资源请求使用缓存</li>
 * </ul>
 * @author Chivenh
 * @since 2020-05-18 12:00
 */
@SuppressWarnings({"unused"})
public class ClearCacheFilter implements Filter {
    /**
     * 匹配忽略列表的正则集合
     */
    private List<Pattern> patterns ;

    public ClearCacheFilter() {
        this.patterns = new ArrayList<>();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        /* 忽略的静态资源列表.*/
        String exurl = filterConfig.getInitParameter("exurl");
        String[] curls = new String[] {};
        if (exurl != null && exurl.trim().length() > 0) {
            curls = exurl.split("\\s*,\\s*");
        }
        for (String ci : curls) {
            if (!ci.startsWith("/")) {
                ci = "/" + ci;
            }
            this.patterns.add(Pattern.compile(ci));
        }
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String url = httpRequest.getRequestURI();
        /*标记此请求使用缓存*/
        boolean cache = Boolean.parseBoolean(httpRequest.getParameter("cache"));
        if(!cache){
            /*排队允许缓存的请求路径*/
            cache = this.patterns.stream().anyMatch(i -> {
                boolean bi = i.matcher(url).matches();
                return bi;
            });
        }
        if (cache) {
            filterChain.doFilter(request, response);
            return;
        }
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        FixResponseHelper.noCache(httpResponse);
        filterChain.doFilter(request, response);
    }
    @Override
    public void destroy() {
        this.patterns.clear();
    }
}