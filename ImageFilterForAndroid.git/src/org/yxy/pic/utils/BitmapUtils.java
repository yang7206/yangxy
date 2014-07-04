package org.yxy.pic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * bitmap工具
 * 
 * @author Administrator
 * 
 */
public class BitmapUtils {

	/**
	 * 读取资源缩放bitmap
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

	/**
	 * 缩放比例
	 * 
	 * @param ops
	 * @param width
	 * @return
	 */
	private static int inSampleSize(BitmapFactory.Options ops, int width) {
		if (ops == null)
			return 1;
		return ops.outWidth % width == 0 ? ops.outWidth / width : ops.outWidth
				/ width + 1;
	}

	/**
	 * 缩放bitmap
	 * 
	 * @param bitmap
	 * @param scale
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 缩放比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	/**
	 * 根据宽度读取文件并缩放
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

	/**
	 * 读取文件并缩放单张bitmap
	 * 
	 * @param context
	 * @param res
	 * @param width
	 * @return
	 */
	public static Bitmap scaleSingleBitmapFile(String file,
			int singleBitmapMaxSpace) {
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file, ops);
		ops.inSampleSize = getSimpleSize(ops, singleBitmapMaxSpace);
		ops.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(file, ops);
		return bitmap;
	}

	/**
	 * 获取bitmap占用大小
	 * @param bitmap
	 * @return
	 */
	public static int getByteCount(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	private static int getSimpleSize(BitmapFactory.Options ops , int singleBitmapMaxSpace){
		int inSimlpeSize = 1 ;
		float byteCount = ((float)ops.outWidth) * ((float)ops.outHeight);
		while (( byteCount * (1f/(float)inSimlpeSize) * (1f/(float)inSimlpeSize)) > (float) singleBitmapMaxSpace) {
			inSimlpeSize ++ ;
		}
		return inSimlpeSize ;
	}
}
