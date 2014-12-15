package com.muu.sns;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.muu.cartoons.R;
import com.muu.util.FileReaderUtil;
import com.muu.util.FileUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.SendMessageToWX.Req;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeChat {

    private volatile static WeChat sInstance;
    private IWXAPI mIWXAPI;
    private Context mCtx;

    public static WeChat getInstance(Context ctx) {
        if (null == sInstance) {
            synchronized (WeChat.class) {
                if (null == sInstance) {
                    sInstance = new WeChat(ctx);
                }
            }
        }
        return sInstance;
    }

    private WeChat(Context ctx) {
    	mCtx = ctx.getApplicationContext();
        mIWXAPI = WXAPIFactory.createWXAPI(ctx, SnsConstants.WECHAT_APP_ID);
        registerApp();
    }

    /*
     * 生成分享缩略图byte[]数据
     */
    private static final int THUMB_SIZE = 90;
    public byte[] getThumbBitmapByteArray(Bitmap bitmap) {
    	// 注意：限制内容大小不超过32KB，即长度为32768
        return FileUtils.getThumbBitmapByteArray(bitmap, THUMB_SIZE, THUMB_SIZE, false);
    }

    /*
     * 向微信注册客户端，是否已安装微信，微信API是否是否支持分享
     */
    public void registerApp() {
        mIWXAPI.registerApp(SnsConstants.WECHAT_APP_ID);
    }
    
    public void handleRespIntent(Intent intent, IWXAPIEventHandler handler) {
        if (intent == null || handler == null || mIWXAPI == null) return;
        
        mIWXAPI.handleIntent(intent, handler);
    }

    public boolean isWXAppInstalled() {
        return mIWXAPI.isWXAppInstalled();
    }

    public boolean isWXAppSupportAPI() {
        return mIWXAPI.isWXAppSupportAPI();
    }

    public boolean sendReq(SendMessageToWX.Req result) {
        return mIWXAPI.sendReq(result);
    }
    
	public void shareByWechat(String content, String targetUrl, String picUrl) {
		new SendWechatMsgTask().execute(content, targetUrl, picUrl);
    }
	
	private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }
	
	private class SendWechatMsgTask extends AsyncTask<String, Integer, SendMessageToWX.Req> {
		@Override
		protected Req doInBackground(String... params) {
			String content = params[0];
			String targetUrl = params[1];
			String picUrl = params[2];
			WXWebpageObject webpage = new WXWebpageObject();
	        if (!TextUtils.isEmpty(targetUrl)) {
	        	webpage.webpageUrl = targetUrl;
			} else {
				webpage.webpageUrl = "http://www.muu.com.cn";
			}
	        
	        WXMediaMessage msg = new WXMediaMessage(webpage);
	        msg.title = mCtx.getString(R.string.app_name);
	        msg.description = content;
	        
	        Bitmap thumb = null;
//	        Bitmap bmp = VolleyHelper.getInstance(mCtx).getImageLoader().getBitmapFromCache(picUrl);
//	        if (bmp != null) {
//				thumb = bmp;
//				msg.thumbData = FileReaderUtil.bmpToByteArray(thumb, false);
//			} else {
				 thumb  = BitmapFactory.decodeResource(
			                mCtx.getResources(), R.drawable.ic_launcher);
				 msg.thumbData = FileReaderUtil.bmpToByteArray(thumb, true);
//			}
	        
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			return req;
		}
		
		@Override
		protected void onPostExecute(SendMessageToWX.Req req) {
			WeChat.this.sendReq(req);
		}
	}
}
