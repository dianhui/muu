package com.muu.ui;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.uidemo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
	private ImageButton mMenuBtn = null;
	private SlidingMenu mSlidingMenu = null;
	private View mChangeListView = null;
	private PopupWindow mChangeListPopup = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		setUpSlideMenu();
		mMenuBtn = (ImageButton) findViewById(R.id.imbtn_slide_category);
		mMenuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSlidingMenu.toggle();
			}
		});
		
		mChangeListView = LayoutInflater.from(this).inflate(
		        R.layout.change_list_popup_layout, null);
		
		mChangeListPopup = new PopupWindow(mChangeListView,
		        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mChangeListPopup.setTouchable(true);
		mChangeListPopup.setOutsideTouchable(true);
		mChangeListPopup.setBackgroundDrawable(new ColorDrawable(0));
		
		final RelativeLayout layout = (RelativeLayout) this
		        .findViewById(R.id.action_bar_layout);
		RelativeLayout topBtn = (RelativeLayout) this
		        .findViewById(R.id.rl_top_btn);
		topBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChangeListPopup.showAsDropDown(layout, 0, -45);
			}
		});
		
		ImageButton recentBtn = (ImageButton) this
				.findViewById(R.id.imbtn_recent_history);
		recentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						BooksListActivity.class);
				intent.putExtra(BooksListActivity.sListTypeKey,
						BooksListActivity.sListRecent);
				MainActivity.this.startActivity(intent);
			}
		});
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						BooksListActivity.class);
				intent.putExtra(BooksListActivity.sListTypeKey,
						BooksListActivity.sListSearch);
				MainActivity.this.startActivity(intent);
			}
		});
		
		setupTop3Views();
		updateOtherBooks();
	}

	private void setUpSlideMenu() {
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mSlidingMenu.setMenu(R.layout.slide_category_layout);

		ListView appList = (ListView) mSlidingMenu
		        .findViewById(R.id.lv_categories);
		appList.setDividerHeight(0);
		appList.setAdapter(new CategoryListAdapter(MainActivity.this,
		        mSlidingMenu));
	}
	
	private void setupTop3Views() {
		RelativeLayout layout = (RelativeLayout)this.findViewById(R.id.rl_no1);
		setClickEvent(layout);
		
		layout = (RelativeLayout)this.findViewById(R.id.rl_no2);
		setClickEvent(layout);
	}
	
	private void updateOtherBooks() {
		RelativeLayout othersLayout = (RelativeLayout)this.findViewById(R.id.rl_others);
		
		for (int i = 0; i < 29; i++) {
			LayoutInflater inflater = LayoutInflater.from(this);
			RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.book_item_layout, null);
			layout.setId(9999+i);
			setClickEvent(layout);

			LayoutParams params = new LayoutParams(
			        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			if (i > 0) {
				params.addRule(RelativeLayout.RIGHT_OF, 9999+i-1);
			}
			
			if (i > 2) {
	            params.addRule(RelativeLayout.BELOW, 9999+i-3);
            }
			
			if (i % 3 == 0) {
	            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
			
			layout.setLayoutParams(params);
			
			othersLayout.addView(layout);
        }
	}
	
	private void setClickEvent(RelativeLayout layout) {
		layout.setClickable(true);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DetailsPageActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}
}
