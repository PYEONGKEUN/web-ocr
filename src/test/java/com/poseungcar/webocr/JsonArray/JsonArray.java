package com.poseungcar.webocr.JsonArray;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.poseungcar.webocr.DTO.OCR;
import com.poseungcar.webocr.DTO.OCR.OCRBuilder;
import com.poseungcar.webocr.config.DataSourceConfig;
import com.poseungcar.webocr.util.TimeLib;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {DataSourceConfig.class})
public class JsonArray {

	private static final Logger logger = LoggerFactory.getLogger(JsonArray.class);
	


	@Test
	public void selectTest() throws Exception {

	}




}
