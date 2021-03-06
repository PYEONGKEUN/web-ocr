package com.poseungcar.webocr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
//컨트롤러를 스캔하는 패키지 위치
@ComponentScan(basePackages = { "com.poseungcar.webocr","com.poseungcar.webocr.controller" })
@PropertySource({"classpath:profiles/${spring.profiles.active}/application.properties"})
public class ServletConfig implements WebMvcConfigurer {
	@Value("${imgs.location}")
	private String imgsLocation;
	@Value("${imgs.uri_path}")
	private String imgsUriPath;


	private final int MAX_SIZE = 20 * 1024 * 1024;

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		registry.viewResolver(viewResolver);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//mapping="/resources/**" locations=/resources/
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler(imgsUriPath+"/**").addResourceLocations("file://"+imgsLocation);

	}

	   @Bean
	   public MultipartResolver multipartResolver() {
	      CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	      multipartResolver.setMaxUploadSize(MAX_SIZE); // 10MB
	      multipartResolver.setMaxUploadSizePerFile(MAX_SIZE); // 10MB
	      multipartResolver.setMaxInMemorySize(0);
	      return multipartResolver;
	   }




}
