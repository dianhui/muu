package com.muu.util;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PkgMrgUtil {
	public static final String TENCENT_WEIBO_PKG = "com.tencent.wblog";
	public static final String WEIXIN_PKG = "com.tencent.mm";
	public static final String SINA_WEIBO_PKG = "com.sina.weibo";
	public static final String QQ_PKG = "tencent.mobileqq";
	
	public static boolean isPkgInstalled(Context ctx, String pkgName) {
		PackageManager pkgMrg = ctx.getPackageManager();
		List<ApplicationInfo> infoList = pkgMrg.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo info : infoList) {
			if (info.packageName.contains(pkgName)) {
				return true;
			}
		}
		
		return false;
	}

}
