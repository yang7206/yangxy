package com.loopj.android.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.http.Header;

/**
 * 文件返回处理 （1）文件名+存放路径已知 （2）断点续传 (3) 写到SDcard中
 */
public class FileRangeHttpResponseHandler extends AsyncHttpResponseHandler {
	private String mFilePath;
	private RandomAccessFile mRandomFile;
	private long mFileTotalSize = -1;

	private State mState;

	/**
	 * 下载状态
	 * 
	 */
	private enum State {
		NULL, START, DOWNLIADING, FAIL, SUCCESS
	}
	//下载片段开始位置
	private long mRangeStart;	
	//当前接收到的下载片段位置
	private long mCurrDlRange;
	
	private OnDownloadPercentListener mListener;

	public FileRangeHttpResponseHandler(String filePath, long rangeStart,OnDownloadPercentListener lisetner) {
		super();
		this.mFilePath = filePath;
		this.mRangeStart = rangeStart;
		this.mListener = lisetner;
		this.mCurrDlRange = mRangeStart;
	}

	@Override
	public void onContentTypeAndLengthReceive(String contentType, int length) {
		if (mFileTotalSize == -1) {
			mFileTotalSize = mRangeStart + length;
		}
	}

	private final String CONTENT_RANGE = "Content-Range";
	private final String CONTENT_LENGTH = "Content-Length";

	@Override
	public void onStatusCodeReceive(int statusCode, Header[] headers) {
	}

	@Override
	public void onStart() {
		super.onStart();
		sendDownloadStartMessage();
		try {
			if (mRandomFile != null) {
				mRandomFile.close();
			}
			mRandomFile = new RandomAccessFile(mFilePath, "rw");
			System.out.println("onStart seek length:" + mRangeStart);
			mRandomFile.seek(mRangeStart);
		} catch (FileNotFoundException e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		} catch (IOException e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	

	// ////////////////////////////////////////////////////////////////////////////////////

	private void sendDownloadFailMessage(Throwable error, String content) {
		mState = State.FAIL;
		if (mRandomFile != null) {
			try {
				mRandomFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mRandomFile = null;
		}
		if(mListener!=null){
			mListener.onDownloadFail(error, content);
		}
	}

	private void sendDownloadPercentMessage(long receiveLength, long fileSize) {
		if(mListener!=null){
			mListener.onDownloadRangeSize(receiveLength, fileSize);
		}
	}

	private void sendDownloadStartMessage() {
		mState = State.START;
		if(mListener!=null){
			mListener.onDownloadStart();
		}
	}

	private void sendDownloadSuccessMessage() {
		mState = State.SUCCESS;
		if(mListener!=null){
			mListener.onDownloadSuccess();
		}
	}

	@Override
	public void onFinish() {
		super.onFinish();
	}

	@Override
	public void onStartReceive(int contentLength, String charset) {
		super.onStartReceive(contentLength, charset);
		if (mState == State.FAIL || mRandomFile == null) {
			// 开始接收时已经失败，可能是无法写入
			return;
		}
		try {
			long length = mRandomFile.length();
			sendDownloadPercentMessage(length, mFileTotalSize);
		} catch (IOException e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		}
	}

	@Override
	public void onSegmentReceive(byte[] slice, int length) {
		super.onSegmentReceive(slice, length);
		if (mState == State.FAIL || mRandomFile == null) {
			// 开始接收时已经失败，可能是无法写入
			return;
		}
		try {
			mRandomFile.write(slice, 0, length);
			mCurrDlRange = mCurrDlRange + length;
			sendDownloadPercentMessage(mCurrDlRange, mFileTotalSize);
		} catch (Exception e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccessReceive() {
		super.onSuccessReceive();
		if (mState == State.FAIL || mRandomFile == null) {
			// 开始接收时已经失败，可能是无法写入
			return;
		}

		try {
			mRandomFile.close();
			mRandomFile = null;
			System.out.println("下载完成...");
			sendDownloadPercentMessage(mFileTotalSize, mFileTotalSize);
			sendDownloadSuccessMessage();
		} catch (IOException e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		}
	}

	public void close() {
		if (mRandomFile != null) {
			try {
				mRandomFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mRandomFile = null;
		}
	}

	@Override
	public void onFailure(Throwable error, String content) {
		super.onFailure(error, content);
		System.out.println("onFailure :" + error);
		sendDownloadFailMessage(error, content);
	}
	
	public interface OnDownloadPercentListener{
		/**
		 * 开始下载
		 */
		public void onDownloadStart();

		/**
		 * 下载失败
		 */
		public void onDownloadFail(Throwable error, String content);
		/**
		 * 下载进度
		 * 
		 * @param percent
		 * @param receiveLength
		 * @param rangeEndSize
		 */
		public void onDownloadRangeSize(long receiveLength, long rangeEndSize);
		/**
		 * 下载完成
		 */
		public void onDownloadSuccess();
	}
}
