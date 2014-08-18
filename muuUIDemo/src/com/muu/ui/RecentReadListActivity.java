package com.muu.ui;

import java.util.ArrayList;

import com.android.volley.toolbox.NetworkImageView;
import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.RECENT_HISTORY_COLUMN;
import com.muu.uidemo.R;
import com.muu.volley.VolleyHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecentReadListActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recent_read_list_layout);
		
		setupActionBar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setupConentView();
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
				RecentReadListActivity.this.finish();
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
				RecentReadListActivity.this.startActivity(intent);
			}
		});
	}
	
	private void setupConentView() {
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setText(R.string.recent_read);
		setupCartoonsList(getHistoryCartoons());
	}
	
	private void setupCartoonsList(ArrayList<CartoonInfo> list) {
		ListView listView = (ListView)this.findViewById(R.id.lv_cartoons_list);
		RecentReadListAdapter listAdapter = new RecentReadListAdapter(this, list);
		listView.setAdapter(listAdapter);
	}
	
	private class RecentReadListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		ArrayList<CartoonInfo> mList;
		
		public RecentReadListAdapter(Context ctx, ArrayList<CartoonInfo> list) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mList = list;
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

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (mList != null && mList.get(position) != null) {
				CartoonInfo info = mList.get(position);
				
				holder.name.setText(info.name);
				holder.author.setText(getString(R.string.author,
						info.author));
				if (!TextUtils.isEmpty(info.coverUrl)) {
					holder.icon.setImageUrl(info.coverUrl, VolleyHelper
							.getInstanse(mCtx).getDefaultImageLoader());
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
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			DatabaseMgr dbMgr = new DatabaseMgr(RecentReadListActivity.this);
			Cursor cur = getHistoryCursor(dbMgr, mList.get(position).id);
			if (cur == null || !cur.moveToFirst()) {
				intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, mList.get(position).id);
				intent.setClass(mCtx, DetailsPageActivity.class);
				mCtx.startActivity(intent);
				
				if (cur != null) cur.close();
				dbMgr.closeDatabase();
				return;
			}
			
			int chapterIdx = cur.getInt(cur.getColumnIndex(RECENT_HISTORY_COLUMN.CHAPTER_IDX));
			int pageIdx = cur.getInt(cur.getColumnIndex(RECENT_HISTORY_COLUMN.PAGE_IDX));
			intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, mList.get(position).id);
			intent.putExtra(ReadPageActivity.sChapterIdxExtraKey, chapterIdx);
			intent.putExtra(ReadPageActivity.sPageIdxExtraKey, pageIdx);
			intent.setClass(mCtx, ReadPageActivity.class);
			mCtx.startActivity(intent);
			if (cur != null) cur.close();
			dbMgr.closeDatabase();
		}
		
		private Cursor getHistoryCursor(DatabaseMgr dbMgr, int cartoonId) {
			Cursor cur = dbMgr.query(DatabaseMgr.RECENT_HISTORY_ALL_URL, null,
					String.format("%s=%d", RECENT_HISTORY_COLUMN.CARTOON_ID, cartoonId), null, null);
			if (cur == null) {
				return null;
			}
			
			if (cur.getCount() < 1) {
				cur.close();
				return null;
			}
			
			return cur;
		}
		
		private class ViewHolder {
			NetworkImageView icon;
			TextView name;
			TextView author;
		}
	}

	private ArrayList<CartoonInfo> getHistoryCartoons() {
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Cursor cur = dbMgr.query(DatabaseMgr.RECENT_HISTORY_ALL_URL, null,
				null, null, null);
		if (cur == null) {
			dbMgr.closeDatabase();
			return null;
		}
		
		if (cur.getCount() < 1) {
			cur.close();
			dbMgr.closeDatabase();
			return null;
		}
		
		ArrayList<CartoonInfo> list = new ArrayList<CartoonInfo>();
		while (cur.moveToNext()) {
			CartoonInfo info = new CartoonInfo();
			info.id = cur.getInt(cur.getColumnIndex(RECENT_HISTORY_COLUMN.CARTOON_ID));
			info.name = cur.getString(cur.getColumnIndex(RECENT_HISTORY_COLUMN.CARTOON_NAME));
			info.author = cur.getString(cur.getColumnIndex(RECENT_HISTORY_COLUMN.CARTOON_AUTHOR));
			info.coverUrl = cur.getString(cur.getColumnIndex(RECENT_HISTORY_COLUMN.CARTOON_COVER_URL));
			
			list.add(info);
		}
		cur.close();
		dbMgr.closeDatabase();
		
		return list;
	}
}