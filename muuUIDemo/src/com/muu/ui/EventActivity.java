package com.muu.ui;

import com.muu.uidemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventActivity extends Activity {
	public static final String sActivityTitle = "activity_title";
	public static final String sActivityUrl = "activity_url"; 
	
	private WebView mWebView = null;
	private String mActivityTitle = null;
	private String mActivityUrl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.event_activity_layout);
		mActivityTitle = getIntent().getStringExtra(sActivityTitle);
		mActivityUrl = getIntent().getStringExtra(sActivityUrl);
		mWebView = (WebView)this.findViewById(R.id.wb_activity);
		mWebView.loadUrl(mActivityUrl);
		
		setupActionBar();
	}
	
	
	private void setupActionBar() {
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.INVISIBLE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventActivity.this.finish();
			}
		});
		
		ImageButton recentButton = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.INVISIBLE);
		
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setText(mActivityTitle);
	}
}
