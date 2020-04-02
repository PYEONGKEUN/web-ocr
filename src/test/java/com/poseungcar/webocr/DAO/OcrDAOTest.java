package com.poseungcar.webocr.DAO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Vertex;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poseungcar.webocr.DTO.OCR;
import com.poseungcar.webocr.DTO.OCR.OCRBuilder;
import com.poseungcar.webocr.config.RootConfig;
import com.poseungcar.webocr.util.TimeLib;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {RootConfig.class})
public class OcrDAOTest {

	private static final Logger logger = LoggerFactory.getLogger(OcrDAOTest.class);

	//강제로 rect 따기
	//결과물들의 좌표를 확인하여 범위 값에 들어가는지 확인
	final int minX= 1900,MaxX = 2400;
	final int minY= 170,MaxY = 350;

	@Autowired
	OcrDAO ocrDAO;
//	@Test
//	public void selectTest() throws Exception {
////		logger.info(OCR.builder().build().toString());
//		List<OCR> ocrs = ocrDAO.select(OCR.builder().build(),0,100);
//		for(OCR ocr : ocrs) {
//			String json = ocr.getOcr_ocrResult();
//			Gson gson = new Gson();		
//			List<EntityAnnotation> annotations = gson.fromJson(json, new TypeToken<List<EntityAnnotation>>(){}.getType());
//			//logger.info(annotations.toString());
//			int n = 0;
//
//
//			String detectedVoucherNum = "";
//			//해당 엔티티어노테이션에 
//			for (EntityAnnotation annotation : annotations) {
//				//get BoundingPoly x,y 
//				//강제로 rect 따기
//				//결과물들의 좌표를 확인하여 범위 값에 들어가는지 확인
//				boolean isInRect = true;
//
//				for(Vertex vert: annotation.getBoundingPoly().getVerticesList()) {
//					int thisX = vert.getX();
//					int thisY = vert.getY();
//					//해당 좌표가 범위에 안들어 있다면.
//					if(!(thisX >= minX && thisX <= MaxX)) isInRect = false;
//					if(!(thisY >= minY && thisY <= MaxY)) isInRect = false;
//				}
//				//			//결과물들의 좌표를 확인하여 범위 값에 들어가는지 확인결과
//				//들어있다면 로그로 출력하
//				if(isInRect) {
////					logger.info("--------------"+n++);
////					logger.info(annotation.getDescription());
////					logger.info(annotation.getBoundingPoly().toString());
//					detectedVoucherNum+=annotation.getDescription();
//				}
//
//			}
//			int removeIdx = detectedVoucherNum.indexOf('|');
//			
//			if( removeIdx != -1) {
//				detectedVoucherNum = detectedVoucherNum.substring(removeIdx+1);
//			}
//			
//			logger.info("영수증 번호는 "+detectedVoucherNum+"입니다.");
//		}
//	}
	@Test
	public void selectTest2() throws Exception {
//		logger.info(OCR.builder().build().toString());
		List<OCR> ocrs = ocrDAO.select(OCR.builder().build(),0,100);
		Gson gson = new Gson();	
		String detectedVoucherNum;
		String json;
		for(OCR ocr : ocrs) {
			json = ocr.getOcr_ocrResult();
			//logger.info(ocr.getOcr_filePath());
			//logger.info(ocr.getOcr_fileName());
			List<EntityAnnotation> annotations = gson.fromJson(json, new TypeToken<List<EntityAnnotation>>(){}.getType());
			//logger.info(annotations.toString());

			//해당 엔티티어노테이션에 
			String allTxt = annotations.get(0).getDescription();
			allTxt = allTxt.replaceAll(" ", "");
			allTxt = allTxt.replaceAll("(\r\n|\r|\n|\n\r)", "");
			//logger.info(allTxt);
			Pattern  regExPattern = Pattern.compile("[0-9]{1}-[0-9]{6}-[0-9]{5}");
			Matcher m = regExPattern.matcher(allTxt);
			
			if(m.find())
	        {
				detectedVoucherNum = m.group();
	        }
	        else
	        {
	        	detectedVoucherNum = null;
	        }
			
			logger.info("영수증 번호는 "+detectedVoucherNum+"입니다.\n----------------------------");
		}
	}
	
	
//	@Test
//	public void insertTest() throws Exception {
//		List<OCR> ocrs = ocrDAO.select(OCR.builder().build());
//
//	}




}
