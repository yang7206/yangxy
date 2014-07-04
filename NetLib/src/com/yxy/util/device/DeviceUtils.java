package com.yxy.util.device;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * 设备相关工具类
 * @author yxy
 *
 */
public class DeviceUtils {
	
	/**
	 * 获取IMSI
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonyManager.getSubscriberId();
		
		return imsi;
	}
	
//	private static String _getIMSI(Context context) {
//		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		return telephonyManager.getSubscriberId();
//	}
	
	/**
	 * 获取IMEI
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
//	private static String _getIMEI(Context context) {
//		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		return telephonyManager.getDeviceId();
//	}

	/**
	 * 获取AndroidId
	 * @param context
	 * @return
	 */
	public static String getAndroidID(Context context) {
		String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    	return androidId;
	}
	
	/**
	 * 获取MAC地址
	 * @param context
	 * @return
	 */
	public static String getMAC(Context context) {
		WifiManager wifi = (WifiManager)(context.getSystemService(Context.WIFI_SERVICE));
		if (wifi == null) {
			return ""; 
		}
		
		WifiInfo info = wifi.getConnectionInfo();
		if (info == null) {
			return ""; 
		}
		
		return info.getMacAddress();
	}
	
	public static final int SCREEN_PORTRAIT = 0;
	public static final int SCREEN_LANDSCAPE = 1;
	/**
	 * 当前屏幕的转向
	 * @param context
	 * @return
	 */
	public static int getScreenOrientation(Context context) {
		DisplayMetrics display = context.getResources().getDisplayMetrics();

        int w = display.widthPixels;
        int h = display.heightPixels;
        
        
		if (h > w){
		   return SCREEN_PORTRAIT;
		}
		
		return SCREEN_LANDSCAPE;
	}
	
	/**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context){
		DisplayMetrics display = context.getResources().getDisplayMetrics();

        return display.widthPixels;
	}
	
	/**
	 * 获取屏幕高度
	 * @param context
	 * @return
	 */
	public static int getScreenHeigth(Context context){
		DisplayMetrics display = context.getResources().getDisplayMetrics();
		
		return display.heightPixels;
	}
	
	
	/**
	 * dp转px
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px转dp
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
}
