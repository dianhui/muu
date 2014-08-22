package com.muu.sns;

import com.muu.cartoon.test.R;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.exception.WeiboShareException;

import android.app.Activity;
import android.widget.Toast;

public class ShareUtils {
	public static void share2SinaWeibo(final Activity activity) {
		IWeiboShareAPI weiboShareAPI = WeiboShareSDK.createWeiboAPI(activity,
				SnsConstants.APP_KEY);
		weiboShareAPI.registerApp();

		setupSinaWeiboDownloadListener(activity, weiboShareAPI);
		sendSinaWeibo(activity, weiboShareAPI);
	}

	private static void setupSinaWeiboDownloadListener(final Activity activity,
			IWeiboShareAPI weiboShareAPI) {
		if (weiboShareAPI.isWeiboAppInstalled()) {
			return;
		}

		weiboShareAPI
				.registerWeiboDownloadListener(new IWeiboDownloadListener() {
					@Override
					public void onCancel() {
						Toast.makeText(activity,
								R.string.sina_weibo_cancel_download,
								Toast.LENGTH_SHORT).show();
					}
				});
	}
	
	private static void sendSinaWeibo(Activity activity, IWeiboShareAPI weiboShareAPI) {
		if (!weiboShareAPI.checkEnvironment(true)) {
			//TODO
			return;
		}
		
		try {
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			TextObject textObj = new TextObject();
			textObj.text = "我正在用漫悠悠看漫画";
			weiboMessage.textObject = textObj;

			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			request.transaction = String
					.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;

			weiboShareAPI.sendRequest(request);
		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	
}
