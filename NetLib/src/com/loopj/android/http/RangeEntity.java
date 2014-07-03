package com.loopj.android.http;

import org.apache.http.client.methods.HttpUriRequest;

public class RangeEntity {
	private long rangeStart;
	private long rangeEnd;

	private final String RANGE_KEY = "Range";
	public final int RANGE_MAX = -1;

	public RangeEntity(long start, long end) {
		this.rangeStart = start;
		this.rangeEnd = end;
	}

	public void loadRangeHeader(HttpUriRequest uriRequest) {
		String rangeMax = "";
		if (rangeEnd > rangeStart) {
			rangeMax = (rangeEnd == RANGE_MAX ? "" : rangeEnd + "");
		}
		uriRequest.addHeader(RANGE_KEY, "bytes=" + rangeStart + "-" + rangeMax);
	}
}