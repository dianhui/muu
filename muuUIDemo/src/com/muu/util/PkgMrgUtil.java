package com.muu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class PkgMrgUtil {
	private static final String TAG = "PkgMrgUtil";
	public static String getAppVersion(Context ctx) {
		String version = "";
		 PackageInfo pi;
		try {
			pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			version = String.format("%s.%s", pi.versionName, pi.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "version: " + version);
		return version;
	}
}
