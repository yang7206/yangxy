package com.loopj.android.http.download.undo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class DownloadManager {

	private Map<String, Downloader> map;

	private static DownloadManager mInstance;

	private DownloadManager() {
		if (map == null) {
			map = new HashMap<String, Downloader>();
		}
	}

	public static DownloadManager getInstance() {
		if (mInstance == null) {
			mInstance = new DownloadManager();
		}
		return mInstance;
	}

	public void newTask(Context ctx, String downloadUrl, String storeDir) {
		Downloader downloader = new Downloader(ctx, downloadUrl, getStoreUrl(downloadUrl, storeDir));
		map.put(downloadUrl, downloader);
		downloader.start();
	}

	
	private String getStoreUrl(String downloadUrl,String storeDir){
		return	storeDir+File.separator+new File(downloadUrl).getName();
	}
	
	public void cancelTask(String downloadUrl) {
		if (map.containsKey(downloadUrl)) {
			Downloader downloader = map.get(downloadUrl);
			if (downloader != null) {
				downloader.cancel();
			}
		}else{
//			File file=	new File(mStoreUrl);
//			if(file.exists()){
//				file.delete();
//			}
		}
	}

}
