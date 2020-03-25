package com.poseungcar.webocr.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface OcrService {

	
	public Map<String, Object> detectText(String id, String fileName,String filePath)  throws FileNotFoundException, IOException;
	public String getVoucherNum(String id, String fileName,String filePath);
}
