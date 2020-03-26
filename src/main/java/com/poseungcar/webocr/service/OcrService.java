package com.poseungcar.webocr.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface OcrService {

	
	public boolean detectText(String id, String fileName,String filePath,String fileHash)  throws FileNotFoundException, IOException;
	public String getVoucherNum(String id, String fileName,String filePath);
}
