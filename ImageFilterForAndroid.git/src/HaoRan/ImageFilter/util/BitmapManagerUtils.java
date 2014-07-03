package HaoRan.ImageFilter.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.LruCache;

public class BitmapManagerUtils {

	private List<String> mKeys;
	private LruCache<String, Bitmap> mCaches;

	private BitmapManagerUtils() {
		mKeys = new ArrayList<String>();
		long maxMemory = Runtime.getRuntime().maxMemory();
		int useMemory = (int) (maxMemory / 8);
		mCaches = new LruCache<String, Bitmap>(useMemory) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
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

	public static void destory() {
		getInstance().recycle();
	}

	/**
	 * 读取资源图片并缩放
	 * 
	 * @param context
	 * @param res
	 * @param width
	 * @return
	 */
	public static Bitmap scaleBitmapRes(Context context, int res, int width) {
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				res, ops);
		ops.inSampleSize = inSampleSize(ops, width);
		ops.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeResource(context.getResources(), res, ops);
		return bitmap;
	}

	private static int inSampleSize(BitmapFactory.Options ops, int width) {
		return ops.outWidth % width == 0 ? ops.outWidth / width : ops.outWidth
				/ width + 1;
	}

	public static Bitmap smallBitmap(Bitmap bitmap,float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	/**
	 * 读取文件图片并缩放
	 * 
	 * @param context
	 * @param res
	 * @param width
	 * @return
	 */
	public static Bitmap scaleBitmapFile(String file, int width) {
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file, ops);
		ops.inSampleSize = inSampleSize(ops, width);
		ops.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(file, ops);
		return bitmap;
	}

}
