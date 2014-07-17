package com.yxy.util.bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * bitmap工具
 * 
 * @author Administrator
 * 
 */
public class BitmapUtils {

	/**
	 * 读取资源根据宽度 缩放bitmap
	 * 
	 * @param context
	 * @param res
	 * @param width
	 * @return
	 */
	public static Bitmap scaleBitmapRes(Context context, int res, int width) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options ops = new BitmapFactory.Options();
			ops.inJustDecodeBounds = true;

			bitmap = BitmapFactory.decodeResource(context.getResources(), res,
					ops);
			ops.inSampleSize = inSampleSize(ops, width);
			ops.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeResource(context.getResources(), res,
					ops);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap loadBitmapRes(Context context, int res) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), res);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
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
		Bitmap resizeBmp = null;
		try {
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale); // 缩放比例
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
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
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options ops = new BitmapFactory.Options();
			ops.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(file, ops);
			ops.inSampleSize = inSampleSize(ops, width);
			ops.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(file, ops);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 根据指定内存空间 读取文件并缩放bitmap
	 * 
	 * @param context
	 * @param res
	 * @param width
	 * @return
	 */
	public static Bitmap scaleBitmapFromFileWhitMaxMemory(String file,
			int singleBitmapMaxSpace) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options ops = new BitmapFactory.Options();
			ops.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(file, ops);
			ops.inSampleSize = getScaleSizeFromMaxMemory(ops,
					singleBitmapMaxSpace);
			ops.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(file, ops);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 获取bitmap占用大小
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int getByteCount(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * 根据指定内存空间 获取缩放精度;
	 * 
	 * @param ops
	 * @param singleBitmapMaxSpace
	 * @return
	 */
	private static int getScaleSizeFromMaxMemory(BitmapFactory.Options ops,
			int singleBitmapMaxSpace) {
		int inSimlpeSize = 1;
		float byteCount = ((float) ops.outWidth) * ((float) ops.outHeight);
		while ((byteCount * (1f / (float) inSimlpeSize) * (1f / (float) inSimlpeSize)) > (float) singleBitmapMaxSpace) {
			inSimlpeSize++;
		}
		return inSimlpeSize;
	}

	/**
	 * 根据流大小 计算缩放比例
	 * 
	 * @param is
	 * @param singleBitmapMaxSpace
	 * @return
	 */
	public static int inSimpleSizeWithInputStream(InputStream is,
			int singleBitmapMaxSpace) {
		int inSimlpeSize = 1;
		if (is == null) {
			return inSimlpeSize;
		}
		try {
			int byteCount = is.available();
			while ((byteCount * (1f / (float) inSimlpeSize) * (1f / (float) inSimlpeSize)) > (float) singleBitmapMaxSpace) {
				inSimlpeSize++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("inSimpleSizeWithInputStream  : " + inSimlpeSize);

		return inSimlpeSize;
	}

	/**
	 * 保存图片
	 * 
	 * @param bitmap
	 * @param name
	 * @return
	 */
	public static boolean saveImage(Bitmap bitmap, String name) {

		File file = new File(name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(name);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 将bitmap变成灰色
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap gray(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(faceIconGreyBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(colorMatrixFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return faceIconGreyBitmap;
	}
	
	/**
	 * 过滤图片资源颜色
	 * @param context
	 * @param resId
	 * @param isEnable
	 * @return
	 */
	public static Drawable filterColor(Context context , int resId, boolean isFilterColor) {
		Drawable mDrawable = context.getResources().getDrawable(resId);
		if (isFilterColor) {
			mDrawable.mutate();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
			mDrawable.setColorFilter(cf);
		}
		return mDrawable;
	}
}
