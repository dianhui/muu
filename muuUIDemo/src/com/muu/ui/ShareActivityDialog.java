package com.muu.ui;

import com.muu.cartoon.test.R;
import com.muu.sns.QQShareHelper;
import com.muu.sns.SnsConstants;
import com.muu.sns.TencentWeiboHelper;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivityDialog extends Activity implements
		IWeiboHandler.Response {
	private IWeiboShareAPI mSinaWeiboShareAPI;
	private int mCartoonId = -1;
	private String mCartoonName;
	private Bitmap mCartoonCover;
	private String mCartoonCoverUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_layout);
		setFinishOnTouchOutside(true);

		Intent intent = getIntent();
		if (intent != null) {
			mCartoonId = intent.getIntExtra(
					DetailsPageActivity.sCartoonIdExtraKey, -1);
			mCartoonName = intent
					.getStringExtra(DetailsPageActivity.sCartoonNameExtraKey);
			mCartoonCover = intent
					.getParcelableExtra(DetailsPageActivity.sCartoonCoverExtraKey);
			mCartoonCoverUrl = intent
					.getStringExtra(DetailsPageActivity.sCartoonCoverUrlKey);
		}

		setupBtns();
		initSinaWeiboApi();

		if (savedInstanceState != null) {
			mSinaWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
	}

	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		mSinaWeiboShareAPI.handleWeiboResponse(intent, this);

		super.onNewIntent(intent);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		this.finish();

		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, getString(R.string.share_success),
					Toast.LENGTH_SHORT).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, getString(R.string.share_fail),
					Toast.LENGTH_SHORT).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, getString(R.string.share_cancel),
					Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private void setupBtns() {
		setupShare2SinaWeiboBtn();
		setupShare2TencentWeiboBtn();
		setupShare2QQ();
		setupShare2Wechat();
	}

	private void setupShare2SinaWeiboBtn() {
		TextView tv = (TextView) findViewById(R.id.tv_sina_weibo);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendSinaWeibo();
			}
		});
	}

	private void setupShare2TencentWeiboBtn() {
		TextView tv = (TextView) findViewById(R.id.tv_tencent_weibo);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendTencentWeibo();
			}
		});
	}

	private void setupShare2QQ() {
		TextView tv = (TextView) findViewById(R.id.tv_qq);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new QQShareHelper(getApplicationContext()).shareToQQ(
						ShareActivityDialog.this, getQQShareContent(),
						mCartoonCoverUrl, getCartoonUrl());
			}
		});
	}

	private void setupShare2Wechat() {
		TextView tv = (TextView) findViewById(R.id.tv_wechat);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						ShareActivityDialog.this,
						ShareActivityDialog.this.getString(R.string.to_be_done),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initSinaWeiboApi() {
		mSinaWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				SnsConstants.SINA_WEIBO_APP_KEY);
		mSinaWeiboShareAPI.registerApp();
		setupSinaWeiboDownloadListener();
	}

	private void setupSinaWeiboDownloadListener() {
		if (mSinaWeiboShareAPI.isWeiboAppInstalled()) {
			return;
		}

		mSinaWeiboShareAPI
				.registerWeiboDownloadListener(new IWeiboDownloadListener() {
					@Override
					public void onCancel() {
						Toast.makeText(ShareActivityDialog.this,
								R.string.sina_weibo_cancel_download,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void sendSinaWeibo() {
		if (!mSinaWeiboShareAPI.checkEnvironment(true)) {
			// TODO
			return;
		}

		try {
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			TextObject textObj = new TextObject();
			textObj.text = getWeiboContent();
			weiboMessage.textObject = textObj;

			if (mCartoonCover != null) {
				ImageObject imgObj = new ImageObject();
				imgObj.setImageObject(mCartoonCover);
				weiboMessage.imageObject = imgObj;
			}

			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;

			mSinaWeiboShareAPI.sendRequest(request);
		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(ShareActivityDialog.this, e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void sendTencentWeibo() {
		String content = getWeiboContent();
		TencentWeiboHelper.sendWeibo(this, content, mCartoonCoverUrl);
	}

	private String getWeiboContent() {
		return String.format("%s%s%s%s%s", "我正在用漫悠悠看漫画:《", mCartoonName, "》",
				"--", getCartoonUrl());
	}
	
	private String getQQShareContent() {
		return String.format("%s%s%s", "我正在用漫悠悠看漫画:《", mCartoonName, "》");
	}

	private String getCartoonUrl() {
		if (mCartoonId == -1) {
			return "";
		}

		return String.format("%s%d%s", "http://www.muu.com.cn/comics/", mCartoonId,
				".html");
	}

}
