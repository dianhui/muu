package com.muu.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.data.ActivityEventInfo;
import com.muu.data.CartoonInfo;
import com.muu.server.MuuClient.ListType;
import com.muu.server.MuuServerWrapper;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private ImageButton mMenuBtn = null;
	private SlidingMenu mSlidingMenu = null;
	private View mChangeListView = null;
	private PopupWindow mChangeListPopup = null;
	
	private ProgressBar mProgress = null;
	private TextView mEmpty = null;
	private ScrollView mCartoonsContainer = null;
	private PullToRefreshScrollView mPullRefreshScrollView = null;
	private ImageView mActivityImageView = null;
	private ImageView mFirstImageView = null;
	private ImageView mSecondImageView = null;
	
	private ListType mCurrentList = null;
	private int mCurrentPage = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		mProgress = (ProgressBar)this.findViewById(R.id.progress_bar);
		mEmpty = (TextView)this.findViewById(R.id.tv_empty);
		mPullRefreshScrollView = (PullToRefreshScrollView)this.findViewById(R.id.sv_middle_content);
		mCartoonsContainer = mPullRefreshScrollView.getRefreshableView();
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new RetrieveCartoonListTask().execute(mCurrentList);
			}
		});

		mActivityImageView = (ImageView)this.findViewById(R.id.imv_activity);
		
		setupSlideMenu();
		setupActionBar();
		setupDropdownView();
		changeList(ListType.RANDOM);
		
		new RetrieveAcitivitiesTask().execute();
	}
	
	@Override
	protected void onDestroy() {
		recycleImvBmp(mActivityImageView);
		recycleImvBmp(mFirstImageView);
		recycleImvBmp(mSecondImageView);
		
		super.onDestroy();
	}
	
	private void recycleImvBmp(ImageView imv) {
		if (imv == null) {
			return;
		}
		
		Drawable drawable = (Drawable) imv.getDrawable();
		if (drawable != null && drawable instanceof BitmapDrawable) {
			BitmapDrawable bmpDrawable = (BitmapDrawable)drawable;
			if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
				bmpDrawable.getBitmap().recycle();
			}
		}
		
		imv = null;
	}
	
	private void changeList(ListType type) {
		if (mCurrentList != null) {
			Log.d(TAG, String.format("current type: %s type: %s", mCurrentList, type));
		}
		if (mCurrentList == type) return;
		
		mCurrentPage = -1;
		mCurrentList = type;
		RetrieveCartoonListTask task = new RetrieveCartoonListTask();
		task.execute(type);
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
		appList.setAdapter(new SlideSettingsListAdapter(MainActivity.this,
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
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)recentBtn.getLayoutParams();
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		recentBtn.setLayoutParams(layoutParams);
		
		recentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						RecentReadListActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.GONE);
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
		
		LinearLayout randomReadLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_random_read);
		randomReadLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.random_read);
				Drawable leftDrawable = MainActivity.this.getResources()
						.getDrawable(R.drawable.ic_list_random_selected);
				leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
						leftDrawable.getMinimumHeight());
				Drawable rightDrawable = MainActivity.this.getResources()
						.getDrawable(R.drawable.ic_change_list);
				rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(),
						rightDrawable.getMinimumHeight());
				listTitle.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
				
				LinearLayout ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_random_read);
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
				
				changeList(ListType.RANDOM);
			}
		});
		
		LinearLayout newUpdateLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_new);
		newUpdateLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.recent_update);
				Drawable leftDrawable = MainActivity.this.getResources()
						.getDrawable(R.drawable.ic_list_new_selected);
				leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
						leftDrawable.getMinimumHeight());
				Drawable rightDrawable = MainActivity.this.getResources()
						.getDrawable(R.drawable.ic_change_list);
				rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(),
						rightDrawable.getMinimumHeight());
				listTitle.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
				
				LinearLayout ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_random_read);
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
				
				changeList(ListType.NEW);
			}
		});
		
		LinearLayout hotLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_hot);
		hotLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.hot_list);
				Drawable leftDrawable = MainActivity.this.getResources()
						.getDrawable(R.drawable.ic_list_hot_selected);
				leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
						leftDrawable.getMinimumHeight());
				Drawable rightDrawable = MainActivity.this.getResources()
						.getDrawable(R.drawable.ic_change_list);
				rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(),
						rightDrawable.getMinimumHeight());
				listTitle.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
				
				LinearLayout ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_random_read);
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
				
				changeList(ListType.TOP);
			}
		});
	}
	
	private void setupFirstCartoon(CartoonInfo info) {
		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.rl_no1);
		setClickEvent(layout, info.id);

		WeakReference<Bitmap> bmpRef = new TempDataLoader().getCartoonCover(info.id);
		if (bmpRef != null && bmpRef.get() != null) {
			mFirstImageView = (ImageView)layout.findViewById(R.id.imv_no1_icon);
			mFirstImageView.setImageBitmap(bmpRef.get());			
		}
		
		ImageView imv = (ImageView) layout.findViewById(R.id.imv_no1_tag);
		imv.setImageDrawable(TempDataLoader.getTopicTagDrawable(
				getApplicationContext(), info.topicCode));
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_no1_name);
		tv.setText(info.name);
	}
	
	private void setupSecondCartoon(CartoonInfo info) {
		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.rl_no2);
		setClickEvent(layout, info.id);
		
		WeakReference<Bitmap> bmpRef = new TempDataLoader().getCartoonCover(info.id);
		if (bmpRef != null && bmpRef.get() != null) {
			mSecondImageView = (ImageView)layout.findViewById(R.id.imv_no2_icon);
			mSecondImageView.setImageBitmap(bmpRef.get());
		}
		
		ImageView imv = (ImageView) layout.findViewById(R.id.imv_no2_tag);
		imv.setImageDrawable(TempDataLoader.getTopicTagDrawable(
				getApplicationContext(), info.topicCode));
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_no2_name);
		tv.setText(info.name);
	}
	
	private static final int sBaseCartoonViewId = 9999;
	private void addMoreCartoons(ArrayList<CartoonInfo> list, Boolean isFirstList) {
		RelativeLayout othersLayout = (RelativeLayout) this
				.findViewById(R.id.rl_others);
		int baseCartoonViewId = sBaseCartoonViewId;
		if (isFirstList) {
			othersLayout.removeAllViews();
		} else {
			baseCartoonViewId = othersLayout.getChildAt(othersLayout.getChildCount() - 1).getId() + 1;
		}
		
		Log.d(TAG, "baseViewId: " + baseCartoonViewId);
		for (int i = 0; i < list.size(); i++) {
			LayoutInflater inflater = LayoutInflater.from(this);
			RelativeLayout layout = (RelativeLayout) inflater.inflate(
					R.layout.cartoon_item_layout, null);
			layout.setId(baseCartoonViewId + i);
			setupCartoonView(layout, list.get(i));
			setClickEvent(layout, list.get(i).id);

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			if (i > 0) {
				params.addRule(RelativeLayout.RIGHT_OF, baseCartoonViewId + i - 1);
			}
			
			if (!isFirstList || i > 2) {
				params.addRule(RelativeLayout.BELOW, baseCartoonViewId + i - 3);
			}

			if (i % 3 == 0) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}

			layout.setLayoutParams(params);
			othersLayout.addView(layout);
		}
	}
	
	private void setupCartoonView(RelativeLayout layout, CartoonInfo info) {
		ImageView imv = (ImageView)layout.findViewById(R.id.imv_icon);
		WeakReference<Bitmap> bmpRef = new TempDataLoader().getCartoonCover(info.id);
		if (bmpRef != null && bmpRef.get() != null) {
			imv.setImageBitmap(bmpRef.get());
		}
		imv = (ImageView) layout.findViewById(R.id.imv_tag);
		imv.setImageDrawable(TempDataLoader.getTopicTagDrawable(
				getApplicationContext(), info.topicCode));
		
		TextView tv = (TextView)layout.findViewById(R.id.tv_name);
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
	
	private class RetrieveCartoonListTask extends
			AsyncTask<ListType, Integer, ArrayList<CartoonInfo>> {
		private ListType mListType = ListType.RANDOM;
		
		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(View.VISIBLE);
			if (mCurrentPage == -1) {
				mCartoonsContainer.setVisibility(View.GONE);
			}
		}
		
		@Override
		protected ArrayList<CartoonInfo> doInBackground(ListType... params) {
			mListType = params[0];
			return retrieveCartoonList(mListType);
		}
		
		@Override
		protected void onPostExecute(ArrayList<CartoonInfo> result) {
			mPullRefreshScrollView.onRefreshComplete();
			mProgress.setVisibility(View.GONE);
			mEmpty.setVisibility(View.GONE);
			mCartoonsContainer.setVisibility(View.VISIBLE);
			
			if (result == null || result.size() < 1) {
				if (mCurrentPage == -1) {
					mCartoonsContainer.setVisibility(View.GONE);
					mEmpty.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getApplicationContext(),
							MainActivity.this.getString(R.string.no_more_data),
							Toast.LENGTH_SHORT).show();
				}
				
				return;
			}
			
			mCurrentPage++;
			if (mCurrentPage == 0) {
				setupFirstCartoon(result.remove(0));
				setupSecondCartoon(result.remove(0));
				addMoreCartoons(result, true);
			} else {
				addMoreCartoons(result, false);
			}
		}
	}
	
	private static final int sFirstRetrieveCount = 8;
	private static final int sRetrieveMoreCount = 9;
	private ArrayList<CartoonInfo> retrieveCartoonList(ListType type) {
		ArrayList<CartoonInfo> list = null;
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		int requestCount = mCurrentPage == -1 ? sFirstRetrieveCount : sRetrieveMoreCount;
		switch (type) {
		case RANDOM:
			list = muuWrapper.getCartoonListByType(type, mCurrentPage + 1, requestCount);
			break;
		case NEW:
			list = muuWrapper.getCartoonListByType(type, mCurrentPage + 1, requestCount);
			break;
		case TOP:
			list = muuWrapper.getCartoonListByType(type, mCurrentPage + 1, requestCount);
			break;
		default:
			break;
		}
		return list;
	}
	
	private class RetrieveAcitivitiesTask extends AsyncTask<Void, Integer, ArrayList<ActivityEventInfo>> {

		@Override
		protected void onPreExecute() {
			mActivityImageView.setVisibility(View.GONE);
		}
		
		@Override
		protected ArrayList<ActivityEventInfo> doInBackground(Void... params) {
			MuuServerWrapper muuWrapper = new MuuServerWrapper(MainActivity.this.getApplicationContext());
			
			return muuWrapper.getActivityEventInfos();
		}
		
		@Override
		protected void onPostExecute(final ArrayList<ActivityEventInfo> result) {
			if (result == null || result.size() < 1) {
				return;
			}
			
			WeakReference<Bitmap> bmpRef = new TempDataLoader().getActivityCover("activityCover");
			if (bmpRef == null || bmpRef.get() == null) {
				Log.d(TAG, "Bitmap of activity cover is null.");
				return;
			}
			
			mActivityImageView.setVisibility(View.VISIBLE);
			mActivityImageView.setImageBitmap(bmpRef.get());
			mActivityImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(),
							EventActivity.class);
					intent.putExtra(EventActivity.sActivityTitle, result.get(0).activityName);
					intent.putExtra(EventActivity.sActivityUrl, result.get(0).activityUrl);
					MainActivity.this.startActivity(intent);
				}
			});
			
		}
	}
}