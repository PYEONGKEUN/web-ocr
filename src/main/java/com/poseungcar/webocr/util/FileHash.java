package com.poseungcar.webocr.util;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileHash {
	public static String md(String filePath){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(filePath);

			byte[] dataBytes = new byte[1024];

			int nread = 0; 
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}


	}



	public static String sha(String filePath){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			FileInputStream fis = new FileInputStream(filePath);

			byte[] dataBytes = new byte[1024];

			int nread = 0; 
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			};
			byte[] mdbytes = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}


}
