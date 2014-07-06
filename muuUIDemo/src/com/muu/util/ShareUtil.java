package com.muu.util;

import java.util.ArrayList;
import java.util.List;

import com.muu.uidemo.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.widget.Toast;

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
	
	public static void onShareClicked(Context ctx, String cartoonName) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		List<ResolveInfo> infoList = ctx.getPackageManager().queryIntentActivities(intent, 0);
		if (infoList == null) {
			return;
		}
		
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
        for (ResolveInfo info : infoList) {
            Intent targeted = new Intent(Intent.ACTION_SEND);
            targeted.setType("text/plain");
            ActivityInfo activityInfo = info.activityInfo;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
            // judgments : activityInfo.packageName, activityInfo.name, etc.
            if (activityInfo.packageName.contains(PkgMrgUtil.QQ_PKG) || activityInfo.name.contains(PkgMrgUtil.QQ_PKG)
            		|| activityInfo.packageName.contains(PkgMrgUtil.SINA_WEIBO_PKG) || activityInfo.name.contains(PkgMrgUtil.SINA_WEIBO_PKG)
            		|| activityInfo.packageName.contains(PkgMrgUtil.TENCENT_WEIBO_PKG) || activityInfo.name.contains(PkgMrgUtil.TENCENT_WEIBO_PKG)
            		|| activityInfo.packageName.contains(PkgMrgUtil.WEIXIN_PKG) || activityInfo.name.contains(PkgMrgUtil.WEIXIN_PKG)) {
            	targeted.putExtra(Intent.EXTRA_TEXT, ctx.getString(R.string.share_text, cartoonName));
            	targeted.setPackage(activityInfo.packageName);
                targetedShareIntents.add(targeted);
            }
        }
        
        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), ctx.getString(R.string.share_to));
        if (chooserIntent == null) return;
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
        try {
            ctx.startActivity(chooserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, "Can't find share component to share", Toast.LENGTH_SHORT).show();
        }
	}
}
