package com.fhtiger.helper.utils.servletx;


import com.fhtiger.helper.utils.SpecialUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * 加载图片
 * 
 * @author Chivenh
 * @version 1.0.0
 * @since 2020-04-26 15:14
 */
@SuppressWarnings({ "unused" })
public final class LoadImageUtil {

	private LoadImageUtil()  throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	/**
	 * 根据路径返回图像到response.
	 * 
	 * @author Chivenh
	 * @since 2017年9月22日 上午8:45:55
	 * @param response 响应对象
	 * @param path 文件路径
	 */
	public static void loadImageByPath(@NotNull HttpServletResponse response,@NotNull String path) {
		if (response == null || SpecialUtil.isNull(path)) {
			SpecialUtil.consoleErr("File Not Found!The Path is required!");
			return;
		}
		try (FileInputStream img = new FileInputStream(new File(path));
				ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType("multipart/form-data");
			/*
			 * 设置以下两个自属性,可以下载图片. \
			 * response.setContentType("application/octet-stream"); \
			 * response.setHeader("Content-Disposition", \
			 * "attachment;filename=\"" + new String("图片.png".getBytes(), "iso-8859-1") + "\""); \
			 */
			int len;
			byte[] buf = new byte[1024 * 10];
			while ((len = img.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();

		} catch (IOException e) {
			SpecialUtil.consoleErr(e.getMessage());
		}
	}

	private static final String PATH = "path";
	private static final String BASEPATH = "basePath";
	private static final String TYPE = "type";
	private static final String REALNAME = "realName";
	private static final String DOWNLOAD = "download";

	public static void loadImgByFileAttrs(Map<String, String> attrs,@NotNull  HttpServletRequest request,
			@NotNull HttpServletResponse response) {
		if (attrs == null || attrs.isEmpty()) {
			SpecialUtil.consoleErr("File Not Found!");
			try {
				response.getWriter().write("Load image has got a file-not-found error!");
			} catch (Exception e) {
				/**/}
			return;
		}
		FileInputStream img;
		String path = attrs.get(PATH);
		String basePath = attrs.get(BASEPATH);
		String type = attrs.get(TYPE);
		String realName = attrs.get(REALNAME);
		String dlString = SpecialUtil.getStr(attrs.get(DOWNLOAD));
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			boolean downLoad = SpecialUtil.isNull(dlString) ? false : Boolean.valueOf(attrs.get(DOWNLOAD));
			try {
				img = new FileInputStream(new File(path));
			} catch (Exception e) {
				img = new FileInputStream(basePath);
			}
			response.setContentType("multipart/form-data");
			if (downLoad) {
				response.setContentType("application/octet-stream");
				Function<String, String> nameGenerator = OutputNameGenerator.BROWSER.getGenerator(request);
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + nameGenerator.apply(realName) + "." + type + "\"");
			}
			/*
			 * 设置以下两个自属性,可以下载图片. \
			 * response.setContentType("application/octet-stream"); \
			 * response.setHeader("Content-Disposition", \
			 * "attachment;filename=\"" + new String("图片.png".getBytes(), "iso-8859-1") + "\""); \
			 */
			int len;
			byte[] buf = new byte[1024 * 10];
			while ((len = img.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();
		} catch (Exception e) {
			SpecialUtil.consoleErr(e.getMessage());
		}
	}
}
