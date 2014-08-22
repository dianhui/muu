package com.muu.ui;

import com.muu.cartoon.test.R;
import com.muu.sns.ShareUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShareDialog extends Dialog {
	private Context mCtx;

	public ShareDialog(Context context, int theme) {
		super(context, theme);
		mCtx = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.share_layout);
		
		setupBtns();
	}
	
	private void setupBtns() {
		TextView tv = (TextView)findViewById(R.id.tv_sina_weibo);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(mCtx, mCtx.getString(R.string.to_be_done),
//						Toast.LENGTH_SHORT).show();
				ShareUtils.share2SinaWeibo(mCtx);
			}
		});
		
		tv = (TextView)findViewById(R.id.tv_tencent_weibo);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mCtx, mCtx.getString(R.string.to_be_done),
						Toast.LENGTH_SHORT).show();
			}
		});
		
		tv = (TextView)findViewById(R.id.tv_qq);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mCtx, mCtx.getString(R.string.to_be_done),
						Toast.LENGTH_SHORT).show();
			}
		});
		
		tv = (TextView)findViewById(R.id.tv_wechat);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mCtx, mCtx.getString(R.string.to_be_done),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
