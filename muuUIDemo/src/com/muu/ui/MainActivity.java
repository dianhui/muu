package com.muu.ui;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
	private ImageButton mMenuBtn = null;
	private SlidingMenu mSlidingMenu = null;
	private View mChangeListView = null;
	private PopupWindow mChangeListPopup = null;
	
	private ProgressBar mProgress = null;
	private ScrollView mCartoonsContainer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		mProgress = (ProgressBar)this.findViewById(R.id.progress_bar);
		mCartoonsContainer = (ScrollView)this.findViewById(R.id.sv_middle_content);
		
		setupSlideMenu();
		setupActionBar();
		setupDropdownView();
		
		RetrieveCartoonListTask task = new RetrieveCartoonListTask();
		task.execute(sTypeWeekTop);
	}

	private void setupSlideMenu() {
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.img_menu_shadow);
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
	
	private void setupActionBar() {
		mMenuBtn = (ImageButton) findViewById(R.id.imbtn_slide_category);
		mMenuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSlidingMenu.toggle();
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
	}
	
	private void setupDropdownView() {
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
				mChangeListPopup.showAsDropDown(layout, 0, 0);
			}
		});
		
		final TextView listTitle = (TextView)this.findViewById(R.id.tv_title);
		
		LinearLayout weekTopLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_week_top);
		weekTopLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.weekly_top);
				LinearLayout ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_week_top);
				ll.setVisibility(View.GONE);
				
				ImageView imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_1);
				imv.setVisibility(View.GONE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_new);
				ll.setVisibility(View.VISIBLE);
				
				imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_2);
				imv.setVisibility(View.VISIBLE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_hot);
				ll.setVisibility(View.VISIBLE);
				mChangeListPopup.dismiss();
				
				RetrieveCartoonListTask task = new RetrieveCartoonListTask();
				task.execute(sTypeWeekTop);
			}
		});
		
		LinearLayout newUpdateLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_new);
		newUpdateLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.recent_update);
				LinearLayout ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_week_top);
				ll.setVisibility(View.VISIBLE);
				
				ImageView imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_1);
				imv.setVisibility(View.VISIBLE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_new);
				ll.setVisibility(View.GONE);
				
				imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_2);
				imv.setVisibility(View.GONE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_hot);
				ll.setVisibility(View.VISIBLE);
				mChangeListPopup.dismiss();
				
				RetrieveCartoonListTask task = new RetrieveCartoonListTask();
				task.execute(sTypeNew);
			}
		});
		
		LinearLayout hotLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_hot);
		hotLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.hot_list);
				LinearLayout ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_week_top);
				ll.setVisibility(View.VISIBLE);
				
				ImageView imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_1);
				imv.setVisibility(View.VISIBLE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_new);
				ll.setVisibility(View.VISIBLE);
				
				imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_2);
				imv.setVisibility(View.GONE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_hot);
				ll.setVisibility(View.GONE);
				mChangeListPopup.dismiss();
				
				RetrieveCartoonListTask task = new RetrieveCartoonListTask();
				task.execute(sTypeHot);
			}
		});
	}
	
	private void setupFirstCartoon(int firstId) {
		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.rl_no1);
		setClickEvent(layout, firstId);
		
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), firstId));
		Cursor cur = dbMgr.query(uri, null, null, null, null);
		if (cur == null) return;
		if (!cur.moveToFirst()) {
			cur.close();
			return;
		}
		
		CartoonInfo info = new CartoonInfo(cur);
		cur.close();
		dbMgr.closeDatabase();
		
		Log.d("XXX", "cartoon info: "+info.toString());
		
		ImageView imv = (ImageView)layout.findViewById(R.id.imv_no1_icon);
		Bitmap bmp = new TempDataLoader().getCartoonCover(firstId);
		imv.setImageBitmap(bmp);
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_no1_tag);
		tv.setText(info.category);
		
		tv = (TextView)layout.findViewById(R.id.tv_no1_name);
		tv.setText(info.name);
	}
	
	private void setupSecondCartoon(int secondId) {
		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.rl_no2);
		setClickEvent(layout, secondId);
		
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Uri uri = Uri.parse(String.format("%s/%d",
				DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), secondId));
		Cursor cur = dbMgr.query(uri, null, null, null, null);
		if (cur == null) return;
		if (!cur.moveToFirst()) {
			cur.close();
			return;
		}
		
		CartoonInfo info = new CartoonInfo(cur);
		cur.close();
		dbMgr.closeDatabase();
		
		Log.d("XXX", "cartoon info: "+info.toString());
		
		ImageView imv = (ImageView)layout.findViewById(R.id.imv_no2_icon);
		Bitmap bmp = new TempDataLoader().getCartoonCover(secondId);
		imv.setImageBitmap(bmp);
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_no2_tag);
		tv.setText(info.category);
		
		tv = (TextView)layout.findViewById(R.id.tv_no2_name);
		tv.setText(info.name);
	}
	
	private void setupOtherCartoons(ArrayList<Integer> list) {
		RelativeLayout othersLayout = (RelativeLayout) this
				.findViewById(R.id.rl_others);

		for (int i = 0; i < list.size(); i++) {
			LayoutInflater inflater = LayoutInflater.from(this);
			RelativeLayout layout = (RelativeLayout) inflater.inflate(
					R.layout.book_item_layout, null);
			layout.setId(9999 + i);
			setupCartoonView(layout, list.get(i));
			setClickEvent(layout, list.get(i));

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			if (i > 0) {
				params.addRule(RelativeLayout.RIGHT_OF, 9999 + i - 1);
			}

			if (i > 2) {
				params.addRule(RelativeLayout.BELOW, 9999 + i - 3);
			}

			if (i % 3 == 0) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}

			layout.setLayoutParams(params);
			othersLayout.addView(layout);
		}
	}
	
	private void setupCartoonView(RelativeLayout layout, Integer id) {
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Uri uri = Uri.parse(String.format("%s/%d",
				DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), id));
		Cursor cur = dbMgr.query(uri, null, null, null, null);
		if (cur == null)
			return;
		if (!cur.moveToFirst()) {
			cur.close();
			return;
		}
		
		CartoonInfo info = new CartoonInfo(cur);
		cur.close();
		dbMgr.closeDatabase();
		ImageView imv = (ImageView)layout.findViewById(R.id.imv_icon);
		Bitmap bmp = new TempDataLoader().getCartoonCover(id);
		imv.setImageBitmap(bmp);
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_tag);
		tv.setText(info.category);
		
		tv = (TextView)layout.findViewById(R.id.tv_name);
		tv.setText(info.name);
	}
	
	private void setClickEvent(RelativeLayout layout, final int id) {
		layout.setClickable(true);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DetailsPageActivity.class);
				intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, id);
				MainActivity.this.startActivity(intent);
			}
		});
	}
	
	private static final int sTypeWeekTop = 0;
	private static final int sTypeNew = 1;
	private static final int sTypeHot = 2;
	private class RetrieveCartoonListTask extends
			AsyncTask<Integer, Integer, ArrayList<Integer>> {
		private int mListType = sTypeWeekTop;
		
		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(View.VISIBLE);
			mCartoonsContainer.setVisibility(View.GONE);
		}
		
		@Override
		protected ArrayList<Integer> doInBackground(Integer... params) {
			//TODO: retrieve data from server.
			
			mListType = params[0];
			return retrieveCartoonList(mListType);
		}
		
		@Override
		protected void onPostExecute(ArrayList<Integer> result) {
			mProgress.setVisibility(View.GONE);
			mCartoonsContainer.setVisibility(View.VISIBLE);
			
			setupFirstCartoon(result.remove(0));
			setupSecondCartoon(result.remove(0));
			setupOtherCartoons(result);
		}
	}
	
	private ArrayList<Integer> retrieveCartoonList(Integer type) {
		ArrayList<Integer> list = null;
		TempDataLoader dataLoader = new TempDataLoader();
		
		switch (type) {
		case sTypeWeekTop:
			list = dataLoader.getCartoonIds(TempDataLoader.WEEK_TOP20);
			break;
		case sTypeNew:
			list = dataLoader.getCartoonIds(TempDataLoader.NEW_TOP20);
			break;
		case sTypeHot:
			list = dataLoader.getCartoonIds(TempDataLoader.HOT_TOP20);
			break;

		default:
			break;
		}
		//TODO: retrieve cartoon list from server.
		return list;
	}
}
