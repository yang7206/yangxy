package com.yxy.util.bitmap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * bitmap管理类
 * 
 * @author yxy
 * 
 */
public class BitmapManager {

	private ExecutorService mThreadPools;

	private final int MAX_THREAD_NUMBER = 10;

	private List<WeakReference<Future<?>>> mTaskList;

	private BitmapManager() {
		mThreadPools = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
		mTaskList = new ArrayList<WeakReference<Future<?>>>();
	}

	private static class BitmapManagerHolder {
		static BitmapManager mInstance = new BitmapManager();
	}

	public static BitmapManager getInstance() {
		return BitmapManagerHolder.mInstance;
	}
	
	private String DIR_CACHES = Environment.getExternalStorageDirectory()
			+ File.separator + "bitmap_caches";
	
	public void setDir(String dir){
		this.DIR_CACHES = dir ;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param ctx
	 * @param url
	 *            bitmap的url地址
	 * @param callback
	 */
	public void getBitmap(Context ctx, String url, IBitmapLoadCallBack callback) {
		recycle();
		Bitmap bitmap = BitmapCaches.getUrl(url);
		if (bitmap != null && !bitmap.isRecycled()) {
			callback.onLoadSuccess(url, bitmap);
			return;
		}
		Future<?> task = mThreadPools.submit(new BitmapLoader(ctx, DIR_CACHES, url, callback));
		mTaskList.add(new WeakReference<Future<?>>(task));
	}

	/**
	 * 回收
	 */
	private void recycle() {
		if (mTaskList == null || mTaskList.size() <= 0) {
			return;
		}
		List<WeakReference<Future<?>>> doneList = new ArrayList<WeakReference<Future<?>>>();
		for (int i = 0; i < mTaskList.size(); i++) {
			WeakReference<Future<?>> weak = mTaskList.get(i);
			Future<?> task = weak.get();
			if (task == null) {
				doneList.add(weak);
			}
		}
		mTaskList.removeAll(doneList);
	}

	/**
	 * 销毁
	 */
	public void destory() {
		if (mThreadPools != null) {
			mThreadPools.shutdown();
		}
		if (mTaskList != null) {
			for (int i = 0; i < mTaskList.size(); i++) {
				Future<?> task = mTaskList.get(i).get();
				if (task != null && !task.isDone()) {
					task.cancel(true);
				}
			}
			mTaskList.clear();
		}
		BitmapCaches.destory();
	}
}
