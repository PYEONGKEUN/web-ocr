package com.poseungcar.webocr.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@Builder
//No constructor found in com.poseungcar.webocr.DTO.OCR matching [java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp, java.lang.String, java.lang.String]
// 두개가 같이 있어야 됨
@NoArgsConstructor
@AllArgsConstructor
//두개가 같이 있어야 mybatis 오류 안남
public class OCR {

	private Integer ocr_no;
	private String usr_id;
	private String ocr_fileName;
	private String ocr_filePath;
	private String ocr_datetime;
	private String ocr_ocrResult;//mariaDB 내부에서는 LONGTEXT 형식으로 저장됨 불러와서 Json형식으로 바꾸어야함
	private String ocr_hash;
	
}
