package com.poseungcar.webocr.serviceImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Vertex;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import com.poseungcar.webocr.DAO.OcrDAO;
import com.poseungcar.webocr.DTO.OCR;
import com.poseungcar.webocr.DTO.OCR.OCRBuilder;
import com.poseungcar.webocr.service.OcrService;
import com.poseungcar.webocr.util.TimeLib;


@Service
public class OcrServiceImpl implements OcrService {

	private static final Logger logger = LoggerFactory.getLogger(OcrServiceImpl.class);


	@Autowired
	OcrDAO ocrDao;	




	@Override
	public Map<String, Object> detectText(String id, String fileName,String filePath) throws FileNotFoundException, IOException{
		// TODO Auto-generated method stub
		logger.info("---detectText Start---");
		// 결과물을 받을 변수
		Map<String,Object> result = new HashMap<String, Object>();

		List<AnnotateImageRequest> requests = new ArrayList<>();
		//		try {
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

		Image img = Image.newBuilder().setContent(imgBytes).build();			
		Feature feat = Feature.newBuilder().setTypeValue(Type.DOCUMENT_TEXT_DETECTION_VALUE).build();
		AnnotateImageRequest request =
				AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();





			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {

					logger.error("Error: %s\n", res.getError().getMessage());
					return null;
				}

				Gson gson = new Gson();

				String json = gson.toJson(res.getTextAnnotationsList());
				//logger.info(json);

				OCR ocr = OCR.builder()
						.usr_id(id)
						.ocr_datetime(TimeLib.getCurrDateTime())
						.ocr_fileName(fileName)
						.ocr_filePath(filePath)
						.ocr_ocrResult(json)
						.build();

				//logger.debug(ocr.toString());

				ocrDao.insert(ocr);
			}
		}
		//		}catch(Exception e) {
		//			for(StackTraceElement el : e.getStackTrace()) {
		//				logger.error(el.toString());
		//			}			
		//		}
		//			
		//logger.info(result.toString());
		logger.info("---detectText End---");
		return result;
	}


	final int minX= 1900,MaxX = 2400;
	final int minY= 170,MaxY = 350;

	public String getVoucherNum(String id, String fileName,String filePath) {
		//번호를 찾을 대상 OCR
		OCR findOcr = OCR.builder()
				.usr_id(id)
				.ocr_fileName(fileName)
				.ocr_filePath(filePath)
				.build();

		//결과물을 담을 변수
		String detectedVoucherNum = "";

		List<OCR> ocrs = ocrDao.select(findOcr);

		
		if(ocrs.size() == 0) {
			logger.info("OCR result is not found.");
			return null;
		}
		
		//하나만 고르기때문에 가장 첫번째
		String json = ocrs.get(0).getOcr_ocrResult();		
		

		Gson gson = new Gson();	
		//json 형식의 String에서 List<EntityAnnotation>객체를 생성
		List<EntityAnnotation> annotations = gson.fromJson(json, new TypeToken<List<EntityAnnotation>>(){}.getType());

		//해당 엔티티어노테이션에 
		for (EntityAnnotation annotation : annotations) {
			//get BoundingPoly x,y 
			//강제로 rect 따기
			//결과물들의 좌표를 확인하여 범위 값에 들어가가는 OCR결과물들만 detectedVoucherNum에 추가
			boolean isInRect = true;
			for(Vertex vert: annotation.getBoundingPoly().getVerticesList()) {
				int thisX = vert.getX();
				int thisY = vert.getY();
				//해당 좌표가 범위에 안들어 있다면.
				if(!(thisX >= minX && thisX <= MaxX)) isInRect = false;
				if(!(thisY >= minY && thisY <= MaxY)) isInRect = false;
			}

			if(isInRect) {
				//					logger.info("--------------"+n++);
				//					logger.info(annotation.getDescription());
				//					logger.info(annotation.getBoundingPoly().toString());
				detectedVoucherNum+=annotation.getDescription();
			}

		}

		//필요없는 문자열 처리
		int removeIdx = detectedVoucherNum.indexOf('|');

		if( removeIdx != -1) {
			detectedVoucherNum = detectedVoucherNum.substring(removeIdx+1);
		}



		return detectedVoucherNum;
	}



}
