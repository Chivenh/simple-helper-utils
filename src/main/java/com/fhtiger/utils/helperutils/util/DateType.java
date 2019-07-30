package com.fhtiger.utils.helperutils.util;

import java.util.Locale;

/**
 * 转换日期类型
 *
 * @author LFH
 * @since 2019/7/30 13:23
 */
public enum DateType {
	/*** yyyy-MM-dd */
	A("yyyy-MM-dd"),

	/*** yyyy-M-d */
	A1("yyyy-M-d"),

	/*** yyyy-MM */
	A2("yyyy-MM"),
	/*** yyyy-M */
	A3("yyyy-M"),

	/*** yyyy/MM/dd */
	B("yyyy/MM/dd"),

	/*** yyyy/M/d */
	B1("yyyy/M/d"),

	/*** yyyy/M/d HH:mm:ss */
	B2("yyyy/MM/dd HH:mm:ss"),

	/*** yyyy/M/d HH:mm:ss */
	B3("yyyy/M/d HH:mm:ss"),

	/*** yyyy-MM-dd HH:mm:ss */
	C("yyyy-MM-dd HH:mm:ss"),

	/*** yyyy-M-d HH:mm:ss */
	C1("yyyy-M-d HH:mm:ss"),

	/*** d-MMM {@link Locale#ENGLISH} */
	D("d-MMM", Locale.ENGLISH),

	/*** d-MMM-yyyy {@link Locale#ENGLISH} */
	D1("d-MMM-yyyy", Locale.ENGLISH),

	/*** d/MMM/yyyy {@link Locale#ENGLISH} */
	D2("d/MMM/yyyy", Locale.ENGLISH),

	/*** d/MMM/yyyy HH:mm:ss {@link Locale#ENGLISH} */
	D3("d/MMM/yyyy HH:mm:ss", Locale.ENGLISH),

	/*** MMM.dd,yyyy {@link Locale#ENGLISH} */
	D4("MMM.dd,yyyy", Locale.ENGLISH),

	/*** MMM dd,yyyy {@link Locale#ENGLISH} */
	D5("MMM dd,yyyy", Locale.ENGLISH),

	/*** yyyy年M月d日 */
	E0("yyyy年M月d日"),

	/*** yyyy年MM月dd */
	E("yyyy年MM月dd"),

	/*** yyyy年MM月dd HH:mm:ss */
	E1("yyyy年MM月dd HH:mm:ss"),

	/*** yyyy年MM月dd日 */
	E2("yyyy年MM月dd日"),

	/*** yyyy.MM.dd HH.mm */
	F("yyyy.MM.dd HH.mm"),

	/*** yyyyMMdd */
	F1("yyyyMMdd"),

	/*** yyyy */
	YE("yyyy"),
	/*** yyyy */
	YE1("yyyyMM");

	private final String value;

	private Locale locale;

	public String getValue() {
		return value;
	}

	public Locale getLocale() {
		return locale;
	}

	DateType(String value) {
		this.value = value;
	}

	DateType(String value, Locale locale) {
		this.value = value;
		this.locale = locale;
	}

}
