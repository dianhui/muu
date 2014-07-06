package com.muu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkingUtil {
	public static Boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager conMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr == null)
			return false;

		// Use Google recommended way
		NetworkInfo info = conMgr.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}

		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI)
			return info.isConnected();

		return info.isConnected();
	}
}
