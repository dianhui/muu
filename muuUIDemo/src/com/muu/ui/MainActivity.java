
package com.muu.ui;

import java.util.ArrayList;

import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.data.ActivityEventInfo;
import com.muu.data.CartoonInfo;
import com.muu.data.Top2CartoonInfo;
import com.muu.server.MuuClient.ListType;
import com.muu.server.MuuServerWrapper;
import com.muu.cartoon.test.R;
import com.muu.util.TempDataLoader;
import com.muu.volley.VolleyHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements OnPageChangeListener {
	private static final String TAG = "MainActivity";
	
	private VolleyHelper mVolleyHelper = null;
	private ImageButton mMenuBtn = null;
	private SlidingMenu mSlidingMenu = null;
	private View mChangeListView = null;
	private PopupWindow mChangeListPopup = null;
	
	private ProgressBar mProgress = null;
	private TextView mEmpty = null;
	private ScrollView mCartoonsContainer = null;
	private PullToRefreshScrollView mPullRefreshScrollView = null;
	
	private ViewPager mTopViewPager = null;
	private LinearLayout mDotsGroupView = null;
	private ArrayList<NetworkImageView> mTopCartoonsViews = null;
	
	private ListType mCurrentList = null;
	private int mCurrentPage = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		mVolleyHelper = VolleyHelper.getInstanse(getApplicationContext());
		
		mTopViewPager = (ViewPager)this.findViewById(R.id.top_view_pager);
		mTopViewPager.setOnPageChangeListener(this);
		
		mDotsGroupView = (LinearLayout)this.findViewById(R.id.dots_group);
		
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

		setupSlideMenu();
		setupActionBar();
		setupDropdownView();
		new RetrieveAcitivitiesTask().execute();
		new RetrieveTop2CartoonListTask().execute();
		changeList(ListType.TOP);
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
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
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
		mChangeListView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		mChangeListPopup = new PopupWindow(mChangeListView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
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
				mChangeListPopup.showAsDropDown(
						layout,
						(layout.getWidth() - mChangeListView.getMeasuredWidth()) / 2,
						0);
			}
		});
		
		final TextView listTitle = (TextView)this.findViewById(R.id.tv_title);
		
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
				imv.setVisibility(View.VISIBLE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_hot);
				ll.setVisibility(View.GONE);
				mChangeListPopup.dismiss();
				
				changeList(ListType.TOP);
			}
		});
		
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
	}
	
	private void setupFirstTwoCartoons(ArrayList<Top2CartoonInfo> result) {
		if (mTopCartoonsViews == null) {
			mTopCartoonsViews = new ArrayList<NetworkImageView>();
		}
		
		final Top2CartoonInfo firstInfo = result.remove(0);
		NetworkImageView firstImageView = new NetworkImageView(this);
		firstImageView.setImageUrl(firstInfo.recommendCover, VolleyHelper.getInstanse(this).getDefaultImageLoader());
		firstImageView.setDefaultImageResId(R.drawable.activity_event_default);
		firstImageView.setScaleType(ScaleType.FIT_XY);
		firstImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DetailsPageActivity.class);
				intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, firstInfo.id);
				MainActivity.this.startActivity(intent);
			}
		});
		if (mTopCartoonsViews.size() > 1) {
			mTopCartoonsViews.remove(1);
			mTopCartoonsViews.add(1, firstImageView);
		} else {
			mTopCartoonsViews.add(firstImageView);
		}

		ImageView imv = new ImageView(MainActivity.this);
		imv.setBackgroundResource(R.drawable.ic_dot_normal);
		if (mDotsGroupView.getChildCount() > 1) {
			mDotsGroupView.removeViewAt(1);
			mDotsGroupView.addView(imv, 1, getDotParams());
		} else {
			mDotsGroupView.addView(imv, getDotParams());
		}
		
		final Top2CartoonInfo secondInfo = result.remove(0);
		NetworkImageView secondImageView = new NetworkImageView(this);
		secondImageView.setImageUrl(secondInfo.recommendCover, VolleyHelper.getInstanse(this).getDefaultImageLoader());
		secondImageView.setDefaultImageResId(R.drawable.activity_event_default);
		secondImageView.setScaleType(ScaleType.FIT_XY);
		secondImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DetailsPageActivity.class);
				intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, secondInfo.id);
				MainActivity.this.startActivity(intent);
			}
		});
		if (mTopCartoonsViews.size() > 2) {
			mTopCartoonsViews.remove(2);
			mTopCartoonsViews.add(2, secondImageView);
		} else {
			mTopCartoonsViews.add(secondImageView);
		}
		
		imv = new ImageView(MainActivity.this);
		imv.setBackgroundResource(R.drawable.ic_dot_normal);
		if (mDotsGroupView.getChildCount() > 2) {
			mDotsGroupView.removeViewAt(2);
			mDotsGroupView.addView(imv, 2, getDotParams());
		} else {
			mDotsGroupView.addView(imv, getDotParams());
		}
		
		mTopViewPager.setAdapter(new TopSlideViewAdapter(mTopCartoonsViews));
		mTopViewPager.setCurrentItem(0);
		((ImageView)mDotsGroupView.getChildAt(0)).setBackgroundResource(R.drawable.ic_dot_selected);
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
		if (!TextUtils.isEmpty(info.coverUrl)) {
			NetworkImageView netImv = (NetworkImageView)layout.findViewById(R.id.imv_icon);
			netImv.setImageUrl(info.coverUrl,
					mVolleyHelper.getDefaultImageLoader());
		}
		
		ImageView imv = (ImageView) layout.findViewById(R.id.imv_tag);
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
	
	private class RetrieveTop2CartoonListTask extends
			AsyncTask<Void, Integer, ArrayList<Top2CartoonInfo>> {
		@Override
		protected ArrayList<Top2CartoonInfo> doInBackground(Void... arg0) {
			return retrieveTop2CartoonList();
		}

		@Override
		protected void onPostExecute(ArrayList<Top2CartoonInfo> result) {
			if (result == null || result.size() < 1) {
				return;
			}

			setupFirstTwoCartoons(result);
		}
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
			addMoreCartoons(result, mCurrentPage == 0);
		}
	}
	
	private ArrayList<Top2CartoonInfo> retrieveTop2CartoonList() {
		ArrayList<Top2CartoonInfo> list = null;
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		list = muuWrapper.getTop2CartoonList();
		return list;
	}
	
	private static final int sFirstRetrieveCount = 9;
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
		protected ArrayList<ActivityEventInfo> doInBackground(Void... params) {
			MuuServerWrapper muuWrapper = new MuuServerWrapper(MainActivity.this.getApplicationContext());
			
			return muuWrapper.getActivityEventInfos();
		}
		
		@Override
		protected void onPostExecute(final ArrayList<ActivityEventInfo> result) {
			if (result == null || result.size() < 1) {
				return;
			}
			
			if (mTopCartoonsViews == null) {
				mTopCartoonsViews = new ArrayList<NetworkImageView>();
			}
			NetworkImageView imgView = new NetworkImageView(
					getApplicationContext());
			imgView.setImageUrl(result.get(0).imgUrl,
					VolleyHelper.getInstanse(getApplicationContext())
							.getDefaultImageLoader());
			imgView.setDefaultImageResId(R.drawable.activity_event_default);
			imgView.setOnClickListener(new OnClickListener() {
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
			mTopCartoonsViews.add(0, imgView);
			
			 TopSlideViewAdapter adapter = (TopSlideViewAdapter)mTopViewPager.getAdapter();
			 if (adapter == null) {
				mTopViewPager.setAdapter(new TopSlideViewAdapter(mTopCartoonsViews));
			} else {
				adapter.updateData(mTopCartoonsViews);
			}
			 
			 ImageView imv = new ImageView(MainActivity.this);
			 imv.setBackgroundResource(R.drawable.ic_dot_normal);
			 mDotsGroupView.addView(imv, getDotParams());
		}
	}

	boolean isAutoPlay = false;
	@Override
	public void onPageScrollStateChanged(int arg0) {
		switch (arg0) {
		case 1:
			isAutoPlay = false;
			break;
		case 2:
			isAutoPlay = true;
			break;
		case 0:
			if (mTopViewPager.getCurrentItem() == mTopViewPager.getAdapter()
					.getCount() - 1 && !isAutoPlay) {
				mTopViewPager.setCurrentItem(0);
			} else if (mTopViewPager.getCurrentItem() == 0 && !isAutoPlay) {
				mTopViewPager.setCurrentItem(mTopViewPager.getAdapter()
						.getCount() - 1);
			}
			break;
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < mDotsGroupView.getChildCount(); ++i) {
			ImageView imv = (ImageView)mDotsGroupView.getChildAt(i);
			
			if (arg0 == i) {
				imv.setBackgroundResource(R.drawable.ic_dot_selected);
			} else {
				imv.setBackgroundResource(R.drawable.ic_dot_normal);
			}
		}
	}
	
	private LinearLayout.LayoutParams getDotParams() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			layoutParams.setMargins(10, 0,10, 0);
			return layoutParams;
	}
}