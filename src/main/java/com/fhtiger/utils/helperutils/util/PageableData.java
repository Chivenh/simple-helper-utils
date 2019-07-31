package com.fhtiger.utils.helperutils.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 分页对象数据
 *
 * @author LFH
 * @param  <T> T
 * @since 2018年11月15日 09:24
 */
public class PageableData<T> {
	private Long total;
	private Integer page;
	private List<T> rows;
	private Long totalPage;

	private PageableData(Long total, Integer page, List<T> rows, Long totalPage) {
		this.total = total;
		this.page = page;
		this.rows = rows;
		this.totalPage = totalPage;
	}

	public static <T> PageableData<T> build(Long total, List<T> rows){
		return new PageableData<>(total,null,rows,null);
	}

	public static <T>PageableData<T> build(Long total, List<T> rows,Long totalPage){
		return new PageableData<>(total,null,rows,totalPage);
	}

	public static <T>PageableData<T> build(Long total,Integer page, List<T> rows,Long totalPage){
		return new PageableData<>(total,page,rows,totalPage);
	}

	public Long getTotal() {
		return Optional.ofNullable(total).orElse(0L);
	}

	public Integer getPage() {
		return Optional.ofNullable(page).orElse(1);
	}

	public List<T> getRows() {
		return Optional.ofNullable(rows).orElseGet(ArrayList::new);
	}

	public Long getTotalPage() {
		return Optional.ofNullable(totalPage).orElse(0L);
	}
}
