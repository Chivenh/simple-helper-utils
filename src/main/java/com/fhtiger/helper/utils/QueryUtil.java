package com.fhtiger.helper.utils;


import com.fhtiger.helper.utils.adapter.SplitAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * QUtil
 * 查询相关工具
 *
 * @author Chivenh
 * @since 2020年04月30日 09:24
 */
@SuppressWarnings({ "unused", "WeakerAccess" })

public final class QueryUtil {

	private QueryUtil() throws IllegalAccessException {
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * <p>用于多列排序时,根据请求获取排序sql.</p>
	 *
	 * @param request {@link HttpServletRequest}
	 * @return String
	 * @author Chivenh
	 * @since 2020-04-30 9:26
	 */
	public static String getOrderStr(HttpServletRequest request) {
		String sortColumns = request.getParameter("sort");
		String order = request.getParameter("order");
		return getOrderStr(sortColumns, order);
	}

	/**
	 * <p>用于多列排序时,根据请求获取排序sql.</p>
	 *
	 * @param sortColumns 排序字段，逗号分隔
	 * @param order       排序方向
	 * @return String
	 */
	public static String getOrderStr(String sortColumns, String order) {
		if (sortColumns != null && sortColumns.length() > 0 && order != null && order.length() > 0) {
			String[] sorts = sortColumns.split(",");
			String[] orders = order.split(",");
			/*多列排序时，未指定order则继承上一列order*/
			String cacheOrder = "asc", curOrder;
			int sortsLength = sorts.length, ordersLength = orders.length;
			List<String> orderSorts = new ArrayList<>(sortsLength);
			for (int i = 0; i < sortsLength; i++) {
				if (ordersLength <= i) {
					curOrder = cacheOrder;
				} else {
					curOrder = cacheOrder = orders[i];
				}
				orderSorts.add(sorts[i] + " " + curOrder);
			}
			return String.join(",", orderSorts);
		}
		return "";
	}

	/**
	 * 从请求中获取需要的参数,组装成Map
	 *
	 * @param request {@link HttpServletRequest}
	 * @param keys    取值键
	 * @return &lt;Key,Value&gt;数据
	 * @author Chivenh
	 * @since 2020-04-30 9:27
	 */
	public static Map<String, String> entryKeys(HttpServletRequest request, String... keys) {
		if (keys != null && keys.length > 0) {
			Map<String, String> resultKeys = new HashMap<>(keys.length);
			for (String k : keys) {
				resultKeys.put(k, request.getParameter(k));
			}
			return resultKeys;
		}
		return Collections.emptyMap();
	}

	/**
	 * 处理批量参数为sql in 条件
	 *
	 * @param length IDS集合的大小
	 * @param field  匹配的字段名
	 * @param sep    分割大小
	 * @return ID拼装SQL
	 * @author Chivenh
	 * @since 2020-04-30 9:27
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
		String sql = addSql.toString().replaceFirst(",", "").replaceAll("&#field", field);
		return sql.trim();
	}

	/**
	 * 参数Like处理
	 *
	 * @param param 参数值
	 * @return like
	 */
	public static String paramLike(Object param) {
		return "%" + param + "%";
	}

	/**
	 * 参数Like处理,先trim再转换
	 *
	 * @param param 参数值
	 * @return like
	 */
	public static String paramTrimLike(Object param) {
		return "%" + SpecialUtil.getStr(param).trim() + "%";
	}

	/**
	 * 参数LeftLike处理
	 *
	 * @param param 参数值
	 * @return like
	 */
	public static String paramLeftLike(Object param) {
		return "%" + param;
	}

	/**
	 * 参数RightLike处理
	 *
	 * @param param 参数值
	 * @return like
	 */
	public static String paramRightLike(Object param) {
		return param + "%";
	}

	/**
	 * 将总数按页码分割表达，返回记录位节点列表。
	 *
	 * @param total    记录总数
	 * @param pageSize 页码大小
	 * @return -
	 */
	public static List<Integer> splitTotal(int total, int pageSize) {
		List<Integer> split = SplitAdapter.autoSplitBySize(total, pageSize);

		split.add(0, 0);

		return split;
	}

	/**
	 * 将总数按页码分割后，返回每页的起止位列表
	 *
	 * @param total    记录总数
	 * @param pageSize 页码大小
	 * @return -
	 */
	public static List<Integer[]> rangeRowNumber(int total, int pageSize) {
		List<Integer> split = splitTotal(total, pageSize);

		return rangeRowNumber(split);
	}

	/**
	 * 将记录位节点列表转换为每页的起止位列表
	 *
	 * @param splitTotal 记录位节点列表
	 * @return -
	 */
	public static List<Integer[]> rangeRowNumber(List<Integer> splitTotal) {
		List<Integer[]> range = new ArrayList<>();

		for (int i = 0, il = splitTotal.size() - 1; i < il; i++) {
			range.add(new Integer[] { splitTotal.get(i), splitTotal.get(i + 1) });
		}

		return range;
	}
}
