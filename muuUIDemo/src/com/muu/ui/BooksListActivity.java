package com.muu.ui;

import com.muu.uidemo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BooksListActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list_layout);
		
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.GONE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BooksListActivity.this.finish();
			}
		});
		
		ImageButton recentButton = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.GONE);
		
		setupBooksList();
	}
	
	private void setupBooksList() {
		ListView listView = (ListView)this.findViewById(R.id.lv_books_list);
		BooksListAdapter listAdapter = new BooksListAdapter(this);
		listView.setAdapter(listAdapter);
	}
	
	private class BooksListAdapter extends BaseAdapter {

		private Context mCtx;
		private LayoutInflater mInflater;
		
		// TODO: need data.
//		private 
		
		public BooksListAdapter(Context ctx) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
		}
		
		@Override
        public int getCount() {
	        return 20;
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

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.category_list_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
				        .findViewById(R.id.imv_icon);
				holder.name = (TextView) convertView
				        .findViewById(R.id.tv_name);
				holder.author = (TextView) convertView
				        .findViewById(R.id.tv_author);
				holder.comment = (TextView) convertView
				        .findViewById(R.id.tv_comment);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			//TODO:
			
			convertView.setClickable(true);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent();
					intent.setClass(mCtx, DetailsPageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mCtx.startActivity(intent);
				}
			});

			return convertView;
        }
		
		private class ViewHolder {
			ImageView icon;
			TextView name;
			TextView author;
			TextView comment;
		}
	}
}
