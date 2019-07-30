package com.fhtiger.utils.helperutils.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 导出word的简单辅助类
 *
 * @author LFH
 * @since 2018年07月19日 17:19
 */
public class ExportWordDeal {

	private static Logger logger = LoggerFactory.getLogger(ExportWordDeal.class);

	private final String filePath;
	private final String file;
	private Map<String ,Object> data=new HashMap<>();
	private String name;
	private final Template ftl;

	/**
	 * 获取 Freemarker 模板配置
	 * @param filePath 配置文件路径
	 * @return {@link Configuration}
	 */
	private static Configuration configuration(String filePath){
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		try {
			//配置模板加载的目录
			cfg.setDirectoryForTemplateLoading(new File(filePath));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateUpdateDelayMilliseconds(0);
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		} catch (IOException e) {
			logger.error("error:{0}",e);
		}
		return cfg;
	}

	/**
	 * 获取要填充的模板
	 * @param filePath 模板路径
	 * @param file 模板文件名
	 * @return {@link Template}
	 */
	public static Template getTemplate(String filePath,String file) {
		try {
			return configuration(filePath).getTemplate(file);
		} catch (IOException e) {
			logger.error("error:{0}",e);
		}
		return null;
	}

	private ExportWordDeal(String filePath,String file) {
		this.filePath = filePath;
		this.file=file;
		this.ftl=getTemplate(this.filePath,this.file);
	}

	public static ExportWordDeal create(HttpServletRequest request,String dirPath,String fileName){
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		String filePath = rootPath.replaceAll("\\\\", "/") + dirPath;
		return new ExportWordDeal(filePath,fileName);
	}

	public ExportWordDeal setData(Map<String,Object> data){
		if(data!=null){
			for (String k : data.keySet()) {
				if(data.get(k)!=null){
					data.put(k, Tutil.getStr( data.get(k)).replaceAll("&","&amp;"));
				}
			}
		}
		this.data=data;
		return this;
	}

	public void out(HttpServletResponse response,String outName) {
		response.reset();
		response.setContentType("application/ms-word;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		try(Writer out = response.getWriter();) {
			if(!outName.endsWith(".doc")){
				outName+=".doc";
			}
			response.setHeader("Content-Disposition",
					"attachment;filename=\"" + new String(outName.getBytes("GB2312"), "ISO_8859_1") + "\"");
			this.ftl.process(this.data, out);
		}catch (IOException|TemplateException e){
			logger.error("error:{0}",e);
		}
	}
}
