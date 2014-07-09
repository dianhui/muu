package com.muu.ui;

import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopicsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topics_activity_layout);
		
		setupActionBar();
		
		GridView gridView = (GridView)this.findViewById(R.id.grid_topics);
		gridView.setAdapter(new TopicsAdapter(this));
	}
	
	private void setupActionBar() {
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TopicsActivity.this.finish();
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
				TopicsActivity.this.startActivity(intent);
			}
		});
	}
	
	private class TopicsAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		private Object[] mTopicStrs;
		
		public TopicsAdapter(Context ctx) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mTopicStrs = TempDataLoader.getTopicsArray();
		}
		
		@Override
		public int getCount() {
			return mTopicStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final String topicStr = mTopicStrs[position].toString();
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.topic_item, null);
				holder = new ViewHolder();
				holder.relativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.rl_topic_item);
				holder.text = (TextView) convertView
						.findViewById(R.id.tv_topic);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.text.setText(topicStr);
			String topicCode = TempDataLoader.getTopicCode(topicStr);
			holder.relativeLayout.setBackground(TempDataLoader
					.getTopicBgDrawable(mCtx, topicCode));
			
			convertView.setClickable(true);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClick(topicStr);
				}
			});
			
			return convertView;
		}
		
		private void onItemClick(String topicStr) {
			Intent intent = new Intent(TopicsActivity.this, CategoryCartoonsListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(CategoryCartoonsListActivity.sTopicStr, topicStr);
			TopicsActivity.this.startActivity(intent);
		}
	}
	
	static class ViewHolder {
		RelativeLayout relativeLayout;
		TextView text;
	}
}
