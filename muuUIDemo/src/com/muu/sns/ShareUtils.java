package com.muu.sns;

import com.muu.cartoon.test.R;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.exception.WeiboShareException;

import android.content.Context;
import android.widget.Toast;

public class ShareUtils {
	public static void share2SinaWeibo(final Context ctx) {
		IWeiboShareAPI weiboShareAPI = WeiboShareSDK.createWeiboAPI(ctx, SnsConstants.APP_KEY);
		weiboShareAPI.registerApp();
		
		if (!weiboShareAPI.isWeiboAppInstalled()) {
			weiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							Toast.makeText(
									ctx,
									R.string.sina_weibo_cancel_download,
									Toast.LENGTH_SHORT).show();
						}
					});
		}
		
		try {
			if (weiboShareAPI.checkEnvironment(true)) {
				WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
				TextObject textObj = new TextObject();
				textObj.text = "我正在用漫悠悠看漫画";
				weiboMessage.textObject = textObj;
				
				SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
				 request.transaction = String.valueOf(System.currentTimeMillis());
				 request.multiMessage = weiboMessage;
				 
				 weiboShareAPI.sendRequest(request);
			}
		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}
}
