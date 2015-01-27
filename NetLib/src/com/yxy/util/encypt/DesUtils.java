package com.yxy.util.encypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.yxy.util.encypt.algor.Base64;

public class DesUtils {
	public static final String DEF_CHARSET = "UTF-8";
	public static final String DES_PASSWORD = "ezonsport_watch_Intelligence";
	private static final String IV_PASSWORD = "01234567";

	public static String decodeDES(String encryptText) throws Exception {
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		DESedeKeySpec spec = new DESedeKeySpec(DES_PASSWORD.getBytes());
		Key deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede/CBC/PKCS7Padding");
		IvParameterSpec ips = new IvParameterSpec(IV_PASSWORD.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
		return new String(decryptData, DEF_CHARSET);
	}
}
