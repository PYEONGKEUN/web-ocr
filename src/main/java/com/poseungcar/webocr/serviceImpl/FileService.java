


 package com.poseungcar.webocr.serviceImpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.poseungcar.webocr.DAO.OcrDAO;
import com.poseungcar.webocr.DTO.OCR;
import com.poseungcar.webocr.service.IFileService;
import com.poseungcar.webocr.service.OcrService;
import com.poseungcar.webocr.util.FileHash;
import com.poseungcar.webocr.util.ImageTools;
import com.poseungcar.webocr.util.TimeLib;


@Service
@PropertySource({"classpath:profiles/${spring.profiles.active}/application.properties"})
public class FileService implements IFileService{

	private static final Logger logger = LoggerFactory.getLogger(FileService.class);


	@Value("${imgs.location}")
	private String uploadsLocation;
	@Value("${imgs.uri_path}")
	private String uploadsUriPath;
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
	
	
	final int IMG_WIDTH = 2478, IMG_HEIGHT = 1746; 



	@Override
	public Map<String, Object> imgUpload(		
			MultipartFile file, 
			Model model, 
			HttpSession session,
			HttpServletRequest request,
			HttpServletResponse response) throws IllegalStateException, IOException{
		// TODO Auto-generated method stub

		
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("["+TimeLib.getCurrTime()+"] File uploaded loading.");


		//REAL_PATH : /opt/webocr/imgs
		logger.info("REAL_PATH : " + uploadsLocation);
		String userId="default";
//		if(activeProfile.equals("dev")) {
//			userId = "test";
//		}else {
//			userId = session.getAttribute("memberInfo").toString();
//		}
		
		

		// 업로드 폴더 생성
		File dir = new File(uploadsLocation,userId);		
		if(dir.exists()) {
			logger.info(dir.getPath()+ " is Exist");
		}else {
			// 	폴더 생성
			dir.mkdirs();		
			logger.info(dir.getPath()+" is not Exist so create dir");
		}

		// Save mediaFile on system
		if (!file.getOriginalFilename().isEmpty()) {
			// image/jpeg 에서 jpeg만 가져오는 작업
			String fileType = file.getContentType().substring(file.getContentType().indexOf("/")+1);
			//파일저장
			
			
			
			
			File saveFilePath = new File(dir.getPath(), UUID.randomUUID().toString()+"."+fileType);
			
			BufferedImage bfResizedImg = ImageTools.resize(file.getInputStream(), IMG_WIDTH, IMG_HEIGHT);
			ImageIO.write(bfResizedImg,fileType, saveFilePath);
			//file.transferTo(saveFilePath);
			
			model.addAttribute("msg", "이미지 업로드 완료");

			logger.info("["+TimeLib.getCurrTime()+"] File uploaded successfully.");


//			//사이트의 순수한 주소
//			String host = request.getRequestURL().toString().replace(request.getRequestURI(),"");
//			// 해당 프로젝트의 url
//			String getContextPath = request.getContextPath();
//			//전체 URL
//			String fileURL = host + getContextPath+uploadsUriPath +"/"+userId+"/"+file.getOriginalFilename();
//			logger.info("return : "+fileURL);
			   

			result.put("fileName", file.getOriginalFilename());
			result.put("filePath", saveFilePath.getPath());
			result.put("fileHash", FileHash.sha(saveFilePath.getPath()));
			return result;
			
		} else {
			model.addAttribute("msg", "이미지 파일을 선택해주세요.");
			logger.info("이미지 파일을 선택해주세요");
			return null;
		}
	}




	@Override
	public String getFilename(String id, String uuid) {
		// TODO Auto-generated method stub
		return null;
	}




}
