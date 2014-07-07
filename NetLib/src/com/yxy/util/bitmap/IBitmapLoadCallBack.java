package com.yxy.util.bitmap;

import android.graphics.Bitmap;

public interface IBitmapLoadCallBack {
	public void onLoadSuccess(String url, Bitmap bitmap);

	public void onLoadFail(String url, String errorMsg);
}