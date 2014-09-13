package com.muu.sns;

import com.tencent.weibo.sdk.android.component.ReAddActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TencentWeiboHelper {
	public static void sendWeibo(Activity activity, String content, String picUrl) {
		Intent i = new Intent(activity, ReAddActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("content", content);
		bundle.putString("pic_url", picUrl);
		i.putExtras(bundle);
		activity.startActivity(i);
	}
}
