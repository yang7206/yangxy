package com.yxy.util.bitmap;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.yxy.util.bitmap.BitmapLoader.OnDoneListener;

/**
 * bitmap管理类
 * 
 * 1.内存中寻找，找到返回，没有找到进行第2步
 * 
 * 2.URL的hashcode寻找sd卡，找到返回，没有找到进行第3步
 * 
 * 3.从指定的地址URL下载，下载完成文件<1M， 返回并存至缓存及本地SD卡，>1M ，先写入本地SD卡然后进行缩放读取存入缓存并返回
 * 
 * @author yxy
 * 
 */
public class BitmapManager {

	private ExecutorService mThreadPools;

	private final int MAX_THREAD_NUMBER = 10;

	private List<WeakReference<Future<?>>> mTaskList;
	private HashMap<String, WeakReference<BitmapLoader>> mLoaderMap;

	private boolean isInit = false;

	private BitmapManager() {
		init();
	}

	private void init() {
		if (!isInit) {
			isInit = true;
			mThreadPools = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
			mTaskList = new ArrayList<WeakReference<Future<?>>>();
			mLoaderMap = new HashMap<String, WeakReference<BitmapLoader>>();
		}
	}

	private static BitmapManager mInstance;

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public synchronized static BitmapManager getInstance() {
		if (mInstance == null) {
			mInstance = new BitmapManager();
		}
		return mInstance;
	}

	private String DIR_CACHES = Environment.getExternalStorageDirectory() + File.separator + "bitmap_caches";

	/**
	 * 设置存放文件夹
	 * 
	 * @param dir
	 */
	public void setDir(String dir) {
		this.DIR_CACHES = dir;
	}

	/**
	 * 获取bitmap,将会自动下载Bitmap
	 * 
	 * @param ctx
	 * @param url
	 *            bitmap的url地址
	 * @param callback
	 */
	public WeakReference<Future<?>> getBitmap(Context ctx, String url, IBitmapLoadCallBack callback) {
		recycle();

		if (TextUtils.isEmpty(url)) {
			callback.onLoadFail(url, "URL IS NULL");
			return null;
		}

		Bitmap bitmap = BitmapCaches.getUrl(url);
		if (bitmap != null && !bitmap.isRecycled()) {
			callback.onLoadSuccess(url, bitmap);
			return null;
		}

		WeakReference<BitmapLoader> reference = mLoaderMap.get(url);
		if (reference != null && reference.get() != null && !reference.get().isDone()) {
			BitmapLoader loader = reference.get();
			loader.addCallback(callback);
		} else {
			BitmapLoader loader = new BitmapLoader(ctx, DIR_CACHES, url, l);
			loader.addCallback(callback);
			mLoaderMap.put(url, new WeakReference<BitmapLoader>(loader));
			Future<?> task = mThreadPools.submit(loader);
			WeakReference<Future<?>> weak = new WeakReference<Future<?>>(task);
			mTaskList.add(weak);
			return weak;
		}
		return null;
	}

	/**
	 * 从assts获取图片
	 * 
	 * @param ctx
	 * @param name
	 * @return
	 */
	public Bitmap getBitmapFromAssts(Context ctx, String name) {
		Bitmap bitmap = getBitmap(name);

		if (bitmap != null) {
			return bitmap;
		}
		
		try {
			InputStream is = ctx.getAssets().open(name);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = BitmapUtils.inSimpleSizeWithInputStream(is, BitmapManager.getSingleBitmapMaxSpace());
			bitmap = BitmapFactory.decodeStream(is, null, opts);
			putBitmap(name, bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			BitmapCaches.recycle();
		}
		return bitmap;
	}

	/**
	 * 从文件获取图片
	 * 
	 * @param ctx
	 * @param name
	 * @return
	 */
	public Bitmap getBitmapFromFile(Context ctx, String filePath) {
		Bitmap bitmap = getBitmap(filePath);

		if (bitmap != null) {
			return bitmap;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		try {
			bitmap = BitmapUtils.scaleBitmapFromFileWhitMaxMemory(file.getAbsolutePath(), BitmapCaches.getSingleBitmapMaxSpace());
			putBitmap(filePath, bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 保存bitmap
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void putBitmap(String key, Bitmap bitmap) {
		if (!TextUtils.isEmpty(key) && bitmap != null) {
			BitmapCaches.put(key, bitmap);
		}
	}

	/**
	 * 获取bitmap ,不存在返回空
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmap(String key) {
		return BitmapCaches.get(key);
	}

	private OnDoneListener l = new OnDoneListener() {

		@Override
		public void onTaskDone(String url) {
			if (mLoaderMap != null && url != null && mLoaderMap.containsKey(url)) {
				mLoaderMap.remove(url);
			}
		}
	};

	/**
	 * 回收
	 */
	private void recycle() {
		if (mTaskList == null || mTaskList.size() <= 0) {
			return;
		}
		synchronized (mTaskList) {
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
	}

	/**
	 * 销毁
	 */
	public void destory() {
		if (mTaskList != null) {
			for (int i = 0; i < mTaskList.size(); i++) {
				Future<?> task = mTaskList.get(i).get();
				if (task != null && !task.isDone()) {
					task.cancel(true);
				}
			}
			mTaskList.clear();
		}
		if (mThreadPools != null) {
			mThreadPools.shutdown();
		}

		if (mLoaderMap != null) {
			mLoaderMap.clear();
		}
		BitmapCaches.destory();
		isInit = false;
		mInstance = null;
	}

	public static int getSingleBitmapMaxSpace() {
		return BitmapCaches.getSingleBitmapMaxSpace();
	}
}
