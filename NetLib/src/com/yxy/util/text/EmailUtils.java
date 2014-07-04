package com.yxy.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 邮箱相关工具
 * @author yxy
 *
 */
public class EmailUtils {
	
	/**
	 * 根据邮件地址返回登录地址
	 * @param mailAddress
	 * @return
	 */
	public static String getLoginAddressByMailAddress(String mailAddress) {
		if(TextUtils.isEmpty(mailAddress) || 
				mailAddress.indexOf("@") == -1) {
			return null;
		}
		String at = mailAddress.substring(mailAddress.lastIndexOf("@"), mailAddress.length());
		String loginAddress;
		if(at.equals("@qq.com") || at.equals("@vip.qq.com") || at.equals("@foxmail.com")) {
			loginAddress = "https://w.mail.qq.com/";
		}else if(at.equals("@163.com")) {
			loginAddress = "http://twebmail.mail.163.com/";
		}else if(at.equals("@126.com")) {
			loginAddress = "http://smart.mail.126.com/";
		}else if(at.equals("@sina.com") || at.equals("@vip.com.cn")) {
			loginAddress = "http://mail.sina.com.cn/";
		}else if(at.equals("@sohu.com")) {
			loginAddress = "http://m.mail.sohu.com/";
		}else if(at.equals("@gmail.com")) {
			loginAddress = "https://accounts.google.com/ServiceLogin?service=mail";
		}else if(at.equals("@yeah.net")) {
			loginAddress = "http://mail.sina.com.cn/";
		}else if(at.equals("@vip.163.com")) {
			loginAddress = "http://vip.163.com/";
		}else if(at.equals("@vip.126.com")) {
			loginAddress = "http://vip.126.com/";
		}else if(at.equals("@188.com")) {
			loginAddress = "http://www.188.com/";
		}else if(at.equals("@139.com")) {
			loginAddress = "http://html5.mail.10086.cn/";
		}else {
			loginAddress = null;
		}
		return loginAddress;
	}
	
	/**
	 * 将邮箱地址中不正规的@替换成正规的@
	 * @param emailStr
	 * @return
	 */
	public static String customReplaceAt(String emailStr){
		if(emailStr == null) return "";
		StringBuffer emailBuffer = new StringBuffer();
		for (int i = 0; i < emailStr.length(); i++) {
			char ch = emailStr.charAt(i);
			int v = ch;
			if(v == 65312){//﹫
				emailBuffer.append("@");
				continue;
			}
			emailBuffer.append(ch);
		}
		return emailBuffer.toString();
	}
	
	/**
	 * 是否是邮箱格式
	 * @param account
	 * @return
	 */
	public static boolean isEmailFormat(String account) {
		Pattern pat = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		Matcher mat = pat.matcher(account);
		return mat.matches();
	}
	
}
