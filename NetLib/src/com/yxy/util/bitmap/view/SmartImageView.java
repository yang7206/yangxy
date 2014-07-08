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

public class SmartImageView extends ImageView {

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

	private WeakReference<Future<?>> weak;
	private String mLoadUrl;

	public void setImage(final String loadUrl, final int loadingImageRes) {

		setImageResource(loadingImageRes);

		if (weak != null && weak.get() != null) {
			if (!weak.get().isDone()) {
				weak.get().cancel(true);
			}
		}

		if (TextUtils.isEmpty(loadUrl)) {
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
							setImageResource(loadingImageRes);
						}
					}
				});
			}
		});
	}

}
