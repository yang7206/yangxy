package com.loopj.android.http;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;

public class MiscUtil {
	private static WeakReference<Activity> mHost;
	private static WeakReference<Context> mContext;
    
	public static void bindActivity(Activity ctx) {
		mHost = new WeakReference<Activity>(ctx);
	}
	public static void bindContext(Context context) {
		mContext = new WeakReference<Context>(context);
	}
	
	public static boolean needSetProxy() {
		Context context = null;
		if (mHost != null) {
			context = mHost.get();
		}
		if (context == null && mContext != null) {
			context = mContext.get();
		}
		
		if (context == null) {
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
        if (mobNetInfo == null || "wifi".equals(mobNetInfo.getTypeName().toLowerCase())) {
            return false;
        }
        if (mobNetInfo.getSubtypeName().toLowerCase().contains("cdma")) {
        	//cdma网络 host和port不为空 需要设置代理
            if (android.net.Proxy.getDefaultHost() != null && android.net.Proxy.getDefaultPort() != -1) {
                return true;
            }
            //wap类型需要设置代理
        } else if (mobNetInfo.getExtraInfo().contains("wap")) {
            return true;
        }
        
        return false;
	}
	
}
