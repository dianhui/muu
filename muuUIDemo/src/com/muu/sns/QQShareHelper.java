package com.muu.sns;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.muu.cartoon.test.R;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQShareHelper {
	private static final String TAG = "QQShareHelper";
	
	public static QQAuth mQQAuth;
	private QQShare mQQShare;
	
	public QQShareHelper(Context ctx) {
		mQQAuth =  QQAuth.createInstance(SnsConstants.QQ_APP_ID, ctx);
		if (mQQAuth != null) {
			mQQShare = new QQShare(ctx, mQQAuth.getQQToken());
		}
	}
	
	public void shareToQQ(final Activity activity, String content,
			String imgUrl, String targetUrl) {
		final Bundle params = getShareParams(activity, content, imgUrl, targetUrl);
		
         new Thread(new Runnable() {
             @Override
             public void run() {
            	 if (mQQShare == null) {
					return;
				}
            	 
                mQQShare.shareToQQ(activity, params, new ShareListener(activity));
             }
         }).start();
	}
	
	private Bundle getShareParams(Activity activity, String content,
			String imgUrl, String targetUrl) {
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE,
				activity.getString(R.string.app_name));
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
		//TODO: need put targetUrl to SHARE_TO_QQ_TARGET_URL, but now muu's web is blocked by qq,
		// if use the real address, the shared message is not complete.
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  activity.getString(R.string.app_name));
		return params;
	}
	
	private class ShareListener implements IUiListener {
		private Activity mActivity;
		public ShareListener(Activity activity) {
			mActivity = activity;
		}
		@Override
		public void onCancel() {
			onShareComplete(mActivity, mActivity.getString(R.string.share_cancel));
         }

         @Override
         public void onComplete(Object response) {
        	onShareComplete(mActivity, mActivity.getString(R.string.share_success));
         }

         @Override
         public void onError(UiError e) {
        	 Log.d(TAG, "onError: "+e.errorMessage+ " code: "+e.errorCode);
        	 
        	 onShareComplete(mActivity, mActivity.getString(R.string.share_fail));
         }
	}
	
	/**
	 * onShareComplete: show share result toast and finish the share dialog activity.
	 * @param
	 * 		- activity: the caller activity.
	 * 		- resultStr: share result string for the toast.
	 * */
	private void onShareComplete(final Activity activity, final String resultStr) {
		activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					 Toast.makeText(activity,
							resultStr,
							Toast.LENGTH_SHORT).show();
					 activity.finish();
				}
			});
	}

}
