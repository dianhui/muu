package com.muu.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.muu.data.CartoonInfo;
import com.muu.server.MuuServerWrapper;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryCartoonsListActivity extends Activity {
	public static String sTopicStr = "topic_ste";
	private static final int sCountInOnePage = 4;
	
	private ProgressBar mProgress;
	private PullToRefreshListView mPullToRefreshListView;
	private CharSequence mTopicStr;
	private int mCurPage = -1;
	private CartoonsListAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_cartoon_list_layout);
		mProgress = (ProgressBar)this.findViewById(R.id.progress_bar);
		mTopicStr = getIntent().getCharSequenceExtra(sTopicStr);
		
		setupActionBar();
		setupViews();
		
		mPullToRefreshListView = (PullToRefreshListView) this
				.findViewById(R.id.lv_books_list);
		mPullToRefreshListView.setOnRefreshListener(
				new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						new RetrieveTopicCartoonListTask().execute(mTopicStr.toString());
					}
				});
		
		new RetrieveTopicCartoonListTask().execute(mTopicStr.toString());
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
		backText.setText(mTopicStr);
	}
	
	private void setupBooksList(ArrayList<CartoonInfo> list) {
		mListAdapter = new CartoonsListAdapter(this, list);
		mPullToRefreshListView.getRefreshableView().setAdapter(mListAdapter);
	}
	
	private class CartoonsListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		private ArrayList<CartoonInfo> mList;
		
		public CartoonsListAdapter(Context ctx, ArrayList<CartoonInfo> list) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mList = list;
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
				holder.icon = (ImageView) convertView
				        .findViewById(R.id.imv_icon);
				holder.name = (TextView) convertView
				        .findViewById(R.id.tv_name);
				holder.author = (TextView) convertView
				        .findViewById(R.id.tv_author);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (mList != null && mList.get(position) != null) {
				holder.name.setText(mList.get(position).name);
				holder.author.setText(getString(R.string.author,
						mList.get(position).author));
				if (holder.icon.getDrawable() != null
						&& holder.icon.getDrawable() instanceof BitmapDrawable) {
					BitmapDrawable bmpDrawable = (BitmapDrawable) holder.icon
							.getDrawable();
					bmpDrawable.getBitmap().recycle();
				}
				
				WeakReference<Bitmap> bmpRef = new TempDataLoader()
						.getCartoonCover(mList.get(position).id);
				if (bmpRef != null && bmpRef.get() != null) {
					holder.icon.setImageBitmap(bmpRef.get());
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
			ImageView icon;
			TextView name;
			TextView author;
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
	
	private ArrayList<CartoonInfo> getCategoryCartoons(String topicStr) {
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		return muuWrapper.getCartoonListByTopic(topicStr, mCurPage + 1, sCountInOnePage);
	}
}
