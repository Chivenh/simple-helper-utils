package com.fhtiger.helper.utils.web;

import com.fhtiger.helper.utils.SpecialUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * 加载图片
 * 
 * @author Chivenh
 * @version 1.0.0
 * @since 2020-04-26 15:14
 */
@SuppressWarnings({ "unused" })
public final class LoadImageUtil {
	private static  final   Logger logger = LoggerFactory.getLogger(LoadImageUtil.class);

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
			logger.warn("File Not Found!The Path is required!");
			return;
		}
		try (FileInputStream img = new FileInputStream(path);
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
			logger.warn(e.getMessage());
		}
	}

	private static final String PATH = "path";
	private static final String BASEPATH = "basePath";
	private static final String TYPE = "type";
	private static final String REAL_NAME = "realName";
	private static final String DOWNLOAD = "download";

	public static void loadImgByFileAttrs(Map<String, String> attrs,@NotNull  HttpServletRequest request,
			@NotNull HttpServletResponse response) {
		if (attrs == null || attrs.isEmpty()) {
			logger.warn("File Not Found!");
			try {
				response.getWriter().write("Load image has got a file-not-found error!");
			} catch (Exception e) {
				/**/}
			return;
		}
		String path = attrs.get(PATH);
		String basePath = attrs.get(BASEPATH);
		String type = attrs.get(TYPE);
		String realName = attrs.get(REAL_NAME);
		String dlString = SpecialUtil.getStr(attrs.get(DOWNLOAD));
		try (ServletOutputStream outputStream = response.getOutputStream();FileInputStream img = newImageInputStream(path, basePath)) {
			boolean downLoad = !SpecialUtil.isNull(dlString) && Boolean.parseBoolean(attrs.get(DOWNLOAD));
			response.setContentType("multipart/form-data");
			if (downLoad) {
				response.setContentType("application/octet-stream");
				UnaryOperator<String> nameGenerator = OutputNameGenerator.BROWSER.getGenerator(request);
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
			logger.warn(e.getMessage());
		}
	}

	private static FileInputStream newImageInputStream(String path, String basePath) throws FileNotFoundException {
		FileInputStream img;
		try {
			img = new FileInputStream(path);
		} catch (Exception e) {
			img = new FileInputStream(basePath);
		}
		return img;
	}
}
