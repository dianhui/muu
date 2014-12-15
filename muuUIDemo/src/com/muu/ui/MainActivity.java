package com.muu.ui;

import java.util.ArrayList;

import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.data.ActivityEventInfo;
import com.muu.data.CartoonInfo;
import com.muu.server.MuuClient.ListType;
import com.muu.server.MuuServerWrapper;
import com.muu.cartoons.R;
import com.muu.util.TempDataLoader;
import com.muu.volley.VolleyHelper;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

public class MainActivity extends StatisticsBaseActivity implements OnPageChangeListener, OnTouchListener {
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
	
	//	private Rect mTopViewPagerRect = null;
	private ViewPager mTopViewPager = null;
	private LinearLayout mDotsGroupView = null;
	private ArrayList<NetworkImageView> mTopCartoonsViews = null;
	
	private ListType mCurrentList = null;
	private int mCurrentPage = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		mVolleyHelper = VolleyHelper.getInstance(getApplicationContext());
		
		mTopViewPager = (ViewPager)this.findViewById(R.id.top_view_pager);
		mTopViewPager.setOnPageChangeListener(this);
		mTopViewPager.setOnTouchListener(this);
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
		
		LinearLayout topLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_top);
		topLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.top_list);
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
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_recommend);
				ll.setVisibility(View.VISIBLE);
				
				imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_2);
				imv.setVisibility(View.VISIBLE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_top);
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
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_recommend);
				ll.setVisibility(View.VISIBLE);
				
				imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_2);
				imv.setVisibility(View.VISIBLE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_top);
				ll.setVisibility(View.VISIBLE);
				mChangeListPopup.dismiss();
				
				changeList(ListType.RANDOM);
			}
		});
		
		LinearLayout recommendLayout = (LinearLayout) mChangeListView
				.findViewById(R.id.ll_recommend);
		recommendLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listTitle.setText(R.string.recommend_list);
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
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_recommend);
				ll.setVisibility(View.GONE);
				
				imv = (ImageView)mChangeListView.findViewById(R.id.imv_divider_2);
				imv.setVisibility(View.GONE);
				
				ll = (LinearLayout)mChangeListView.findViewById(R.id.ll_top);
				ll.setVisibility(View.VISIBLE);
				mChangeListPopup.dismiss();
				
				changeList(ListType.RECOMMEND);
			}
		});
	}
	
	private void setupFirstTwoCartoons(ArrayList<CartoonInfo> result) {
		if (mTopCartoonsViews == null) {
			mTopCartoonsViews = new ArrayList<NetworkImageView>();
		}
		
		final CartoonInfo firstInfo = result.remove(0);
		NetworkImageView firstImageView = new NetworkImageView(this);
		firstImageView.setImageUrl(firstInfo.recommendCover, VolleyHelper.getInstance(this).getImageLoader());
		firstImageView.setDefaultImageResId(R.drawable.activity_event_default);
		firstImageView.setBackgroundResource(R.color.transparent);
		firstImageView.setScaleType(ScaleType.FIT_XY);
		firstImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,  
                LayoutParams.MATCH_PARENT)); 
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
		
		final CartoonInfo secondInfo = result.remove(0);
		NetworkImageView secondImageView = new NetworkImageView(this);
		secondImageView.setImageUrl(secondInfo.recommendCover, VolleyHelper.getInstance(this).getImageLoader());
		secondImageView.setDefaultImageResId(R.drawable.activity_event_default);
		secondImageView.setBackgroundResource(R.color.transparent);
		secondImageView.setScaleType(ScaleType.FIT_XY);
		secondImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,  
                LayoutParams.MATCH_PARENT)); 
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
			// remove the first 2 cartoons as they are retrieved by #RetrieveTop2CartoonListTask
			if (mCurrentList == ListType.RECOMMEND) {
				list.remove(0);
				list.remove(1);
			}
			
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
					mVolleyHelper.getImageLoader());
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
			AsyncTask<Void, Integer, ArrayList<CartoonInfo>> {
		@Override
		protected ArrayList<CartoonInfo> doInBackground(Void... arg0) {
			return retrieveTop2CartoonList();
		}

		@Override
		protected void onPostExecute(ArrayList<CartoonInfo> result) {
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
	
	private ArrayList<CartoonInfo> retrieveTop2CartoonList() {
		ArrayList<CartoonInfo> list = null;
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		list = muuWrapper.getTop2CartoonList();
		return list;
	}
	
	private static final int sFirstRetrieveCount = 9;
	private static final int sRetrieveMoreCount = 9;
	private ArrayList<CartoonInfo> retrieveCartoonList(ListType type) {
		ArrayList<CartoonInfo> list = null;
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		int requestCount = (mCurrentPage == -1) ? sFirstRetrieveCount : sRetrieveMoreCount;
		switch (type) {
		case RANDOM:
			list = muuWrapper.getCartoonListByType(type, mCurrentPage + 1, requestCount);
			break;
		case RECOMMEND:
			//Get 2 more for first recommend request, as the top 2 cartoons are put in top scroll view.
			list = muuWrapper.getCartoonListByType(type, mCurrentPage + 1,
					((mCurrentPage == -1) ? requestCount + 2 : requestCount));
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
			new RetrieveTop2CartoonListTask().execute();
			
			if (result == null || result.size() < 1) {
				return;
			}
			
			if (mTopCartoonsViews == null) {
				mTopCartoonsViews = new ArrayList<NetworkImageView>();
			}
			NetworkImageView imgView = new NetworkImageView(
					getApplicationContext());
			imgView.setImageUrl(result.get(0).imgUrl,
					VolleyHelper.getInstance(getApplicationContext())
							.getImageLoader());
			imgView.setDefaultImageResId(R.drawable.activity_event_default);
			imgView.setBackgroundResource(R.color.transparent);
			imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,  
	                LayoutParams.MATCH_PARENT)); 
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
		mTopViewPager.getParent().requestDisallowInterceptTouchEvent(true);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() != R.id.top_view_pager) return false;
		
		v.getParent().requestDisallowInterceptTouchEvent(true);
		return false;
	}
}