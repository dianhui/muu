package com.muu.ui;

import com.muu.cartoons.R;
import com.muu.sns.IShareResponse;
import com.muu.sns.QQShareHelper;
import com.muu.sns.SinaWeibo;
import com.muu.sns.SnsConstants;
import com.muu.sns.TencentWeiboHelper;
import com.muu.sns.WeChat;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.weibo.sdk.android.api.util.Util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivityDialog extends StatisticsBaseActivity implements RequestListener, IShareResponse {
	private int mCartoonId = -1;
	private String mCartoonName;
	private Bitmap mCartoonCover;
	private String mCartoonCoverUrl;
	private ProgressBar mProgressBar;
	
	private SinaWeibo mSinaWeibo;
	private TencentWeiboHelper mTencentWeibo;

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
		mProgressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
		mSinaWeibo = new SinaWeibo(this);
		mTencentWeibo = new TencentWeiboHelper(this, this);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        mSinaWeibo.invokeAuthCallback(requestCode, resultCode, data);
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
				if (!mSinaWeibo.isAccountValid()) {
					mSinaWeibo.authorise(ShareActivityDialog.this);
					return;
				}
				
				mProgressBar.setVisibility(View.VISIBLE);
				mSinaWeibo.sendWeiboWithPicUrl(getWeiboContent(), mCartoonCoverUrl, ShareActivityDialog.this);
			}
		});
	}

	private void setupShare2TencentWeiboBtn() {
		TextView tv = (TextView) findViewById(R.id.tv_tencent_weibo);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String accessToken = Util.getSharePersistent(ShareActivityDialog.this, "ACCESS_TOKEN");
				if (TextUtils.isEmpty(accessToken)) {
					mTencentWeibo.auth(ShareActivityDialog.this, SnsConstants.TENCENT_WEIBO_APP_ID, SnsConstants.TENCENT_APP_SECRET);
					return;
				}
				
				mProgressBar.setVisibility(View.VISIBLE);
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
				finish();
			}
		});
	}

	private void setupShare2Wechat() {
		TextView tv = (TextView) findViewById(R.id.tv_wechat);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WeChat.getInstance(ShareActivityDialog.this).shareByWechat(
						getQQShareContent(), getCartoonUrl(), mCartoonCoverUrl);
				finish();
			}
		});
	}

	private void sendTencentWeibo() {
		String content = getWeiboContent();
		mTencentWeibo.sendWeibo(this, content, mCartoonCoverUrl);
	}

	private String getWeiboContent() {
		return String.format("%s%s%s%s", "我正在用漫悠悠看漫画: ", mCartoonName,
				"--", getCartoonUrl());
	}
	
	private String getQQShareContent() {
		return String.format("%s%s", "我正在用漫悠悠看漫画:", mCartoonName);
	}

	private String getCartoonUrl() {
		if (mCartoonId == -1) {
			return "";
		}

		return String.format("%s%d%s", "http://www.muu.com.cn/comics/", mCartoonId,
				".html");
	}

	@Override
	public void onComplete(String values) {
		Toast.makeText(this, getString(R.string.share_success),
				Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void onWeiboException(WeiboException values) {
		Log.d("ShareActivityDialog", "Fail to share: " + values.getMessage());
		Toast.makeText(this, getString(R.string.share_fail), Toast.LENGTH_LONG)
				.show();
		finish();
	}

	@Override
	public void onShareSuccess() {
		Toast.makeText(this, getString(R.string.share_success),
				Toast.LENGTH_SHORT).show();
		mProgressBar.setVisibility(View.GONE);
		finish();
	}

	@Override
	public void onShareFailed() {
		Toast.makeText(this, getString(R.string.share_fail), Toast.LENGTH_SHORT)
				.show();
		mProgressBar.setVisibility(View.GONE);
		finish();
	}

	@Override
	public void onShareCanceled() {
		mProgressBar.setVisibility(View.GONE);
	}

}
