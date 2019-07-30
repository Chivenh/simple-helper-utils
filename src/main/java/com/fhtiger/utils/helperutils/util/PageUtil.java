package com.fhtiger.utils.helperutils.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更进一步的对分页查询进行包装
 * 
 * @author LFH
 * @since 2017年4月21日
 */
public class PageUtil {

	private int currentPage = 1;
	private int pageSize = 15;
	private int max = 1;
	private int start = 0;
	private String order;

	public String getOrder() {
		return order;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getMax() {
		return max;
	}

	public int getStart() {
		return start;
	}

	public static PageUtil getPg(HttpServletRequest request) {
		int currentPage = Tutil.getInt(request.getParameter("page"), 1);
		int pageSize = Tutil.getInt(request.getParameter("rows"), 15);
		int max = pageSize;
		int start = (currentPage - 1) * max;
		return new PageUtil(currentPage, pageSize, max, start);
	}

	public static PageUtil pgOrder(HttpServletRequest request) {
		PageUtil pg = getPg(request);
		pg.order = getOrderStr(request);
		return pg;
	}

	private PageUtil() {

	}

	private PageUtil(int currentPage, int pageSize, int max, int start) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.max = max;
		this.start = start;
	}


	private PageUtil(int currentPage, int pageSize, int max, int start, String order) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.max = max;
		this.start = start;
		this.order = order;
	}

	/**
	 * 返回前台的数据
	 * 
	 * @author LFH
	 * @since 2017年9月27日 下午3:07:39
	 * @param rows  数据集
	 * @param total 总数
	 * @return {@link Map}
	 */
	public static Map<String, Object> out(List<?> rows, long total) {
		Map<String, Object> res = new HashMap<>();
		res.put("rows", rows);
		res.put("total", total);
		return res;
	}


	/**
	 * <p>用于多列排序时,根据请求获取排序sql.</p>
	 * <p>接收参数后去除前面的(::)符号,即可自己组装.</p>
	 * @author LFH
	 * @since 2017年7月12日 上午11:17:27
	 * @param request {@link HttpServletRequest}
	 * @return String
	 */
	public static String getOrderStr(HttpServletRequest request) {
		StringBuilder orderSql = new StringBuilder("::");
		String sortColumns = request.getParameter("sort");
		String order = request.getParameter("order");
		if (sortColumns != null && sortColumns.length() > 0 && order != null && order.length() > 0) {
			String[] sorts = sortColumns.split(",");
			String[] orders = order.split(",");
			for (int i = 0; i < sorts.length; i++) {
				orderSql.append(",").append(sorts[i]).append("  ").append(orders[i]);
			}
		}
		return orderSql.toString();
	}
	/**
	 * 从请求中获取需要的参数,组装成Map
	 * 
	 * @author LFH
	 * @since 2018年3月22日 上午9:47:28
	 * @param request {@link HttpServletRequest}
	 * @param keys 要获取参数的键
	 * @return {@link Map}
	 */
	public static Map<String, String> entryKeys(HttpServletRequest request, String... keys) {
		Map<String, String> _keys = new HashMap<>();
		if (keys != null && keys.length > 0) {
			for (String k : keys) {
				_keys.put(k, request.getParameter(k));
			}
		}
		return _keys;
	}
	
	/**
	 * 处理批量参数为sql in 条件
	 * 
	 * @author LFH
	 * @since 2018年3月22日 上午9:48:05
	 * @param field 匹配的字段名
	 * @param length IDS集合的大小
	 * @param sep 分割大小
	 * @return String
	 */
	public static String entryIds(String field, int length, int sep) {
		StringBuilder addSql = new StringBuilder(" (&#field in (");
		for (int i = 0; i < length; i++) {
			if (i > 0 && i % sep == 0) {
				addSql.append(") or &#field in (?");
			} else {
				addSql.append(",?");
			}
		}
		addSql.append("))");

		return addSql.toString().replaceFirst(",", "").replaceAll("&#field", field);
	}
}
