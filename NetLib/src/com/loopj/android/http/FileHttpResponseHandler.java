package com.loopj.android.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.http.Header;

import android.os.Handler;

/**
 * 文件返回处理 （1）文件名+存放路径已知 （2）断点续传 (3) 写到SDcard中
 */
public class FileHttpResponseHandler extends AsyncHttpResponseHandler {
	private String mFilePath;
	private RandomAccessFile mRandomFile;
	private long mFileTotalSize = -1;

	// private FileProgressHandler mUiHandler;
	private State mState;

	private int mLastPercent = -100;
	private final static int mStep = 1;

	//
	// private static final int DOWNLOAD_FAIL_MESSAGE = 1;
	// private static final int DOWNLOAD_START_MESSAGE = 2;
	// private static final int DOWNLOAD_PERCENT_MESSAGE = 3;
	// private static final int DOWNLOAD_SUCCESS_MESSAGE = 4;
	//

	/**
	 * 下载状态
	 * 
	 */
	private enum State {
		NULL, START, DOWNLIADING, FAIL, SUCCESS
	}

	public FileHttpResponseHandler(Handler handler) {
		super(handler);
	}

	public FileHttpResponseHandler(String filePath) {
		super();
		this.mFilePath = filePath;
	}

	@Override
	public void onContentTypeAndLengthReceive(String contentType, int length) {
		if (mFileTotalSize == -1) {
			mFileTotalSize = length;
		}
	}

	private final String CONTENT_RANGE = "Content-Range";
	private final String CONTENT_LENGTH = "Content-Length";

	@Override
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
				System.out.println("statusCode :" + statusCode
						+ ",mFileTotalSize :" + mFileTotalSize);
			} catch (Exception e) {
				// TODO
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		sendDownloadStartMessage();
		try {
			if (mRandomFile != null) {
				mRandomFile.close();
			}
			File file = new File(mFilePath);
			System.out.println("mFilePath:" + mFilePath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			mRandomFile = new RandomAccessFile(mFilePath, "rw");
			long length = mRandomFile.length();
			System.out.println("onStart seek length:" + length);
			mRandomFile.seek(length);
			// sendDownloadPercentMessage((int)(length * 100 / mFileTotalSize),
			// length, mFileTotalSize);
		} catch (FileNotFoundException e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		} catch (IOException e) {
			sendDownloadFailMessage(e, null);
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * 开始下载
	 */
	public void onDownloadStart() {
	}

	/**
	 * 下载失败
	 */
	public void onDownloadFail(Throwable error, String content) {
	}

	/**
	 * 下载进度
	 * 
	 * @param percent
	 * @param receiveLength
	 * @param fileSize
	 */
	public void onDownloadPercent(int percent, long receiveLength, long fileSize) {
	}

	/**
	 * 下载完成
	 */
	public void onDownloadSuccess() {
	}

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
		onDownloadFail(error, content);
	}

	private void sendDownloadPercentMessage(int percent, long receiveLength,
			long fileSize) {
		if (percent - mLastPercent < mStep) {
			return;
		}
		mLastPercent = percent;
		onDownloadPercent(percent, receiveLength, fileSize);
	}

	private void sendDownloadStartMessage() {
		mState = State.START;
		onDownloadStart();
	}

	private void sendDownloadSuccessMessage() {
		mState = State.SUCCESS;
		onDownloadSuccess();
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
			sendDownloadPercentMessage((int) (length * 100 / mFileTotalSize),
					length, mFileTotalSize);
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
			long rcvLength = mRandomFile.length();
			sendDownloadPercentMessage(
					(int) (rcvLength * 100 / mFileTotalSize), rcvLength,
					mFileTotalSize);
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
			sendDownloadPercentMessage(100, mFileTotalSize, mFileTotalSize);
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
}
