package com.yxy.util.encypt.algor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 锟斤拷锟斤拷MD5锟斤拷希值
 * @author Administrator
 *
 */
public class Md5 {
 
	
	
	/**
	 * MD5锟斤拷锟斤拷锟较Ｖ?
	 * @param data
	 * @return 锟斤拷锟斤拷锟街斤拷锟斤拷锟斤拷
	 */
	public static byte[] getMD5Code(byte[] data){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(data, 0, data.length); 
			return md5.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
 
	
	
	/**
	 * MD5锟斤拷锟斤拷锟较Ｖ?
	 * @param data
	 * @return 锟斤拷锟斤拷16锟斤拷锟斤拷锟街凤拷
	 */
	public static String getMD5CodeHex(byte[] data){
		byte[] codeByte = getMD5Code(data);
		if(codeByte != null){
			return Hex.byte2hex(codeByte);
		}else{
			return null;
		}
	}
	
}
