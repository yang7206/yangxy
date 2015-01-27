package com.yxy.util.compress;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class BleUtils {

	/******************* 工具方法 *************************/
	/**
	 * 字节数组转为0XFF格式String
	 * 
	 * @param value
	 * @return
	 */
	public static String byteArrayToHexString(byte[] value) {
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < value.length; i++) {
			int v = value[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 字节数组 转为string
	 * 
	 * @param value
	 * @param length
	 * @return
	 */
	public static String byteArrayToString(byte[] value, int length) {
		int max = Math.min(length, value.length);
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < max; i++) {
			if (value[i] <= 20) {
				String s = byteToHexString(value[i]);
				if (s.length() < 2) {
					stringBuilder.append("0");
				}
				stringBuilder.append(s);
			} else {
				stringBuilder.append((char) value[i]);
			}
		}
		return stringBuilder.toString();
	}
	
	public static String byteArrayToString(byte[] value,int start, int length) {
		int max = Math.min(length + start, value.length);
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = start; i < max; i++) {
			if (value[i] <= 20) {
				String s = byteToHexString(value[i]);
				if (s.length() < 2) {
					stringBuilder.append("0");
				}
				stringBuilder.append(s);
			} else {
				stringBuilder.append((char) value[i]);
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * byte转为16进值String
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte b) {
		return Integer.toHexString(byte2Int(b));
	}

	/**
	 * byte转为16进值int
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToHexInt(byte b) {
		if (b < 0) {
			return byte2Int(b);
		}
		return Integer.parseInt(Integer.toHexString(b), 16);
	}

	public static int byte2Int(byte b) {
		return b & 0xff;
	}

	/**
	 * 是否支持BLE
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isSupportBle(Context ctx) {
		return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	public static SimpleDateFormat getFormatter() {
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.CHINA);
		return format;
	}

	public static SimpleDateFormat getFormatter(String temp) {
		SimpleDateFormat format = new SimpleDateFormat(temp, Locale.CHINA);
		return format;
	}

	/**
	 * 获取一个20长度的byte数组
	 * 
	 * @return
	 */
	public static byte[] getByte() {
		byte[] b = new byte[20];
		for (int i = 0; i < b.length; i++) {
			b[i] = 0000;
		}
		return b;
	}

	/**
	 * int值 转为byte
	 * 
	 * @param i
	 * @return
	 */
	public static byte int2Byte(int i) {
		return (byte) ((byte) i & 0xff);
	}

	public static void putShortReverse(byte b[], short s, int index) {
		b[index + 0] = (byte) (s >> 8);
		b[index + 1] = (byte) (s >> 0);
	}

}
