package com.muu.ui;

import java.util.ArrayList;

import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.ROASTS_COLUMN;
import com.muu.server.MuuServerWrapper;
import com.muu.cartoons.R;
import com.muu.util.TempDataLoader;
import com.muu.volley.VolleyHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryCartoonsListActivity extends StatisticsBaseActivity {
	public static String sTopicCode = "topic_code";
	private static final int sCountInOnePage = 4;
	
	private ProgressBar mProgress;
	private PullToRefreshListView mPullToRefreshListView;
	private CharSequence mTopicCode;
	private int mCurPage = -1;
	private CartoonsListAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_cartoon_list_layout);
		mProgress = (ProgressBar)this.findViewById(R.id.progress_bar);
		mTopicCode = getIntent().getCharSequenceExtra(sTopicCode);
		
		setupActionBar();
		setupViews();
		
		mPullToRefreshListView = (PullToRefreshListView) this
				.findViewById(R.id.lv_books_list);
		mPullToRefreshListView.setOnRefreshListener(
				new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						new RetrieveTopicCartoonListTask().execute(mTopicCode.toString());
					}
				});
		
		new RetrieveTopicCartoonListTask().execute(mTopicCode.toString());
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
				CategoryCartoonsListActivity.this.finish();
			}
		});
		
		ImageButton recentButton = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SearchActivity.class);
				CategoryCartoonsListActivity.this.startActivity(intent);
			}
		});
	}
	
	private void setupViews() {
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setText(TempDataLoader.getTopicString(mTopicCode.toString()));
	}
	
	private void setupBooksList(ArrayList<CartoonInfo> list) {
		mListAdapter = new CartoonsListAdapter(this, list);
		mPullToRefreshListView.getRefreshableView().setAdapter(mListAdapter);
	}
	
	private class CartoonsListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		private ArrayList<CartoonInfo> mList;
		private DatabaseMgr mDbMgr;
		
		public CartoonsListAdapter(Context ctx, ArrayList<CartoonInfo> list) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mList = list;
			mDbMgr = new DatabaseMgr(ctx);
		}
		
		public void addDataList(ArrayList<CartoonInfo> list) {
			for (CartoonInfo cartoonInfo : list) {
				mList.add(cartoonInfo);
			}
			
			this.notifyDataSetChanged();
		}
		
		@Override
        public int getCount() {
			if (mList == null) return 0;
			
	        return mList.size();
        }

		@Override
        public Object getItem(int position) {
	        return mList.get(position);
        }

		@Override
        public long getItemId(int position) {
	        return position;
        }

		@Override
        public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.category_cartoon_list_item, null);
				holder = new ViewHolder();
				holder.icon = (NetworkImageView) convertView
				        .findViewById(R.id.imv_icon);
				holder.name = (TextView) convertView
				        .findViewById(R.id.tv_name);
				holder.author = (TextView) convertView
				        .findViewById(R.id.tv_author);
				holder.comment = (TextView) convertView
						.findViewById(R.id.tv_new_comment);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (mList != null && mList.get(position) != null) {
				CartoonInfo info = mList.get(position);
				holder.name.setText(info.name);
				holder.author.setText(getString(R.string.author, info.author));
				if (!TextUtils.isEmpty(info.coverUrl)) {
					holder.icon.setImageUrl(info.coverUrl, VolleyHelper.getInstance(mCtx).getImageLoader());
				}
				
				holder.comment.setVisibility(View.GONE);
				
				Cursor cursor = mDbMgr.query(DatabaseMgr.ROASTS_ALL_URL,
						null, String.format("%s=%d",
								DatabaseMgr.ROASTS_COLUMN.CARTOON_ID,
								info.id), null, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						String comment = cursor.getString(cursor.getColumnIndex(ROASTS_COLUMN.CONTENT));
						holder.comment.setVisibility(View.VISIBLE);
						holder.comment.setText(comment);
					}
					
					cursor.close();
				}
			}
			
			convertView.setClickable(true);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onItemClicked(position);
				}
			});
			return convertView;
        }
		
		private void onItemClicked(int position) {
			if (mPullToRefreshListView.isRefreshing()) {
				return;
			}
			
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, mList.get(position).id);
			intent.setClass(mCtx, DetailsPageActivity.class);
			mCtx.startActivity(intent);
		}
		
		private class ViewHolder {
			NetworkImageView icon;
			TextView name;
			TextView author;
			TextView comment;
		}
	}
	
	private class RetrieveTopicCartoonListTask extends
			AsyncTask<String, Integer, ArrayList<CartoonInfo>> {
		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected ArrayList<CartoonInfo> doInBackground(String... params) {
			return getCategoryCartoons(params[0]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<CartoonInfo> result) {
			mProgress.setVisibility(View.GONE);
			mPullToRefreshListView.onRefreshComplete();
			
			if (result == null || result.size() < 1) {
				if (mCurPage == -1)
					Toast.makeText(getApplicationContext(),
							CategoryCartoonsListActivity.this.getString(R.string.no_more_data),
							Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (mCurPage == -1) {
				setupBooksList(result);
			} else {
				mListAdapter.addDataList(result);
			}
			
			mCurPage++;
			super.onPostExecute(result);
		}
	}
	
	private ArrayList<CartoonInfo> getCategoryCartoons(String topicCode) {
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		ArrayList<CartoonInfo> list = muuWrapper.getCartoonListByTopic(topicCode, mCurPage + 1, sCountInOnePage);
		if (list == null || list.size() < 1) {
			return list;
		}
		for (CartoonInfo cartoonInfo : list) {
			muuWrapper.getRoasts(cartoonInfo.id, 0, 1);
		}
		
		return list;
	}
}
