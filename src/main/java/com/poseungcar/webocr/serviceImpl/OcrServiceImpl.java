package com.poseungcar.webocr.serviceImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.poseungcar.webocr.service.OcrService;
import com.poseungcar.webocr.util.TimeLib;


@Service
public class OcrServiceImpl implements OcrService {

	private static final Logger logger = LoggerFactory.getLogger(OcrServiceImpl.class);

	@Autowired
	OcrDAO ocrDao;	

	@Override
	public boolean detectText(String id, String fileName,String filePath,String fileHash) throws FileNotFoundException, IOException{
		// TODO Auto-generated method stub
		logger.info("["+TimeLib.getCurrTime()+"] ---detectText Start---");		

		// 같은 해시값을 가진 파일을 찾음
		OCR findOCR =OCR.builder()
				.ocr_hash(fileHash)
				.build(); 
		
		List<OCR> findedocrs = ocrDao.select(findOCR,0,1);
		// 같은 해시값을 가진 파일지 존재한다면 OCR 결과를 가져와서 저장후 종료
		if(findedocrs.size() >= 1) {			
			
			OCR ocr = OCR.builder()
					.usr_id(id)
					.ocr_datetime(TimeLib.getCurrDateTime())
					.ocr_fileName(fileName)
					.ocr_filePath(filePath)
					.ocr_ocrResult(findedocrs.get(0).getOcr_ocrResult().toString())
					.ocr_hash(fileHash)
					.build();

			//logger.debug(ocr.toString());

			ocrDao.insert(ocr);
			logger.info("["+TimeLib.getCurrTime()+"] ---detectText End---");	
			return true;
		}
		logger.info("["+TimeLib.getCurrTime()+"] ---Google Vision API Start---");	
		//GOOGLE VISION API에 서비스를 요청하기 위한  객체
		List<AnnotateImageRequest> requests = new ArrayList<>();
		// 이미지 전송을 위해 이미지의 바이트를 읽어와서 구글 이미지 객체에 세팅
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
		Image img = Image.newBuilder().setContent(imgBytes).build();	
		//GOOGLE VISION API 요청 객체를 생성
		Feature feat = Feature.newBuilder().setTypeValue(Type.DOCUMENT_TEXT_DETECTION_VALUE).build();
		AnnotateImageRequest request =
				AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			//GOOGLE VISION API 요청
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			//GOOGLE VISION API 요청 결과를 가져옴
			List<AnnotateImageResponse> responses = response.getResponsesList();
			// 요청 결과 에러가 발생하지 않았다면 OCR인식 결과를 DB에 저장
			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {

					logger.error("["+TimeLib.getCurrTime()+"] Error: %s\n", res.getError().getMessage());
					return false;
				}
				logger.info("["+TimeLib.getCurrTime()+"] ---Google Vision API End---");
				
				Gson gson = new Gson();

				//변환 객체 -> Json String 
				String json = gson.toJson(res.getTextAnnotationsList());
				//logger.info(json);
				
				OCR ocr = OCR.builder()
						.usr_id(id)
						.ocr_datetime(TimeLib.getCurrDateTime())
						.ocr_fileName(fileName)
						.ocr_filePath(filePath)
						.ocr_ocrResult(json)
						.ocr_hash(fileHash)
						.build();
				

				if(ocrDao.insert(ocr) == 1) {
					logger.info("["+TimeLib.getCurrTime()+"] "+ocr.getOcr_fileName() +" is saved as "+ocr.getOcr_filePath());
				}
				
			}
		}
		logger.info("["+TimeLib.getCurrTime()+"] ---detectText End---");		
		
		// AWS 비용 절감을 위해
		return true;
	}


	final int minX= 1900,MaxX = 2400;
	final int minY= 170,MaxY = 350;

	public String getVoucherNum(String id, String fileName,String filePath) {
		//번호를 찾을 대상 OCR
		logger.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum Start---");	
		OCR findOcr = OCR.builder()
				.usr_id(id)
				.ocr_fileName(fileName)
				.ocr_filePath(filePath)
				.build();

		//결과물을 담을 변수
		String detectedVoucherNum = "";

		List<OCR> ocrs = ocrDao.select(findOcr,0,1);
		
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
		detectedVoucherNum = detectedVoucherNum.replaceAll(" |", "");


		logger.info("["+TimeLib.getCurrTime()+"] detectedVoucherNum is "+detectedVoucherNum);	
		logger.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum End---");	
		return detectedVoucherNum;
	}
	
	public String getVoucherNumByRegEx(String id, String fileName,String filePath) {
		logger.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum Start---");	
		//번호를 찾을 대상 OCR
		OCR findOcr = OCR.builder()
				.usr_id(id)
				.ocr_fileName(fileName)
				.ocr_filePath(filePath)
				.build();

		//결과물을 담을 변수
		String detectedVoucherNum = "";

		List<OCR> ocrs = ocrDao.select(findOcr,0,1);
		
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
		String allTxt = annotations.get(0).getDescription();
		
		// 공백 및 줄바꿈 제거후 정규식으로 증표번호 탐색
		allTxt = allTxt.replaceAll(" ", "");
		allTxt = allTxt.replaceAll("(\r\n|\r|\n|\n\r)", "");
		Pattern  regExPattern = Pattern.compile("[0-9]{1}-[0-9]{6}-[0-9]{5}");
		Matcher m = regExPattern.matcher(allTxt);
		
		if(m.find())
        {
			detectedVoucherNum = m.group();
        }
        else
        {	
        	//못찾는다면 null을 리턴
        	detectedVoucherNum = getVoucherNum(id,fileName,filePath);
        }
		logger.info("["+TimeLib.getCurrTime()+"] detectedVoucherNum is "+detectedVoucherNum);	
		logger.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum End---");	
		return detectedVoucherNum;
	}



}
