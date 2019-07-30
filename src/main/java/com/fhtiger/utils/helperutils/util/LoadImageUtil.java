package com.fhtiger.utils.helperutils.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 加载图片
 * 
 * @author LFH
 * @since 2017年9月22日
 * @version 1.0.0
 */
public class LoadImageUtil {

	private static Logger logger = LoggerFactory.getLogger(LoadImageUtil.class);

	/**
	 * 根据路径返回图像到response.
	 * 
	 * @author LFH
	 * @since 2017年9月22日 上午8:45:55
	 * @param response {@link HttpServletResponse}
	 * @param path 图片绝对路径
	 */
	public static void loadImageByPath(HttpServletResponse response, String path) {
		ServletOutputStream outputStream = null;
		FileInputStream img = null;
		if (response == null || Tutil.isNull(path)) {
			logger.error("File Not Found!The Path is required!");
			return;
		}
		try {
			img = new FileInputStream(new File(path));
			response.setContentType("multipart/form-data");
			/*
			 * 设置以下两个自属性,可以下载图片.
			 * response.setContentType("application/octet-stream");
			 * response.setHeader("Content-Disposition",
			 * "attachment;filename=\"" + new String("图片.png".getBytes(), "iso-8859-1") + "\"");
			 */
			outputStream = response.getOutputStream();
			int len = 0;
			byte[] buf = new byte[1024 * 10];
			while ((len = img.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();

		} catch (IOException e) {
			logger.error("error:{0}",e);
		} finally {
			try {
				if( outputStream!=null) outputStream.close();
				if(img!=null) img.close();
			} catch (Exception e2) {
				logger.error("error:{0}",e2);
			}
		}
	}

	private static final String PATH = "path";
	private static final String BASEPATH = "basePath";
	private static final String TYPE = "type";
	private static final String REALNAME = "realName";
	private static final String DOWNLOAD = "download";

	@SuppressWarnings("unused")
	public static void loadImgByFileAttrs(Map<String, String> attrs, HttpServletResponse response) {
		if (attrs == null || attrs.isEmpty()) {
			Tutil.consoleErr("File Not Found!");
			try {
				response.getWriter().write("Load image has got a file-not-found error!");
			} catch (Exception e) {

			}
			return;
		}
		ServletOutputStream outputStream = null;
		FileInputStream img = null;
		String path = attrs.get(PATH);
		String basePath = attrs.get(BASEPATH);
		String type = attrs.get(TYPE);
		String realName = attrs.get(REALNAME);
		String dlString = Tutil.getStr(attrs.get(DOWNLOAD));
		try {
			boolean downLoad = Tutil.isNull(dlString) ? false : Boolean.valueOf(attrs.get(DOWNLOAD));
			try {
				img = new FileInputStream(new File(path));
			} catch (Exception e) {
				img = new FileInputStream(basePath);
			}
			response.setContentType("multipart/form-data");
			if (img == null) {
				response.getWriter().print("Load Error!");
				return;
			}
			if (downLoad) {
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + new String((realName + "." + type).getBytes(), "iso-8859-1") + "\"");
			}
			/*
			 * 设置以下两个自属性,可以下载图片.
			 * response.setContentType("application/octet-stream");
			 * response.setHeader("Content-Disposition",
			 * "attachment;filename=\"" + new String("图片.png".getBytes(), "iso-8859-1") + "\"");
			 */
			outputStream = response.getOutputStream();
			int len = 0;
			byte[] buf = new byte[1024 * 10];
			while ((len = img.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();
		} catch (Exception e) {
			logger.error("error:{0}",e);
		} finally {
			try {
				if (outputStream != null) outputStream.close();
				if (img != null) img.close();
			} catch (Exception e2) {
				logger.error("error:{0}",e2);
			}
		}
	}
}
