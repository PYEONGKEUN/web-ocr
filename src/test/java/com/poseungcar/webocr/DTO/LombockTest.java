package com.poseungcar.webocr.DTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.poseungcar.webocr.DTO.OCR.OCRBuilder;
import com.poseungcar.webocr.config.DataSourceConfig;
import com.poseungcar.webocr.util.TimeLib;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {DataSourceConfig.class})
public class LombockTest {

	private static final Logger logger = LoggerFactory.getLogger(LombockTest.class);
	

	@Test
	public void test() throws Exception {
		OCR ocr = new OCRBuilder()
				.ocr_datetime(TimeLib.getCurrDateTime())
				.ocr_fileName("")
				.ocr_filePath("")
				.ocr_hash("")
				.ocr_ocrResult("")
				.usr_id("skvudrms54")
				.build();
		System.out.println(ocr.toString());
		logger.debug(ocr.toString());
		
	}



}
