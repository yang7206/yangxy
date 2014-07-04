package com.yxy.util.net;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * 网络工具类
 * 
 * @author yxy
 *
 */
public class NetworkUtils {
	
	/**
	 * 刷新代理设置
	 * @param context
	 * @param client
	 */
	@SuppressWarnings("deprecation")
	public static void refreshProxySetting(Context context, HttpClient client) {
		HttpParams params = client.getParams();
		if (isProxyNetwork(context)) {
			String proxy = android.net.Proxy.getDefaultHost(); 
			int port = android.net.Proxy.getDefaultPort();
			HttpHost host = new HttpHost(proxy, port);
			params.setParameter(ConnRouteParams.DEFAULT_PROXY, host);
		} else {
			params.removeParameter(ConnRouteParams.DEFAULT_PROXY);
		}
	}
	
	/**
	 * 构造一个新的HttpClient
	 * @return
	 */
	public static HttpClient newHttpClient() {
		HttpParams params = new BasicHttpParams();
		
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		final int SOCKET_OPERATION_TIMEOUT = 15000;
		HttpConnectionParams.setConnectionTimeout(params, SOCKET_OPERATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
		
		HttpClientParams.setRedirecting(params, false);
		HttpProtocolParams.setUseExpectContinue(params, false);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http",
                PlainSocketFactory.getSocketFactory(), 80));
        return new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
	}
	
	/**
	 * 是否是代理网络  （非WIFI）
	 * @param context
	 * @return
	 */
	private static boolean isProxyNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
        NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
        if (mobNetInfo == null || mobNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return false;
        }
        
        if (mobNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        	return hasProxySetting();
        }
        
        return false;
	}
	
	/**
	 * 网络可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetActivie(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
		NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
		if (mobNetInfo != null && mobNetInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * WIFI可用
	 * @return
	 */
	public static boolean isWifiActive(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
        NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
        if (mobNetInfo != null && mobNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        
        return false;
	}
	
	/**
	 * 移动卡可用
	 * @param context
	 * @return
	 */
	public static boolean isMobileActive(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
        NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
        if (mobNetInfo != null && mobNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        
        return false;
	}
	
	/**
	 * 是否有代理设置
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static boolean hasProxySetting() {
		if (!TextUtils.isEmpty(android.net.Proxy.getDefaultHost()) && 
			 android.net.Proxy.getDefaultPort() != -1) {
            return true;
        }
		return false;
	}
}
