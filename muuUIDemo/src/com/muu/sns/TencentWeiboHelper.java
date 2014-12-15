package com.muu.sns;

import com.muu.cartoons.R;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.Authorize;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.model.BaseVO;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TencentWeiboHelper implements HttpCallback{
	private Context mCtx;
	private WeiboAPI mApi;
	private IShareResponse mResponse;
	
	public TencentWeiboHelper(Context ctx, IShareResponse response) {
		mCtx = ctx.getApplicationContext();
		mResponse = response;
	}
	
	public void sendWeibo(Activity activity, String content, String picUrl) {
		
		String accessToken = Util.getSharePersistent(activity, "ACCESS_TOKEN");
		if (mApi == null) {
			AccountModel account = new AccountModel(accessToken);
			mApi = new WeiboAPI(account);
		}
		
		if (mCtx == null) {
			mCtx = activity.getApplicationContext();
		}
		
		mApi.reAddWeibo(mCtx, content, picUrl, 
				"", "", "", "", this, null, BaseVO.TYPE_JSON);
	}
	
	public void auth(final Activity activity, long appid, String app_secket) {
		// 注册当前应用的appid和appkeysec，并指定一个OnAuthListener
		// OnAuthListener在授权过程中实施监听
		AuthHelper.register(activity, appid, app_secket, new OnAuthListener() {

			// 如果当前设备没有安装腾讯微博客户端，走这里
			@Override
			public void onWeiBoNotInstalled() {
				Toast.makeText(activity, "onWeiBoNotInstalled",
						1000).show();
				AuthHelper.unregister(activity);
				Intent i = new Intent(activity, Authorize.class);
				activity.startActivity(i);
			}

			// 如果当前设备没安装指定版本的微博客户端，走这里
			@Override
			public void onWeiboVersionMisMatch() {
				Toast.makeText(activity, "onWeiboVersionMisMatch",
						1000).show();
				AuthHelper.unregister(activity);
				Intent i = new Intent(activity, Authorize.class);
				activity.startActivity(i);
			}

			// 如果授权失败，走这里
			@Override
			public void onAuthFail(int result, String err) {
				Toast.makeText(activity, "result : " + result,
						1000).show();
				AuthHelper.unregister(activity);
			}

			// 授权成功，走这里
			// 授权成功后，所有的授权信息是存放在WeiboToken对象里面的，可以根据具体的使用场景，将授权信息存放到自己期望的位置，
			// 在这里，存放到了applicationcontext中
			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				Toast.makeText(activity, mCtx.getText(R.string.auth_success), Toast.LENGTH_LONG).show();
				
				Util.saveSharePersistent(activity, "ACCESS_TOKEN",
						token.accessToken);
				Util.saveSharePersistent(activity, "EXPIRES_IN",
						String.valueOf(token.expiresIn));
				Util.saveSharePersistent(activity, "OPEN_ID", token.openID);
				Util.saveSharePersistent(activity, "REFRESH_TOKEN", "");
				Util.saveSharePersistent(activity, "CLIENT_ID", Util.getConfig()
						.getProperty("APP_KEY"));
				Util.saveSharePersistent(activity, "AUTHORIZETIME",
						String.valueOf(System.currentTimeMillis() / 1000l));
				AuthHelper.unregister(activity);
			}
		});

		AuthHelper.auth(activity, "");
	}

	@Override
	public void onResult(Object object) {
		ModelResult result = (ModelResult) object;
			if(result.isExpires()){
				Util.clearSharePersistent(mCtx, "ACCESS_TOKEN");
				mResponse.onShareFailed();
			}else{
				if(result.isSuccess()){
	   				mResponse.onShareSuccess();
	   			}else{
	   				mResponse.onShareFailed();
	   			}
			}
	}
}
