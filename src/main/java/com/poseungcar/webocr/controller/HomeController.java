package com.poseungcar.webocr.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.poseungcar.webocr.service.FileService;
import com.poseungcar.webocr.service.OcrService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles requests for the application home page.
 */
@Log4j2
@Controller
public class HomeController {

	@Autowired
	FileService fileService;

	@Autowired
	OcrService ocrService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );

		return "home";
	}
	// 브라우저 다운로드 링크
	@RequestMapping(value = "/links", method = RequestMethod.GET)
	public String links(Locale locale, Model model) {

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );

		return "links";
	}
	// 영수증에서 큰 번호 읽어오기
	@RequestMapping(value = "/big", method = RequestMethod.GET)
	public String detectBigNum(Locale locale, Model model) {

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );

		return "detectBigNum";
	}

	// 영수증에서 큰 번호 읽어오기 Batch
	@ResponseBody
	@RequestMapping(value="/bigbatch/pre", method=RequestMethod.POST, produces="text/plain;charset=utf-8")
	public String getBigNumBatchPre(
			HttpSession session,
			Model model,
			HttpServletRequest request,
			MultipartHttpServletRequest multiFiles,
			HttpServletResponse response) throws IOException {
		//MultipartFile는 자바스크립트로 File객체와 Bloc 객체를 받을수 있다.
		List<Map<String, Object>> uploadResultList = new ArrayList<Map<String, Object>>();
		// 파일 받기
		List<MultipartFile> fileList = multiFiles.getFiles("file");
		for (MultipartFile file: fileList ) {
			uploadResultList.add(fileService.imgUpload(file,model,session,request,response));
		}
		log.info(uploadResultList.toString());

		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("sheet1");
		//-- 구글 API 처리 및 파일 업로드
		for (Map<String, Object> uploadResultItem: uploadResultList) {
			ocrService.detectText(
					"default",
					uploadResultItem.get("fileName").toString(),
					uploadResultItem.get("filePath").toString(),
					uploadResultItem.get("fileHash").toString());
		}
		//--- getBigNUmberBatch
		List<String> bigNumList = new ArrayList<String>();
		for (Map<String, Object> uploadResultItem: uploadResultList) {
			bigNumList.add(ocrService.getBigVoucherNumByRegEx(
					"default",
					uploadResultItem.get("fileName").toString(),
					uploadResultItem.get("filePath").toString()));
		}
		//--- excel 파일

		log.info(bigNumList.toString());



		return "sucess";
	}
	@ResponseBody
	@RequestMapping(value="/bigbatch", method=RequestMethod.POST, produces = org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public String getBigNumBatch(
			HttpSession session,
			Model model,
			HttpServletRequest request,
			MultipartHttpServletRequest multiFiles,
			HttpServletResponse response) throws Exception{

		OutputStream os = response.getOutputStream();
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		try{

			//MultipartFile는 자바스크립트로 File객체와 Bloc 객체를 받을수 있다.
			List<Map<String, Object>> uploadResultList = new ArrayList<Map<String, Object>>();
			// 파일 받기
			List<MultipartFile> fileList = multiFiles.getFiles("file");
			for (MultipartFile file: fileList ) {
				uploadResultList.add(fileService.imgUpload(file,model,session,request,response));
			}
			log.info("uploadResultList : "+uploadResultList.toString());


			//-- 구글 API 처리 및 파일 업로드
			if(ocrService.detectTextBatch(uploadResultList) == false){
				throw new Exception("detectTextBatch failed");
			}


			//--- getBigNUmberBatch AND create

			Map<String, Object> uploadResultItem = null;
			List<String> bigNumList = new ArrayList<String>();
			int uploadResultListSize = uploadResultList.size();
			String tmpBigNum = null;


			log.info("uploadResultListSize : "+uploadResultListSize);

			// -- 헤더 이후 두번째 행부터 추가 (실질적인 데이터)
			for (int i = 0; i < uploadResultListSize; i++) {
				uploadResultItem = uploadResultList.get(i);
				log.info("uploadResultItem : "+uploadResultItem.toString());
				tmpBigNum = ocrService.getBigVoucherNumByRegEx(
						"default",
						uploadResultItem.get("fileName").toString(),
						uploadResultItem.get("filePath").toString());

				if(tmpBigNum == null) tmpBigNum = "FFFFF_FFFFF";
				log.info(tmpBigNum);
				// 파일 이름과 ext 구하기
				bigNumList.add(tmpBigNum);
			}

			log.info(bigNumList.toString());

			Gson gson = new Gson();
			if(bigNumList.size() > 0){
				return gson.toJson(bigNumList).toString();
			}else{
				return "";
			}

		}catch(Exception e) {
			log.error(e.getMessage());
			return "";
		}
	}
}
