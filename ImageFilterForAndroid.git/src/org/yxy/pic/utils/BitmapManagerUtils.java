package org.yxy.pic.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * bitmap管理 工具类 存放bitmap,回收bitmap等
 * @author yxy
 *
 */
public class BitmapManagerUtils {

	private List<String> mKeys;
	private LruCache<String, Bitmap> mCaches;

	private int maxUseMemorySpace;

	private int minBitmapsNumber = 3;

	private int singleBitmapMaxSpace;

	private int maxMemoryBlock = 4;

	private BitmapManagerUtils() {
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
		static BitmapManagerUtils mInstance = new BitmapManagerUtils();
	}

	public static BitmapManagerUtils getInstance() {
		return BitmapManagerUtilsHolder.mInstance;
	}

	private void putImage(String key, Bitmap bitmap) {
		mCaches.put(key, bitmap);
		mKeys.add(key);
	}

	private Bitmap getImage(String key) {
		return mCaches.get(key);
	}

	private void recycle() {
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
	 * 销毁
	 */
	public static void destory() {
		getInstance().recycle();
	}
	
	/**
	 * 获取单张bitmap 最大可用大小
	 * @return
	 */
	public static int getSingleBitmapMaxSpace(){
		return getInstance().singleBitmapMaxSpace;
	}

	/**
	 * 获取缩放精度
	 * 
	 * @param bitmap
	 * @return
	 */
	public static float getScale(Bitmap bitmap) {
		float scale = 1.0f;
		while (((float)BitmapUtils.getByteCount(bitmap) * scale * scale) > (float) getInstance().singleBitmapMaxSpace) {
			scale = scale - 0.05f;
		}
		return scale;
	}
	
}
