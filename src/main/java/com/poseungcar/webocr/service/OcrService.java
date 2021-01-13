package com.poseungcar.webocr.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface OcrService {

	
	public boolean detectText(String id, String fileName,String filePath,String fileHash)  throws FileNotFoundException, IOException;
	public String getVoucherNum(String id, String fileName,String filePath);
	//영수증 스캔 파일에서 영수증 번
	public String getVoucherNumByRegEx(String id, String fileName,String filePath);
	//  영수중 사진에서 가장큰 글자
	public String getBigVoucherNumByRegEx(String id, String fileName,String filePath);
}
