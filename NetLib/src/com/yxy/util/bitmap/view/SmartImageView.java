package com.yxy.util.bitmap.view;

import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yxy.util.bitmap.BitmapManager;
import com.yxy.util.bitmap.BitmapUtils;
import com.yxy.util.bitmap.IBitmapLoadCallBack;

/**
 * 自动加载imageView
 * 
 * @author yxy
 * 
 */
public class SmartImageView extends ImageView {

	private WeakReference<Future<?>> weak;
	private String mLoadUrl;
	private Handler mHandler;

	public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SmartImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SmartImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mHandler = new Handler(context.getMainLooper());
	}

	public void setImageFromAssets(String name, String loadUrl, int loadingImageRes) {
		setImageFromAssets(name, loadUrl, loadingImageRes, loadingImageRes);
	}

	public void setImageFromAssets(final String name, final String loadUrl, final int loadingImageRes, final int failImageRes) {

		setImageResource(loadingImageRes);

		cancelLoader();

		Bitmap bitmap = BitmapManager.getInstance().getBitmap(name);

		if (bitmap != null) {
			setImageBitmap(bitmap);
			return;
		}

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Bitmap bt = BitmapManager.getInstance().getBitmapFromAssts(getContext(), name + ".jpg");
				if (bt == null) {
					bt = BitmapManager.getInstance().getBitmapFromAssts(getContext(), name + ".bmp");
				}

				if (bt != null) {
					setImageBitmap(bt);
					return;
				}

				if (TextUtils.isEmpty(loadUrl)) {
					setImageResource(failImageRes);
					return;
				}
				mLoadUrl = loadUrl;

				// 从服务器下载图片
				weak = BitmapManager.getInstance().getBitmap(getContext(), loadUrl, new IBitmapLoadCallBack() {

					@Override
					public void onLoadSuccess(String url, Bitmap bitmap) {
						if (mLoadUrl == url && bitmap != null) {
							setImageBitmap(bitmap);
						}
					}

					@Override
					public void onLoadFail(String url, String errorMsg) {
						if (mLoadUrl == url) {
							setImageResource(failImageRes);
						}
					}
				});
			}
		});

	}

	private void cancelLoader() {
		if (weak != null && weak.get() != null) {
			if (!weak.get().isDone()) {
				weak.get().cancel(true);
			}
		}
	}

	public void setImage(String loadUrl, int loadingImageRes) {
		setImage(loadUrl, loadingImageRes, false);
	}
	
	/**
	 * 设置图片
	 * 
	 * @param loadUrl
	 * @param loadingImageRes
	 */
	public void setImage(String loadUrl, int loadingImageRes , boolean isRoundness) {
		setImage(loadUrl, loadingImageRes, loadingImageRes, false , isRoundness);
	}

	/**
	 * 设置URL 图片
	 * 
	 * @param loadUrl
	 *            加载地址
	 * @param loadingImageRes
	 *            加载时的显示的图片资源
	 * @param isFilterColor
	 *            是否过滤颜色为黑白
	 */
	public void setImage(String loadUrl, int loadingImageRes, boolean isFilterColor, boolean isRoundness) {
		setImage(loadUrl, loadingImageRes, loadingImageRes, isFilterColor, isRoundness);
	}

	/**
	 * 设置URL 图片
	 * 
	 * @param loadUrl
	 *            加载地址
	 * @param loadingImageRes
	 *            加载时的显示的图片资源
	 * @param failImageRes
	 *            加载失败时的显示的图片资源
	 * @param isFilterColor
	 *            是否过滤颜色为黑白
	 * @param isRoundness
	 * 			  是否圆形图片
	 */
	public void setImage(final String loadUrl, final int loadingImageRes, final int failImageRes, final boolean isFilterColor ,final boolean isRoundness) {

		setImageDrawableRound(loadingImageRes, isFilterColor, isRoundness);

		cancelLoader();

		if (TextUtils.isEmpty(loadUrl)) {
			setImageDrawableRound(failImageRes, isFilterColor, isRoundness);
			return;
		}
		mLoadUrl = loadUrl;

		if (isFilterColor) {
			Bitmap bitmap = BitmapManager.getInstance().getBitmap(loadUrl + "_filterColor");
			if (bitmap != null) {
				setImageBitmap(isRoundness?BitmapUtils.getRoundedCornerBitmap(bitmap):bitmap);
				return;
			}
		}

		weak = BitmapManager.getInstance().getBitmap(getContext(), loadUrl, new IBitmapLoadCallBack() {

			@Override
			public void onLoadSuccess(String url, Bitmap bitmap) {
				if (mLoadUrl == url) {
					if (isFilterColor) {
						Bitmap filterBitmap = BitmapUtils.gray(bitmap);
						BitmapManager.getInstance().putBitmap(loadUrl + "_filterColor", filterBitmap);
						setImageBitmap(isRoundness?BitmapUtils.getRoundedCornerBitmap(filterBitmap):filterBitmap);
					} else {
						setImageBitmap(isRoundness?BitmapUtils.getRoundedCornerBitmap(bitmap):bitmap);
					}
				}
			}

			@Override
			public void onLoadFail(String url, String errorMsg) {
				if (mLoadUrl == url) {
					setImageDrawableRound(failImageRes, isFilterColor, isRoundness);
				}
			}
		});
	}
	
	private void setImageDrawableRound(int drawableRes, boolean isFilterColor, boolean isRoundness) {
		if (isRoundness) {
			setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapUtils.loadBitmapRes(getContext(), drawableRes)));
		} else {
			setImageDrawable(BitmapUtils.filterColor(getContext(), drawableRes, isFilterColor));
		}
	}

}
