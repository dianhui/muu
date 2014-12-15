package com.muu.sns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.muu.volley.VolleyHelper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.tencent.weibo.sdk.android.api.util.Util;

public class SinaWeibo implements WeiboAuthListener {
	private Context mCtx;
	private SsoHandler mSsoHandler;
	private StatusesAPI mStatusesAPI;
	private Oauth2AccessToken mOauth2AccessToken;
	
	public SinaWeibo(Context ctx) {
		mCtx = ctx.getApplicationContext();
	}
	
	public boolean isAccountValid() {
		String accessToken = Util.getSharePersistent(mCtx, "SINA_ACCESS_TOKEN");
    	String expires_in = Util.getSharePersistent(mCtx, "SINA_EXPIRES_IN");
    	Oauth2AccessToken oauth2 = new Oauth2AccessToken();
    	oauth2.setToken(accessToken);
    	oauth2.setExpiresIn(expires_in);
    	if (oauth2 != null && oauth2.isSessionValid()) {
			return true;
		}
		return false;
	}
	
	public void authorise(Activity activity) {
        Log.d("SinaWeibo", "authorise ...");
        AuthInfo weiboAuth = new AuthInfo(activity, SnsConstants.SINA_WEIBO_APP_KEY,
        		SnsConstants.SINA_WEIBO_REDIRECT_URL, SnsConstants.SINA_WEIBO_SCOPE);
        mSsoHandler = new SsoHandler(activity, weiboAuth);
        mSsoHandler.authorize(this);        
    }

	public void invokeAuthCallback(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler == null)
            return;

        Log.d("SinaWeibo", "SsoHandler authorizeCallBack ... ");
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        mSsoHandler = null;
    }
	
	public void sendWeiboWithPicUrl(String content, String picUrl, RequestListener listener) {
        createStatusesAPI();

        Bitmap bmp = VolleyHelper.getInstance(mCtx).getImageLoader().getBitmapFromCache(picUrl);
        mStatusesAPI.upload(content, bmp, "", "", listener);
    }
	
	private void createStatusesAPI() {
		if (mOauth2AccessToken == null) {
        	String accessToken = Util.getSharePersistent(mCtx, "SINA_ACCESS_TOKEN");
        	String expires_in = Util.getSharePersistent(mCtx, "SINA_EXPIRES_IN");
            mOauth2AccessToken = new Oauth2AccessToken(accessToken, expires_in);
        }

        if (mStatusesAPI == null) {
            mStatusesAPI = new StatusesAPI(mCtx, SnsConstants.SINA_WEIBO_APP_KEY, mOauth2AccessToken);
        }
    }
	
	@Override
	public void onCancel() {
	}

	@Override
	public void onComplete(Bundle values) {
		String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");
        String uid = values.getString("uid");
        if (null == token || null == expires_in || null == uid) {
            return;
        }

		Util.saveSharePersistent(mCtx, "SINA_ACCESS_TOKEN", token);
		Util.saveSharePersistent(mCtx, "SINA_EXPIRES_IN",
				String.valueOf(expires_in));
	}

	@Override
	public void onWeiboException(WeiboException values) {
		Log.d("SinaWeibo", "Failed to auth, exception: " + values.getMessage());
	}

}
