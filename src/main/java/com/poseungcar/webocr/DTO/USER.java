package com.poseungcar.webocr.DTO;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
//회원 테이블 객체
public class USER {

	String id;
	String email;
	String password;
	String userDate;
}
