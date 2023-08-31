package com.fhtiger.helper.utils.springweb;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WebContextLoaderListener
 * 增加对Environment参数的扩展追加
 * 使用 {@link WebEnvs} 注解来注入需要加载的属性文件列表
 * 项目中继承本类，并添加 {@link WebEnvs} 注解，之后替换 web.xml中的 `org.springframework.web.context.ContextLoaderListener`
 * @author Chivenh
 * @since 2020年12月01日 10:43
 */
@SuppressWarnings({ "unused" })
@WebEnvs
public abstract class AbstractWebContextLoaderListener extends ContextLoaderListener {

	private final YamlPropertiesFactoryBean yamlMapFactoryBean;

	/**
	 * 存放properties文件名
	 */
	private Set<String> envs ;

	public AbstractWebContextLoaderListener() {
		this.yamlMapFactoryBean = new YamlPropertiesFactoryBean();
		this.initLoad();
	}

	public AbstractWebContextLoaderListener(WebApplicationContext context) {
		super(context);
		this.yamlMapFactoryBean = new YamlPropertiesFactoryBean();
		this.initLoad();
	}

	/**
	 * 初始化获取需要加载的配置文件名列表
	 */
	private void initLoad(){
		WebEnvs webEnvs = AnnotatedElementUtils.findMergedAnnotation(this.getClass(),WebEnvs.class);
		if(webEnvs!=null){
			this.envs = new HashSet<>(Arrays.asList( webEnvs.value()));
		}
	}

	/**
	 * 加载配置文件
	 * @param resource -
	 * @return -
	 */
	private PropertySource<?> loadProp(Resource resource) {
		final Properties properties = new Properties();
		try {
			properties.load(resource.getInputStream());
			return new PropertiesPropertySource(resource.getFilename(), properties);
		} catch (IOException ex) {
			throw new IllegalStateException("load resource exception" + resource, ex);
		}
	}

	private PropertySource<?> loadYaml(List<Resource> resources){
		final Properties properties = new Properties();
		this.yamlMapFactoryBean.setResources(resources.toArray(new Resource[0]));
		Properties prop = this.yamlMapFactoryBean.getObject();
		if(prop!=null){
			properties.putAll(prop);
			prop.clear();
		}
		return new PropertiesPropertySource(resources.stream().map(Resource::getFilename).collect(Collectors.joining("|")), properties);
	}

	private void loadEnvs(ConfigurableEnvironment env){

		if(this.envs==null || this.envs.size()<1){
			return ;
		}

		/*遍历envs，加载配置文件*/
		List<Resource> yamlResources=new ArrayList<>();
		for (String prop : this.envs) {
			if(isProp(prop) || isYaml(prop)){
				Resource resource = new ClassPathResource(prop);
				if(resource.exists()){
					if(isYaml(prop)){
						yamlResources.add(resource);
					}else{
						PropertySource<?> propertySource = this.loadProp(resource);
						env.getPropertySources().addLast(propertySource);
					}
				}
			}
		}
		/*yaml文件最后加载*/
		if(yamlResources.size()>0){
			env.getPropertySources().addLast(this.loadYaml(yamlResources));
		}
	}

	private static boolean isProp(String name){
		return StringUtils.hasText(name) && (name.endsWith(".props") || name.endsWith(".prop") || name.endsWith(".properties"));
	}

	private static boolean isYaml(String name){
		return StringUtils.hasText(name) && (name.endsWith(".yaml") || name.endsWith(".yml"));
	}

	@Override
	protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
		String configLocationParam;
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			configLocationParam = sc.getInitParameter("contextId");
			if (configLocationParam != null) {
				wac.setId(configLocationParam);
			} else {
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(sc.getContextPath()));
			}
		}

		wac.setServletContext(sc);
		configLocationParam = sc.getInitParameter("contextConfigLocation");
		if (configLocationParam != null) {
			wac.setConfigLocation(configLocationParam);
		}

		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment)env).initPropertySources(sc, (ServletConfig)null);
		}

		/* 增加扩展env属性的追加*/
		this.loadEnvs(env);

		this.customizeContext(sc, wac);
		wac.refresh();
	}
}
