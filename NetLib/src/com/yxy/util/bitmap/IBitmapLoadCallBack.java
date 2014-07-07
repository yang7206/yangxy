package com.yxy.util.bitmap;

import android.graphics.Bitmap;

/**
 * 图片 加载回调
 * 
 * @author yxy
 * 
 */
public interface IBitmapLoadCallBack {
	public void onLoadSuccess(String url, Bitmap bitmap);

	public void onLoadFail(String url, String errorMsg);
}