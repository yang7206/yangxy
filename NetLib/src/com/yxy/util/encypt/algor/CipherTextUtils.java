package com.yxy.util.encypt.algor;


public class CipherTextUtils {
//	private static final String iv = "30212102dicudiab";
//	private static final String key = "30212102dicudiab";
	
	private static final String iv = "489230_guangshan";
	private static final String key = "489230_guangshan";
	
	public static String encrypt(String raw) {
		try {
			byte[] cipherBuffer = AES.encrypt(iv, key, raw.getBytes("utf-8"));
			return Hex.byte2hex(cipherBuffer);
		} catch (Exception e) {
			//
		}  
		return null;
	}
	
	public static String decrypt(String text) {
		byte[] buffer = Hex.hex2byte(text);
		try {
			byte[] raw = AES.decrypt(iv, key, buffer);
			return new String(raw, "utf-8");
		} catch (Exception e) {
			//
		}  
		return null;
	}
	
	public static byte[] encryptText(String raw) {
		try {
			byte[] cipherBuffer = AES.encrypt(iv, key, raw.getBytes("utf-8"));
			return cipherBuffer;
		} catch (Exception e) {
		} 
		return null;
	}
	
	public static String decryptText(byte[] buffer) {
		try {
			byte[] raw = AES.decrypt(iv, key, buffer);
			return new String(raw, "utf-8");
		} catch (Exception e) {
		}  
		return null;
	}
}
