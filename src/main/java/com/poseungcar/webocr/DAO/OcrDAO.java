package com.poseungcar.webocr.DAO;

import java.util.List;

import com.poseungcar.webocr.DTO.OCR;

public interface OcrDAO {

	public List<OCR> select(OCR ocr);
	public int update(OCR ocr);
	public int insert(OCR ocr);
}
