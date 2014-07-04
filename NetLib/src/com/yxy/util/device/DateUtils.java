package com.yxy.util.device;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

/**
 * 日期工具类
 * @author yxy
 *
 */
public class DateUtils {
	
	/**
	 * 日期转文本
	 * @param date
	 * @param pattern yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String parseDate(Date date, String pattern) {
		if(TextUtils.isEmpty(pattern)) {
			return null;
		}
		DateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
		return format.format(date);
	}
	
	/**
	 * 文本转日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date parseString(String date, String pattern) {
		if(!TextUtils.isEmpty(pattern)) {
			DateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
			try {
				return format.parse(date);
			} catch (ParseException e) {}
		}
		return null;
	}

}
