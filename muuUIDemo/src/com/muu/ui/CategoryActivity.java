package com.muu.ui;

import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class CategoryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity_layout);
		
		setupActionBar();
		setupBtns();
	}
	
	private void setupActionBar() {
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CategoryActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.INVISIBLE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		TextView title = (TextView)this.findViewById(R.id.tv_action_title);
		title.setVisibility(View.VISIBLE);
		title.setText(this.getString(R.string.category_title));
		
		ImageButton btnRecent = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		btnRecent.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton)this.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.VISIBLE);
		searchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SearchActivity.class);
				CategoryActivity.this.startActivity(intent);
			}
		});
	}
	
	private void setupBtns() {
		RelativeLayout btnContainer = (RelativeLayout)this.findViewById(R.id.rl_btns_container);
		
		Object[] catStrList = TempDataLoader.getTopicsArray();
		for (int i = 0; i < catStrList.length; i++) {
			Button btn = new Button(getApplicationContext());
			btn.setId(9999 + i);
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			if (i > 2) {
				params.addRule(RelativeLayout.BELOW, 9999 + i - 3);
			}
			
			if (i % 3 == 0) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}
			
			if (i % 3 == 1) {
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			}
			
			if (i % 3 == 2) {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			}
			
			btn.setText(catStrList[i].toString());
			btn.setLayoutParams(params);
			btn.setMinWidth(260);
			setBtnOnClickEvent(btn, catStrList[i].toString());
			
			btnContainer.addView(btn);
		}
	}
	
	private void setBtnOnClickEvent(Button btn, final String topicStr) {
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CategoryActivity.this, CategoryCartoonsListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(CategoryCartoonsListActivity.sTopicStr, topicStr);
				CategoryActivity.this.startActivity(intent);
			}
		});
	}
}
