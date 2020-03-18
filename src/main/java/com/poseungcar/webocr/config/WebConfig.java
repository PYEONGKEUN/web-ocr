package com.poseungcar.webocr.config;

import javax.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer{

	//private static int MAX_FILE_ZIZE = 10 * 1024 * 1024;
	
	
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {RootConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] {ServletConfig.class};
	}


	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}
	
	
	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		
		//UTF-8 인코딩은 server.xml와  web.xml에서도 설정해주어야 함
		// 그래야 uri를 통한 UTF-8 요청도 문제없다.
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);	
		
		return new Filter[] {filter};
	}

	
	
}
