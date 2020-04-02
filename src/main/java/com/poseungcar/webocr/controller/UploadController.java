package com.poseungcar.webocr.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poseungcar.webocr.service.IFileService;
import com.poseungcar.webocr.service.OcrService;


@Controller
@RestController
public class UploadController {


	@Autowired
	IFileService fileService;

	@Autowired
	OcrService ocrService;

	
	@ResponseBody
	@RequestMapping(value="/uploadimg.action", method=RequestMethod.POST, produces="text/plain;charset=utf-8")
	public String singleFileUpload(
			@RequestParam("mediaFile") MultipartFile file, 
			HttpSession session,
			Model model,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//MultipartFile는 자바스크립트로 File객체와 Bloc 객체를 받을수 있다.
		Map<String,Object> uploadResult;

		uploadResult= fileService.imgUpload(file, model, session, request, response);
		ocrService.detectText(
				"default", 
				uploadResult.get("fileName").toString(), 
				uploadResult.get("filePath").toString(),
				uploadResult.get("fileHash").toString());

		String result = ocrService.getVoucherNumByRegEx(
				"default", 
				uploadResult.get("fileName").toString(), 
				uploadResult.get("filePath").toString());


		if(result == null) {
			result = "error";
		}
		return result;

	}


}
