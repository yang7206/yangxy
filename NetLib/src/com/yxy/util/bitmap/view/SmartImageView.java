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
		setImage(loadUrl, loadingImageRes, loadingImageRes);
	}

	public void setImage(final String loadUrl, final int loadingImageRes, final int failImageRes) {

		setImageResource(loadingImageRes);

		cancelLoader();

		if (TextUtils.isEmpty(loadUrl)) {
			setImageResource(failImageRes);
			return;
		}
		mLoadUrl = loadUrl;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				weak = BitmapManager.getInstance().getBitmap(getContext(), loadUrl, new IBitmapLoadCallBack() {

					@Override
					public void onLoadSuccess(String url, Bitmap bitmap) {
						if (mLoadUrl == url) {
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

}
