package com.yxy.util.bitmap;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * bitmap缓存 工具类 存放bitmap,回收bitmap等
 * 
 * @author yxy
 * 
 */
public class BitmapCaches {

	private List<String> mKeys;
	private LruCache<String, Bitmap> mCaches;

	private int maxUseMemorySpace;

	private int minBitmapsNumber = 3;

	private int singleBitmapMaxSpace;

	private int maxMemoryBlock = 8;

	private BitmapCaches() {
		mKeys = new ArrayList<String>();
		long maxMemory = Runtime.getRuntime().maxMemory();
		maxUseMemorySpace = (int) (maxMemory / maxMemoryBlock);
		singleBitmapMaxSpace = maxUseMemorySpace / minBitmapsNumber;
		mCaches = new LruCache<String, Bitmap>(maxUseMemorySpace) {

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return BitmapUtils.getByteCount(bitmap);
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
				if (oldValue != null) {
					oldValue.recycle();
				}
			}

			@Override
			protected Bitmap create(String key) {
				return super.create(key);
			}
		};
	}

	private static class BitmapManagerUtilsHolder {
		static BitmapCaches mInstance = new BitmapCaches();
	}

	public static BitmapCaches getInstance() {
		return BitmapManagerUtilsHolder.mInstance;
	}

	private void putImage(String key, Bitmap bitmap) {
		mCaches.put(key, bitmap);
		mKeys.add(key);
	}

	private Bitmap getImage(String key) {
		return mCaches.get(key);
	}

	private void recycle(String key) {
		mCaches.remove(key);
	}

	private void recycleCaches() {
		synchronized (mCaches) {
			for (int i = 0; i < mKeys.size(); i++) {
				String key = mKeys.get(i);
				Bitmap bitmap = mCaches.get(key);
				if (bitmap != null) {
					bitmap.recycle();
				}
				mCaches.remove(key);
			}
			mKeys.clear();
		}
		mCaches.evictAll();
	}

	public static void put(String key, Bitmap bitmap) {
		getInstance().putImage(key, bitmap);
	}

	public static Bitmap get(String key) {
		return getInstance().getImage(key);
	}

	/**
	 * 销毁指定的Bitmap
	 */
	public static void recycleBitmap(String key) {
		getInstance().recycle(key);
	}
	

	public static void putUrl(String url, Bitmap bitmap) {
		getInstance().putImage(getCacheKey(url), bitmap);
	}

	public static Bitmap getUrl(String url) {
		return getInstance().getImage(getCacheKey(url));
	}
	
	/**
	 * 销毁指定url的Bitmap
	 */
	public static void recycleBitmapUrl(String url) {
		getInstance().recycle(getCacheKey(url));
	}

	/**
	 * 销毁
	 */
	public static void destory() {
		getInstance().recycleCaches();
	}

	/**
	 * 回收
	 */
	public static void recycle() {
		getInstance().recycleCaches();
		System.gc();
	}

	/**
	 * 获取单张bitmap内存最大可用大小
	 * 
	 * @return
	 */
	public static int getSingleBitmapMaxSpace() {
		return getInstance().singleBitmapMaxSpace;
	}

	/**
	 * 获取url的缓存key
	 * 
	 * @param url
	 * @return
	 */
	public static String getCacheKey(String url) {
		if (url == null) {
			return "";
		}
		return String.valueOf(url.hashCode());
	}

}
