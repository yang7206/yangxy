package com.loopj.android.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.HttpUriRequest;

public class HeaderConfig {

	private Map<String, String> map;

	private static HeaderConfig mInstance;

	private List<String> contentTypeList;

	private final String CONTENT_TYPE_KEY = "Content-Type";

	private HeaderConfig() {
		if (map == null) {
			map = new HashMap<String, String>();
		}
		if (contentTypeList == null) {
			contentTypeList = new ArrayList<String>();
		}
	}

	private synchronized static HeaderConfig getInstance() {
		if (mInstance == null) {
			mInstance = new HeaderConfig();
		}
		return mInstance;
	}

	private void addContentTypeList(String typeValue) {
		contentTypeList.add(typeValue);
	}

	private void removeContentTypeList(String typeValue) {
		contentTypeList.remove(typeValue);
	}

	private void clearContentTypeList() {
		contentTypeList.clear();
	}

	private void loadContentTypeList(HttpUriRequest uriRequest) {
		if (contentTypeList != null) {
			synchronized (contentTypeList) {
				if (contentTypeList.size() > 0) {
					StringBuffer value = new StringBuffer();

					for (int i = 0; i < contentTypeList.size(); i++) {
						value.append(contentTypeList.get(i));
						if (i != contentTypeList.size() - 1) {
							value.append(";");
						}
					}
					uriRequest.addHeader(CONTENT_TYPE_KEY, value.toString());
				}
			}
		}
	}

	private void putToHeaderMap(String name, String value) {
		map.put(name, value);
	}

	private void clearHeaderMap() {
		map.clear();
	}

	private void removeHeaderMap(String name) {
		map.remove(name);
	}

	private void loadHeaderMap(HttpUriRequest uriRequest) {
		synchronized (map) {
			Set<String> set = map.keySet();
			Iterator<String> ite = set.iterator();
			while (ite.hasNext()) {
				String key = ite.next();
				String value = map.get(key);
				uriRequest.addHeader(key, value);
			}
		}
	}

	/**
	 * 配置header
	 * 
	 * @param name
	 * @param value
	 */
	public synchronized static void configHeader(String name, String value) {
		getInstance().putToHeaderMap(name, value);
	}

	public static RangeEntity createRangeHeader(long rangeStart, long rangeEnd) {
		return new RangeEntity(rangeStart, rangeEnd);
	}

	/**
	 * 清空header
	 * 
	 * @param name
	 * @param value
	 */
	public synchronized static void clearHeader() {
		getInstance().clearHeaderMap();
	}

	/**
	 * 移除header
	 * 
	 * @param name
	 * @param value
	 */
	public synchronized static void removeHeader(String name) {
		getInstance().removeHeaderMap(name);
	}

	/**
	 * 向HttpUriRequest中加入header
	 * 
	 * @param name
	 * @param value
	 */
	protected synchronized static void loadHeader(HttpUriRequest uriRequest) {
		getInstance().loadHeaderMap(uriRequest);
	}

	/**
	 * 添加Content-Type 值
	 * 
	 * @param typeValue
	 */
	public synchronized static void addContentType(String typeValue) {
		getInstance().addContentTypeList(typeValue);
	}

	/**
	 * 移除Content-Type 值
	 * 
	 * @param typeValue
	 */
	public synchronized static void removeContentType(String typeValue) {
		getInstance().removeContentTypeList(typeValue);
	}

	/**
	 * 清空Content-Type 值
	 * 
	 * @param typeValue
	 */
	public synchronized static void clearContentType() {
		getInstance().clearContentTypeList();
	}

	/**
	 * 向HttpUriRequest中加载Content-Type值
	 * 
	 * @param typeValue
	 */
	protected synchronized static void loadContentType(HttpUriRequest uriRequest) {
		getInstance().loadContentTypeList(uriRequest);
	}

}
