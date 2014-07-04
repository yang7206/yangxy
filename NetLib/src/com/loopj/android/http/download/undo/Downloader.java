package com.loopj.android.http.download.undo;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.Header;

import android.content.Context;
import android.os.HandlerThread;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileHttpResponseHandler;
import com.loopj.android.http.FileRangeHttpResponseHandler;
import com.loopj.android.http.HeaderConfig;
import com.loopj.android.http.RangeEntity;
import com.loopj.android.http.FileRangeHttpResponseHandler.OnDownloadPercentListener;

public class Downloader {
	private WeakReference<Future<?>> mDownloadFutureRef;
	private Context mContext;
	private String mDownloadUrl;
	private String mStoreUrl;

	private AsyncHttpClient mClient;

	private List<WeakReference<Future<?>>> mTaskList;

	private FileHttpResponseHandler mResponseHandler;

	public Downloader(Context ctx, String downloadUrl, String storeUrl) {
		this.rangeMap = new HashMap<String, Long>();

		this.mContext = ctx;
		this.mDownloadUrl = downloadUrl;
		this.mStoreUrl = storeUrl;
		this.mTaskList = new ArrayList<WeakReference<Future<?>>>();
		this.mClient = new AsyncHttpClient();
		this.createNewFileIfNotExist();
	
		mThread=new HandlerThread("");
	}

	private HandlerThread mThread;
	
	private synchronized void createNewFileIfNotExist() {
		File file = new File(mStoreUrl);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		WeakReference<Future<?>> task = mClient.get(mContext, mDownloadUrl,
				new GetFileSizeResponseHandler());
		mTaskList.add(task);
	}

	public void cancel() {
		pause();
		File file = new File(mStoreUrl);
		if (file.exists()) {
			file.delete();
		}
	}

	public void pause() {
		for (WeakReference<Future<?>> task : mTaskList) {
			Future<?> future = task.get();
			if (future != null) {
				future.cancel(true);
			}
		}
	}

	public void restart() {

	}

	// /**
	// * ִ������
	// *
	// * @param range
	// * �ϵ�λ��
	// */
	// private void _doGet(long range) {
	// NdAsyncHttpClient client = NdAsyncHttpClient.getInstance();
	// if (range == 0) {
	// mDownloadFutureRef = client.get(mContext, mDownloadUrl,
	// mResponseHandler);
	// } else {
	// Map<String, String> headers = new HashMap<String, String>();
	// StringBuilder builder = new StringBuilder("bytes=");
	// builder.append(range);
	// builder.append('-');
	// headers.put("Range", builder.toString());
	// mDownloadFutureRef = client.get(mContext, mDownloadUrl, null,
	// headers, mResponseHandler);
	// }
	// }
	//
	// public void cancel(){
	// if(mDownloadFutureRef!=null){
	// Future<?> future=mDownloadFutureRef.get();
	// if(future!=null){
	// future.cancel(false);
	// }
	// }
	// }
	//
	// /**
	// * ׼������
	// * @return
	// */
	// private boolean doGet() {
	// long range = ensureRange();
	//
	// if (range == -1) {
	// if (mListener != null) {
	// mListener.onFail(new IOException("Unknown storage error!"), null);
	// }
	//
	// return false;
	// }
	//
	// if (!TextUtils.isEmpty(mDownloadRealUrl)) {
	// _doGet(range);
	// return true;
	// }
	//
	// return queryDownloadUrl();
	// }
	//
	//
	// /**
	// * ȷ�����ص���ʼ�ֽ�
	// * @return -1 ��ʾ��������
	// */
	// private long ensureRange() {
	// long range = -1;
	//
	// try {
	// RandomAccessFile randomFile;
	// randomFile = new RandomAccessFile(mFileFullPath, "rw");
	// long length = randomFile.length();
	// if (length < mFileSize) {
	// if (mFileSize == Integer.MAX_VALUE) {
	// length--;
	// length = length < 0 ? 0 : length;
	// randomFile.setLength(length);
	// }
	// } else if (length == mFileSize){
	// //�Ѿ�������ɵ��Զ�����һ���ֽ�
	// length--;
	// length = length < 0 ? 0 : length;
	// randomFile.setLength(length);
	// } else {
	// //������ļ�
	// length = 0;
	// randomFile.setLength(0);
	// }
	// range = length;
	// randomFile.close();
	// } catch (FileNotFoundException e) {
	// range = -1;
	// e.printStackTrace();
	// } catch (IOException e) {
	// range = -1;
	// e.printStackTrace();
	// }
	// return range;
	// }

	private final String CONTENT_RANGE = "Content-Range";
	private final String CONTENT_LENGTH = "Content-Length";

	private long mFileTotalSize;

	private class GetFileSizeResponseHandler extends AsyncHttpResponseHandler {

		public void onStatusCodeReceive(int statusCode, Header[] headers) {
			if (statusCode < 300 && headers != null) {
				try {
					String lengthStr = null;
					for (Header header : headers) {
						if (CONTENT_RANGE.equals(header.getName())) {
							lengthStr = header.getValue().split("/")[1];
							break;
						}
					}
					if (lengthStr == null) {
						for (Header header : headers) {
							if (CONTENT_LENGTH.equals(header.getName())) {
								lengthStr = header.getValue();
								break;
							}
						}
					}
					if (lengthStr != null) {
						mFileTotalSize = Long.parseLong(lengthStr);
					}

					if (mFileTotalSize > 0) {
						long threadRange = mFileTotalSize / ((long) threadNum);
						String threadKey1=updateMap(0, threadRange);
						newThread(threadKey1,0, threadRange);
						String threadKey2=updateMap(threadRange + 1, threadRange * 2);
						newThread(threadKey2,threadRange + 1, threadRange * 2);
						String threadKey3=updateMap(threadRange * 2 + 1, mFileTotalSize);
						newThread(threadKey3,threadRange * 2 + 1, mFileTotalSize);
					}
				} catch (Exception e) {
					// TODO
				}
			} else {
				System.out.println("statusCode :" + statusCode);
			}
		}
	}

	
	private String updateMap(long rangeStart, long rangeEnd){
		final String rangeKey=rangeStart + "-" + rangeEnd;
		rangeMap.put(rangeKey, rangeStart);
		return rangeKey;
	}
	
	private int threadNum;

	private	Map<String, Long> rangeMap ;

	private void newThread(final String rangeKey,long rangeStart, long rangeEnd) {
		
		System.out.println("rangeStart :" + rangeStart + ", rangeEnd :"
				+ rangeEnd);
		RangeEntity rangeEntity = HeaderConfig.createRangeHeader(rangeStart,
				rangeEnd);
		WeakReference<Future<?>> task = mClient.get(mContext, mDownloadUrl,
				new FileRangeHttpResponseHandler(mStoreUrl, rangeStart,
						new OnDownloadPercentListener() {

							@Override
							public void onDownloadSuccess() {

							}

							@Override
							public void onDownloadStart() {

							}

							@Override
							public void onDownloadRangeSize(long receiveLength,
									long rangeEndSize) {
								rangeMap.put(rangeKey, receiveLength);
							}

							@Override
							public void onDownloadFail(Throwable error,
									String content) {

							}
						}), rangeEntity);
		mTaskList.add(task);
	}

}
