package com.poseungcar.webocr.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;


public interface FileService {

	
	public Map<String, Object> imgUpload(		
		MultipartFile file, 
		Model model, 
		HttpSession session,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException;

	public String getFilename(String id, String uuid);
	
}
