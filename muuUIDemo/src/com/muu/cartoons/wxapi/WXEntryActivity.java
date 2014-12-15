package com.muu.cartoons.wxapi;

import com.muu.cartoons.R;
import com.muu.sns.WeChat;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private WeChat mWeChat;
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
        mWeChat = WeChat.getInstance(this);
        mWeChat.handleRespIntent(getIntent(), this);
    }
    
    @Override
    public void onReq(BaseReq arg0) {
        //do nothing.
    }

    @Override
    public void onResp(BaseResp resp) {
    	Log.d("WechatShare", "error code: " + resp.errCode);
    	
        switch (resp.errCode) {
        case BaseResp.ErrCode.ERR_OK:
            Toast.makeText(this, getString(R.string.share_success), Toast.LENGTH_LONG).show();
            break;  
        case BaseResp.ErrCode.ERR_AUTH_DENIED:  
        	Toast.makeText(this, getString(R.string.share_fail), Toast.LENGTH_LONG).show();
            break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:  
            break;
        default:  
            break;  
        }
        finish();  
    }

}
