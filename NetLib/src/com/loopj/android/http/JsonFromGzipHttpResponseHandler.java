package com.loopj.android.http;

/**
 * 字符串处理
 * 
 */
public class JsonFromGzipHttpResponseHandler extends AsyncHttpResponseHandler {
	private byte[] mRawBuffer;
	private int mLen = 0;
	private String charsetName;

	public JsonFromGzipHttpResponseHandler() {
		super();
	}

	public void onSuccessByteArray(byte[] data, int byteCount, String charsetName) {

	}

	@Override
	public void onStartReceive(int contentLength, String charset) {
		super.onStartReceive(contentLength, charset);
		mRawBuffer = new byte[contentLength];
		if (charset != null) {
			charsetName = charset;
		}
	}

	@Override
	public void onSegmentReceive(byte[] slice, int length) {
		super.onSegmentReceive(slice, length);
		if (length <= 0) {
			return;
		}
		final int resultLen = mLen + length;
		if (resultLen <= mRawBuffer.length) {
			System.arraycopy(slice, 0, mRawBuffer, mLen, length);
			mLen += length;
		} else {
			byte[] tmp = new byte[Math.max(mRawBuffer.length << 1, resultLen)];
			System.arraycopy(mRawBuffer, 0, tmp, 0, mLen);
			mRawBuffer = tmp;
			System.arraycopy(slice, 0, mRawBuffer, mLen, length);
			mLen += length;
		}
		slice = null;
	}

	@Override
	public void onSuccessReceive() {
		super.onSuccessReceive();
		onSuccessByteArray(mRawBuffer, mLen, charsetName);
	}
}
