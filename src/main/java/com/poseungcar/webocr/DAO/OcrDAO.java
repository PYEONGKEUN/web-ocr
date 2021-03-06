package com.poseungcar.webocr.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.poseungcar.webocr.DTO.OCR;

public interface OcrDAO {

	public List<OCR> selectAll(OCR ocr);
	public List<OCR> select(@Param("ocr") OCR ocr);
	public int update(OCR ocr);
	public int insert(OCR ocr);
	public int bulkInsert(List<OCR> ocrList);
}
