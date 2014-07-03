package com.util.encypt.algor;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 3DES加解密
 * 
 * @author yxy
 *
 */
public class DESede { 

	private KeySpec keySpec;  
	
	private String algorithm = "DESede/CBC/PKCS7Padding";
	
	private SecretKey key;
	private SecretKeyFactory keyFactory; 
	
	private String charset = "utf-8";
	
	public DESede(){ 
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
			key = keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public DESede(byte[] keyByte){  
		initKey(keyByte);
	}
	
	public DESede(String keyStr){  
		byte[] keyByte;
		try {
			keyByte = keyStr.getBytes(charset);
			initKey(keyByte);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	private void initKey(byte[] keyByte){
		try { 
			keyFactory = SecretKeyFactory.getInstance("DESede"); 
			keySpec = new DESedeKeySpec(updateKey(keyByte)); 
			key = keyFactory.generateSecret(keySpec);  
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
	}
	
	
	private byte[] updateKey(byte[] keyByte){
		int lg = keyByte.length; 
		if(keyByte.length < 24){
			byte[] newKey = new byte[24];
			byte[] temp = new byte[24 - lg];
			for(int i = 0; i < 24 - lg; i++){
				temp[i]=0;
			}
			System.arraycopy(keyByte, 0, newKey, 0, lg);
			System.arraycopy(temp, 0, newKey, lg, 24-lg);
			keyByte = newKey;
		}
		return keyByte;
	}
	 
	/**
	 * 加密
	 * @param data 
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] data) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, Exception { 
		IvParameterSpec IVSpec = IvGenerator(key.getEncoded());
		Cipher c1 = Cipher.getInstance(algorithm);
		c1.init(Cipher.ENCRYPT_MODE, key,((java.security.spec.AlgorithmParameterSpec) (IVSpec)));
		return c1.doFinal(data);
	} 
	
	/**
	 * 加密字符串
	 * @param str
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws Exception
	 */
	public byte[] encryptStr(String str) throws InvalidKeyException,
			NoSuchPaddingException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, Exception {
		byte[] data = str.getBytes(charset);
		return encrypt(data);
	}
	
	 

	private static IvParameterSpec IvGenerator(byte[] b)  {
		byte[] defaultIV =new byte[8];
		System.arraycopy(b, 0, defaultIV, 0, 8);
        IvParameterSpec IV = new IvParameterSpec(defaultIV);
        return IV;
    }

	
	/**
	 * 解密
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data) throws Exception {
		IvParameterSpec IVSpec = IvGenerator(key.getEncoded());
		Cipher decryptCipher = Cipher.getInstance(algorithm);
		decryptCipher.init(Cipher.DECRYPT_MODE, key, ((java.security.spec.AlgorithmParameterSpec) (IVSpec)));
		return decryptCipher.doFinal(data);
	} 
	
	/**
	 * 解密字符串
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String decryptStr(byte[] data) throws Exception{
		byte[] strData = decrypt(data);
		return new String(strData,charset);
	}
	
	/**
	 * 获取KEY
	 * @return
	 */
	public byte[] getKey(){
		return key.getEncoded();
	}
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
