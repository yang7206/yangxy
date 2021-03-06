package com.yxy.util.bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

/**
 * bitmap加载器
 * 
 * @author yxy
 * 
 */
class BitmapLoader implements Runnable {
	private String mUrl;
	private List<IBitmapLoadCallBack> mCallbacks;
	private Handler mHandler;
	private String mDir;
	private OnDoneListener mListener;

	// 是否已经完成
	private boolean isDone = false;

	/**
	 * 
	 * @param ctx
	 * @param dir
	 *            存放文件夹
	 * @param url
	 *            下载URL
	 * @param listener
	 *            任务完成回调
	 */
	public BitmapLoader(Context ctx, String dir, String url,
			OnDoneListener listener) {
		this.mHandler = new Handler(ctx.getMainLooper());
		this.mDir = dir;
		this.mUrl = url;
		this.mCallbacks = new ArrayList<IBitmapLoadCallBack>();
		this.mListener = listener;
	}

	/**
	 * 添加 bitmap加载完成回调
	 * 
	 * @param callback
	 */
	public void addCallback(IBitmapLoadCallBack callback) {
		mCallbacks.add(callback);
	}

	/**
	 * 成功回调
	 * 
	 * @param bitmap
	 */
	private void successCallback(Bitmap bitmap) {
		for (IBitmapLoadCallBack callBack : mCallbacks) {
			if (callBack != null) {
				callBack.onLoadSuccess(mUrl, bitmap);
			}
		}
	}

	/**
	 * 失败回调
	 * 
	 * @param errorMsg
	 */
	private void failCallback(String errorMsg) {
		for (IBitmapLoadCallBack callBack : mCallbacks) {
			if (callBack != null) {
				callBack.onLoadFail(mUrl, errorMsg);
			}
		}
	}

	/**
	 * 成功
	 * 
	 * @param bitmap
	 */
	private void postSuccessInfo(final Bitmap bitmap) {
		notifyTaskDone();
		cacheBitmapUrl(mUrl, bitmap);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				successCallback(bitmap);
			}
		});
	}

	/**
	 * 失败
	 * 
	 * @param errorMsg
	 */
	private void postFailInfo(final String errorMsg) {
		notifyTaskDone();
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				failCallback(errorMsg);
			}
		});
	}

	/**
	 * 任务完成
	 */
	private void notifyTaskDone() {
		isDone = true;
		if (mListener != null) {
			mListener.onTaskDone(mUrl);
		}
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	public boolean isDone() {
		return isDone;
	}

	//连接超时时间
	private static final int CONNECT_TIMEOUT = 5000;
	//读取超时时间
	private static final int READ_TIMEOUT = 10000;

	/**
	 * 从文件加载bitmap
	 * 
	 * @param url
	 * @return
	 */
	public boolean loadBitmapFromFile(String url) {
		if (url == null || "".equals(url)) {
			postFailInfo("url is null");
			return false;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			postFailInfo("sdcard not mounted");
			return false;
		}
		File file = new File(mDir, BitmapCaches.getCacheKey(url));
		if (!file.exists()) {
			return false;
		}

		Bitmap bitmap = null;
		try {
			bitmap = BitmapUtils.scaleBitmapFromFileWhitMaxMemory(
					file.getAbsolutePath(),
					BitmapCaches.getSingleBitmapMaxSpace());
		} catch (Exception e) {
			file.delete();
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			BitmapCaches.recycle();
			reLoadFromFile();
			e.printStackTrace();
			return true ;
		}
		if (bitmap != null) {
			postSuccessInfo(bitmap);
			return true;
		}
		return false;
	}
	
	
	private void reLoadFromFile(){
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				loadBitmapFromFile(mUrl);
			}
		}, 100);
	}

	private void cacheBitmapUrl(String url, Bitmap bitmap) {
		BitmapCaches.putUrl(url, bitmap);
	}

	private File getStoreFile() {
		File file = new File(mDir, BitmapCaches.getCacheKey(mUrl));
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (file.exists()) {
			file.delete();
		}
		return file;
	}

	/**
	 * 从url 加载bitmap
	 * 
	 * @param url
	 */
	public void loadBitmapFromUrl(String url) {
		try {
			URLConnection conn = null;
			String proxy = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxy != null && port != -1) {
				Proxy host = new Proxy(java.net.Proxy.Type.HTTP,
						new InetSocketAddress(proxy, port));
				conn = new URL(url).openConnection(host);
			} else {
				conn = new URL(url).openConnection();
			}
			conn.setConnectTimeout(CONNECT_TIMEOUT * 3);
			conn.setReadTimeout(READ_TIMEOUT);

			File file = getStoreFile();

			final int _1M = 1 * 1024 * 1024;
			final int _READ_SIZE_BLOCK = 1024 * 8;
			BufferedOutputStream bos = null;

			// 大于1M 先存储再进行缩放显示
			if (conn.getContentLength() > _1M
					|| conn.getContentLength() > BitmapCaches
							.getSingleBitmapMaxSpace()) {
				BufferedInputStream bis = null;
				try {
					bis = new BufferedInputStream(new FlushedInputStream(
							(InputStream) conn.getContent()), _READ_SIZE_BLOCK);
					FileOutputStream fos = new FileOutputStream(file);
					bos = new BufferedOutputStream(fos, _READ_SIZE_BLOCK);
					byte b[] = new byte[_READ_SIZE_BLOCK];
					int readLen = 0;
					while ((readLen = bis.read(b)) != -1) {
						bos.write(b, 0, readLen);
					}
					bos.flush();
					fos.close();
				} catch (Exception e1) {
					postFailInfo(e1.getLocalizedMessage());
					e1.printStackTrace();
				} finally {
					if (bos != null) {
						bos.close();
					}
					if (bis != null) {
						bis.close();
					}
				}
				loadBitmapFromFile(url);
			} else {
				try {
					Bitmap bitmap = BitmapFactory
							.decodeStream(new FlushedInputStream(
									(InputStream) conn.getContent()));
					postSuccessInfo(bitmap);
					FileOutputStream fos = new FileOutputStream(file);
					bos = new BufferedOutputStream(fos, _READ_SIZE_BLOCK);
					bitmap.compress(CompressFormat.JPEG, 100, bos);
				} catch (Exception e) {
					postFailInfo(e.getLocalizedMessage());
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					postFailInfo(e.getLocalizedMessage());
					e.printStackTrace();
					BitmapCaches.recycle();
				} finally {
					if (bos != null) {
						bos.close();
					}
				}
			}
		} catch (Exception e) {
			postFailInfo(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	@Override
	public void run() {
		if (TextUtils.isEmpty(mUrl)) {
			return;
		}
		if (!loadBitmapFromFile(mUrl)) {
			loadBitmapFromUrl(mUrl);
		}
	}

	public interface OnDoneListener {
		void onTaskDone(String url);
	}

}
