package com.poseungcar.webocr.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;


public interface IUploadService{

	
	public String imgUpload(		
		MultipartFile file, 
		Model model, 
		HttpSession session,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException;
	
}
