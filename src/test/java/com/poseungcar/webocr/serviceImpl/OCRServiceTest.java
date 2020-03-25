package com.poseungcar.webocr.serviceImpl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.poseungcar.webocr.config.RootConfig;
import com.poseungcar.webocr.service.OcrService;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {RootConfig.class})
public class OCRServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(OCRServiceTest.class);

	@Autowired
	private OcrService ocrService;


	// sql 동작은 모두 로그에 기록되기때문에 하지 않아도 됨   
	// DB 서버 연결부터 Ibatis 매핑 테스트
	@Test
	public void test() throws Exception {
		String filePath = "D:\\\\Windows\\\\Documents\\\\GitLab\\\\web-ocr\\\\src\\\\main\\\\webapp\\\\resources\\\\SCAN_20200225_081534829.jpg";
		//ocrService.detectText(filePath);



	}


	@Test
	public void test2() throws Exception {
			
	}



}
