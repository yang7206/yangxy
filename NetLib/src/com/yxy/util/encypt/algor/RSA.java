package com.yxy.util.encypt.algor;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * RSA锟接斤拷锟斤拷锟姐法
 * @author Administrator
 *
 */
public class RSA {
 
	private static String ALGORITHM = "RSA/ECB/PKCS1Padding";
	
	private static String ALGORITHM_1 = "RSA";
	
	private static String charset = "utf-8";
	 

	/**
	 * 锟斤拷锟絩sa钥锟阶讹拷
	 * @return
	 */
	public static KeyPair generateKeyPair(){
		KeyPairGenerator keyPairGennerator;
		try {
			keyPairGennerator = KeyPairGenerator.getInstance(ALGORITHM_1);
			keyPairGennerator.initialize(512, new SecureRandom());
			KeyPair keyPair = keyPairGennerator.genKeyPair();
			return keyPair; 
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 锟斤拷晒锟皆?
	 * @param modulus 锟斤拷
	 * @param publicExponent
	 * @return
	 */
	public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent){   
		KeyFactory keyFac;
		try {
			keyFac = KeyFactory.getInstance(ALGORITHM_1);
			RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent)); 
			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null; 
	}
	
	
	/**
	 * 锟斤拷晒锟皆?
	 * @param modulusHex 16锟斤拷锟斤拷模
	 * @param publicExponentHex 16锟斤拷锟狡癸拷钥锟斤拷
	 * @return
	 */
	public static RSAPublicKey generateRSAPublicKeyHex(String modulusHex, String publicExponentHex){
		return generateRSAPublicKey((new BigInteger(modulusHex,16)).toByteArray(), (new BigInteger(publicExponentHex,16)).toByteArray());
	}
	
	
	/**
	 * 锟斤拷锟剿皆?
	 * @param modulus 锟斤拷
	 * @param privateExponent
	 * @return
	 */
	public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus,
			byte[] privateExponent) {
		KeyFactory keyFac = null;
		try {  
			keyFac = KeyFactory.getInstance(ALGORITHM_1);
			RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(
					new BigInteger(modulus), new BigInteger(privateExponent)); 
			return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null; 
	}  
	
	
	/**
	 * 锟斤拷锟剿皆?
	 * @param modulusHex 16锟斤拷锟斤拷模
	 * @param privateExponentHex 16锟斤拷锟斤拷私钥锟斤拷
	 * @return
	 */
	public static RSAPrivateKey generateRSAPrivateKey(String modulusHex, String privateExponentHex) {
		return generateRSAPrivateKey((new BigInteger(modulusHex,16)).toByteArray(), (new BigInteger(privateExponentHex,16)).toByteArray());
	}
	
	
	/**
	 * 锟斤拷锟斤拷
	 * @param key 锟斤拷钥
	 * @param data 锟斤拷锟斤拷锟斤拷锟斤拷
	 * @return
	 */
	public static byte[] encrypt(Key key, byte[] data) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int blockSize = cipher.getBlockSize();// 锟斤拷眉锟斤拷芸锟斤拷小锟斤拷锟界：锟斤拷锟斤拷前锟斤拷锟轿?28锟斤拷byte锟斤拷锟斤拷key_size=1024
													// 锟斤拷锟杰匡拷锟叫∥?27
													// byte,锟斤拷锟杰猴拷为128锟斤拷byte;锟斤拷斯锟斤拷锟?锟斤拷锟斤拷锟杰块，锟斤拷一锟斤拷127
													// byte锟节讹拷锟斤拷为1锟斤拷byte
			int outputSize = cipher.getOutputSize(data.length);// 锟斤拷眉锟斤拷芸锟斤拷锟杰猴拷锟斤拷小
			int leavedSize = data.length % blockSize;
			int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
					: data.length / blockSize;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (data.length - i * blockSize > 0) {
				if (data.length - i * blockSize > blockSize)
					cipher.doFinal(data, i * blockSize, blockSize, raw, i
							* outputSize);
				else
					cipher.doFinal(data, i * blockSize, data.length - i
							* blockSize, raw, i * outputSize);
				// 锟斤拷锟斤拷锟斤拷doUpdate锟斤拷锟斤拷锟斤拷锟斤拷锟矫ｏ拷锟介看源锟斤拷锟斤拷锟斤拷锟矫匡拷锟絛oUpdate锟斤拷没锟斤拷什么实锟绞讹拷锟斤拷锟斤拷锟剿帮拷byte[]锟脚碉拷ByteArrayOutputStream锟叫ｏ拷锟斤拷锟斤拷锟絛oFinal锟斤拷时锟斤拷沤锟斤拷锟斤拷械锟絙yte[]锟斤拷锟叫硷拷锟杰ｏ拷锟斤拷锟角碉拷锟剿达拷时锟斤拷锟杰匡拷锟叫★拷芸锟斤拷锟斤拷丫锟斤拷锟斤拷锟斤拷锟絆utputSize锟斤拷锟斤拷只锟斤拷锟斤拷dofinal锟斤拷锟斤拷锟斤拷

				i++;
			}
			return raw;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}   
	
	/**
	 * 锟斤拷锟斤拷
	 * @param key 锟斤拷钥
	 * @param str 锟斤拷锟斤拷锟斤拷锟斤拷
	 * @return
	 */
	public static byte[] encryptStr(Key key,String str) {
		try {
			byte[] data = str.getBytes(charset);
			return encrypt(key,data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
 
	
	
	
	/**
	 * 锟斤拷锟斤拷
	 * @param key 私钥
	 * @param raw 锟斤拷锟斤拷艿募锟斤拷锟斤拷锟斤拷
	 * @return
	 */
	public static byte[] decrypt(Key key, byte[] raw) { 		
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			int blockSize = cipher.getBlockSize();
			ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
			int j = 0; 
			while (raw.length - j * blockSize > 0) {
				bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
				j++;
			} 
			return bout.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;  
	}  
	
	/**
	 * 锟斤拷锟斤拷
	 * @param key 私钥
	 * @param raw 锟斤拷锟斤拷艿募锟斤拷锟斤拷锟斤拷
	 * @return
	 */
	public static String decryptStr(Key key, byte[] raw) { 	 
		try {
			byte[] data = decrypt(key, raw);
			return new String(data,charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getCharset() {
		return charset;
	}

	public static void setCharset(String charset) {
		RSA.charset = charset;
	}
	 
}
