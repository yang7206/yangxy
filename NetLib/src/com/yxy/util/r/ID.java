package com.yxy.util.r;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * 获取资源
 * 
 * @author yxy
 *
 */
final public class ID {
	
	public static final String TAG="ID";
	
	public static final int getId(Context context, String name) {
		return getIdentifier(context, name, "id");
	}
	
	public static int getString(Context context, String name) {
		return getIdentifier(context, name, "string");
	}
	
	public static int getColor(Context context, String name) {
		return getIdentifier(context, name, "color");
	}
	
	public static int getDrawable(Context context, String name) {
		return getIdentifier(context, name, "drawable");
	}
	
	public static int getLayout(Context context, String name) {
		return getIdentifier(context, name, "layout");
	}
	
	public static int getDimen(Context context, String name) {
		return getIdentifier(context, name, "dimen");
	}
	
	public static int getStyle(Context context, String name) {
		return getIdentifier(context, name, "style");
	}
	
	public static int getRaw(Context context, String name) {
		return getIdentifier(context, name, "raw");
	}
	
	public static int getAnim(Context context, String name) {
		return getIdentifier(context, name, "anim");
	}
	
	//TODO:澧炲姞鍏跺畠绫诲瀷
	
	private static int getIdentifier(Context context, String name, String defType) {
		Resources resources = context.getResources();
		String s = context.getPackageName();
		int identifier = resources.getIdentifier(name, defType, s);
		return filterIdentifier(name, defType, identifier);
	}
	
	private static int filterIdentifier(String name, String defType, int identifier) {
		if (identifier == 0) {
			Log.e(TAG, "resource " + name + ", type " + defType + ", undefined.");
		}
		return identifier;
	}

}
