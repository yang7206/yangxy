package com.loopj.android.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.protocol.HTTP;

/**
 * 字符串处理
 *
 */
public class StringHttpResponseHandler extends AsyncHttpResponseHandler {
    private String mCharset = HTTP.DEFAULT_CONTENT_CHARSET;
//    private ByteArrayBuffer mBuffer = null; 
    private byte[] mRawBuffer;
    private int mLen = 0;
//    private boolean mOutOfIndex = false;
    
	public StringHttpResponseHandler() {
		super();
	}
	/////////////////////////////////////////////////
	public void onSuccess(String content) {
	}
	//////////////////////////////////////////////   

	@Override
	public void onStartReceive(int contentLength, String charset/*, Header[] headers*/) {
		super.onStartReceive(contentLength, charset/*, headers*/);
//		mBuffer = new ByteArrayBuffer(contentLength);
		mRawBuffer = new byte[contentLength];
		if (charset != null) {
			mCharset = charset;
		}
	}
	@Override
	public void onSegmentReceive(byte[] slice, int length) {
		super.onSegmentReceive(slice, length);
//		mBuffer.append(slice, 0, length);
		if (length <= 0) {
			return;
		}
		final int resultLen =  mLen + length;
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
		
		try {
//			if (!mOutOfIndex) {
				onSuccess(new String(mRawBuffer, 0, mLen, mCharset));
//			} else {
//				onFailure(new OutOfMemoryError("Content-Length too long!"), null);
//			}
		} catch (UnsupportedEncodingException e) {
			onFailure(e, null);
			e.printStackTrace();
		}
	}
}
