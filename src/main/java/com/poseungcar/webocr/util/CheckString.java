package com.poseungcar.webocr.util;

import java.util.regex.Pattern;

public class CheckString {

	public static boolean isNumber(String word) {		
		return Pattern.matches("^[0-9]*$", word);		
	}
	public static boolean isAlphabet(String word) {		
		return Pattern.matches("^[a-zA-Z]*$", word);		
	}
}
