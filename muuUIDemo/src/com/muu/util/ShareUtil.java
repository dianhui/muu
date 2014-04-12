package com.muu.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

public class ShareUtil {
	public static Intent getShareIntent(Context ctx, String type) {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = ctx.getPackageManager()
				.queryIntentActivities(share, 0);
		if (resInfo.isEmpty()) {
			return null;
		}

		for (ResolveInfo info : resInfo) {
			if (info.activityInfo.packageName.toLowerCase().contains(type)
					|| info.activityInfo.name.toLowerCase().contains(type)) {
				share.setPackage(info.activityInfo.packageName);
				return share;
			}
		}
		return null;
	}
}
