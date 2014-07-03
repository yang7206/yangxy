package com.util.encypt.algor;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加解密
 * @author yxy
 *
 */
public class AES {

	public static byte[] encrypt(String iv, String key, byte[] buffer) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
	 {
	    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
	    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	    byte[] result = cipher.doFinal(buffer);
	    return result;
	 }

	  public static byte[] decrypt(String iv, String key, byte[] buffer) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	  {
	    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
	    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	    byte[] result = cipher.doFinal(buffer);
	    return result;
	  }
}
