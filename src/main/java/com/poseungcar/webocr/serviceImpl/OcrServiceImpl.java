package com.poseungcar.webocr.serviceImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;
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
import com.poseungcar.webocr.config.StaticValues;
import com.poseungcar.webocr.service.OcrService;
import com.poseungcar.webocr.util.TimeLib;

@Log4j2
@Service
public class OcrServiceImpl implements OcrService {


	@Autowired
	OcrDAO ocrDao;	

	@Override
	public boolean detectText(String id, String fileName,String filePath,String fileHash) throws FileNotFoundException, IOException{
		// TODO Auto-generated method stub
	log.info("["+TimeLib.getCurrTime()+"] ---detectText Start---");

//		// 같은 해시값을 가진 파일을 찾음
//		OCR findOCR =OCR.builder()
//				.ocr_hash(fileHash)
//				.build(); 
//		
//		List<OCR> findedocrs = ocrDao.select(findOCR,0,1);
//		// 같은 해시값을 가진 파일지 존재한다면 OCR 결과를 가져와서 저장후 종료
//		if(findedocrs.size() >= 1) {			
//			
//			OCR ocr = OCR.builder()
//					.usr_id(id)
//					.ocr_datetime(TimeLib.getCurrDateTime())
//					.ocr_fileName(fileName)
//					.ocr_filePath(filePath)
//					.ocr_ocrResult(findedocrs.get(0).getOcr_ocrResult().toString())
//					.ocr_hash(fileHash)
//					.build();
//
//			/log.debug(ocr.toString());
//
//			ocrDao.insert(ocr);
//		log.info("["+TimeLib.getCurrTime()+"] ---detectText End---");
//			return true;
//		}
		
		
		log.info("["+TimeLib.getCurrTime()+"] ---Google Vision API Start---");
		//GOOGLE VISION API에 서비스를 요청하기 위한  객체
		List<AnnotateImageRequest> requests = new ArrayList<>();
		// 이미지 전송을 위해 이미지의 바이트를 읽어와서 구글 이미지 객체에 세팅
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
		Image img = Image.newBuilder().setContent(imgBytes).build();	
		//GOOGLE VISION API 요청 객체를 생성
		Feature feat = Feature.newBuilder().setTypeValue(Type.DOCUMENT_TEXT_DETECTION_VALUE).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			//GOOGLE VISION API 요청
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			//GOOGLE VISION API 요청 결과를 가져옴
			List<AnnotateImageResponse> responses = response.getResponsesList();
			// 요청 결과 에러가 발생하지 않았다면 OCR인식 결과를 DB에 저장
			OCR ocr = null;
			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					log.info("["+TimeLib.getCurrTime()+"] Error: %s\n", res.getError().getMessage());
					ocr = OCR.builder()
							.usr_id("default")
							.ocr_datetime(TimeLib.getCurrDateTime())
							.ocr_fileName(fileName)
							.ocr_filePath(filePath)
							.ocr_ocrResult("FFFFFFFFFF")
							.ocr_hash(fileHash)
							.build();
				}else{
					log.info("["+TimeLib.getCurrTime()+"] ---Google Vision API End---");

					Gson gson = new Gson();

					//변환 객체 -> Json String
					// 만약 응답이 [] 일경우 처리
					String json = null;
					if(res.getTextAnnotationsList().size() > 0) {
						json = gson.toJson(res.getTextAnnotationsList());
					}else{
						json = "FFFFFFFFFF";
					}

					log.info(json);

					ocr = OCR.builder()
							.usr_id(id)
							.ocr_datetime(TimeLib.getCurrDateTime())
							.ocr_fileName(fileName)
							.ocr_filePath(filePath)
							.ocr_ocrResult(json)
							.ocr_hash(fileHash)
							.build();

					if(ocrDao.insert(ocr) == 1) {
						log.info("["+TimeLib.getCurrTime()+"] "+ocr.getOcr_fileName() +" is saved as "+ocr.getOcr_filePath());
					}
				}
			}
		}
		catch (Exception e){
			log.info(e.getStackTrace());
		}
		log.info("["+TimeLib.getCurrTime()+"] ---detectText End---");
		
		// AWS 비용 절감을 위해
		return true;
	}

	@Override
	public boolean detectTextBatch(List<Map<String, Object>> uploadResultList) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		log.info("["+TimeLib.getCurrTime()+"] ---detectTextBatch Start---");

		log.info("["+TimeLib.getCurrTime()+"] ---Google Vision API Start---");
		//GOOGLE VISION API에 서비스를 요청하기 위한  객체
		List<AnnotateImageRequest> requests = new ArrayList<>();

		for (Map<String, Object> uploadResultItem: uploadResultList) {
			// 이미지 전송을 위해 이미지의 바이트를 읽어와서 구글 이미지 객체에 세팅
			ByteString imgBytes = ByteString.readFrom(new FileInputStream(uploadResultItem.get("filePath").toString()));
			Image img = Image.newBuilder().setContent(imgBytes).build();
			//GOOGLE VISION API 요청 객체를 생성
			Feature feat = Feature.newBuilder().setTypeValue(Type.DOCUMENT_TEXT_DETECTION_VALUE).build();
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
			requests.add(request);

		}

//		uploadResultItem.get("fileName").toString();
//
//		uploadResultItem.get("fileHash").toString();


		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			//GOOGLE VISION API 요청
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			//GOOGLE VISION API 요청 결과를 가져옴
			List<AnnotateImageResponse> responses = response.getResponsesList();
			// 요청 결과 에러가 발생하지 않았다면 OCR인식 결과를 DB에 저장


			int resLen = responses.size();
			AnnotateImageResponse res = null;
			OCR ocr = null;
			List<OCR> ocrList = new ArrayList<OCR>();

			for(int i = 0; i < resLen; i++){
				res = responses.get(i);

				if (res.hasError()) {
					log.info("["+TimeLib.getCurrTime()+"] Error: %s\n", res.getError().getMessage());
					ocr = OCR.builder()
							.usr_id("default")
							.ocr_datetime(TimeLib.getCurrDateTime())
							.ocr_fileName(uploadResultList.get(i).get("fileName").toString())
							.ocr_filePath(uploadResultList.get(i).get("filePath").toString())
							.ocr_ocrResult("FFFFFFFFFF")
							.ocr_hash(uploadResultList.get(i).get("fileHash").toString())
							.build();
				}
				else{
					//log.info("["+TimeLib.getCurrTime()+"] ---Google Vision API End---");

					Gson gson = new Gson();

					//변환 객체 -> Json String
					// 만약 응답이 [] 일경우 처리
					String json = null;
					if(res.getTextAnnotationsList().size() > 0) {
						json = gson.toJson(res.getTextAnnotationsList());
					}else{
						json = "FFFFFFFFFF";
					}


					log.info(json);
					// ocrList에 ocr 객체들을 하나씩 추가
					ocr = OCR.builder()
							.usr_id("default")
							.ocr_datetime(TimeLib.getCurrDateTime())
							.ocr_fileName(uploadResultList.get(i).get("fileName").toString())
							.ocr_filePath(uploadResultList.get(i).get("filePath").toString())
							.ocr_ocrResult(json)
							.ocr_hash(uploadResultList.get(i).get("fileHash").toString())
							.build();
					ocrList.add(ocr);
				}
			}
			if(ocrDao.bulkInsert(ocrList) >= 1) {
				log.info("sucess");
			}else{
				log.info("error");
			}
		}
		catch (Exception e){
			log.info(e.getStackTrace());
		}
		log.info("["+TimeLib.getCurrTime()+"] ---detectTextBatch End---");

		return true;
	}


	final int minX= 1900,MaxX = 2400;
	final int minY= 170,MaxY = 350;

	public String getVoucherNum(String id, String fileName,String filePath) {
		//번호를 찾을 대상 OCR
	log.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum Start---");
		OCR findOcr = OCR.builder()
				.usr_id(id)
				.ocr_fileName(fileName)
				.ocr_filePath(filePath)
				.build();

		//결과물을 담을 변수
		String detectedVoucherNum = "";

		List<OCR> ocrs = ocrDao.select(findOcr);
		
		if(ocrs.size() == 0) {
		log.info("OCR result is not found.");
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
				//				log.info("--------------"+n++);
				//				log.info(annotation.getDescription());
				//				log.info(annotation.getBoundingPoly().toString());
				detectedVoucherNum+=annotation.getDescription();
			}
		}
		//필요없는 문자열 처리		
		detectedVoucherNum = detectedVoucherNum.replaceAll(" |", "");


		log.info("["+TimeLib.getCurrTime()+"] detectedVoucherNum is "+detectedVoucherNum);
		log.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum End---");
		return detectedVoucherNum;
	}
	
	public String getVoucherNumByRegEx(String id, String fileName,String filePath) {
		log.info("["+TimeLib.getCurrTime()+"] ---getVoucherNumByRegEx Start---");
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
		log.info("OCR result is not found.");
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
		log.info(allTxt);

		Pattern  regExPattern = Pattern.compile(StaticValues.REGEX_PATTERN_VOUCHER_NUM);
		Matcher m = regExPattern.matcher(allTxt);
		
		if(m.find())
        {
			detectedVoucherNum = m.group();
        }
        else
        {	
        	//못찾는다면 위치기반으로 탐색
        	detectedVoucherNum = getVoucherNum(id,fileName,filePath);
        }
	log.info("["+TimeLib.getCurrTime()+"] detectedVoucherNum is "+detectedVoucherNum);
	log.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum End---");
		return detectedVoucherNum;
	}

	@Override
	public String getBigVoucherNumByRegEx(String id, String fileName, String filePath) {
		log.info("["+TimeLib.getCurrTime()+"] ---getBigVoucherNumByRegEx Start---");
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
		log.info("OCR result is not found.");
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
		log.info(allTxt);

		Pattern  regExPattern = Pattern.compile(StaticValues.REGEX_PATTERN_BIG_VOUCHER_NUM);
		Matcher m = regExPattern.matcher(allTxt);
		if(m.find())
		{
			StringBuffer origin = new StringBuffer(m.group());
			detectedVoucherNum = origin.insert(5,"_").toString();
		}
		else
		{
			//못찾는다면 위치기반으로 탐색
			detectedVoucherNum = "FFFFF_FFFFF";
		}
		log.info("["+TimeLib.getCurrTime()+"] detectedVoucherNum is "+detectedVoucherNum);
		log.info("["+TimeLib.getCurrTime()+"] ---getVoucherNum End---");
		return detectedVoucherNum;
	}




}
