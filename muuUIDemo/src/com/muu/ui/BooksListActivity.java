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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BooksListActivity extends Activity {
	public static String sListTypeKey = "list_type";
	public static int sListCategory = 0;
	public static int sListRecent = 1;
	public static int sListSearch = 2;
	
	public static String sCategoryIdx = "category_idx";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list_layout);
		
		setupActionBar();
		setupViews();
//		getListData();
		setupBooksList();
	}
	
	private void setupActionBar() {
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
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						BooksListActivity.class);
				intent.putExtra(BooksListActivity.sListTypeKey,
						BooksListActivity.sListSearch);
				BooksListActivity.this.startActivity(intent);
			}
		});

		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		Intent intent = getIntent();
		int listType = intent.getIntExtra(sListTypeKey, 0);
		if (listType == sListCategory) {
			int categoryIdx = intent.getIntExtra(sCategoryIdx, 0);
			String categoryName = this.getResources().getStringArray(
			        R.array.category_text_array)[categoryIdx];
			backText.setText(categoryName);
			return;
		}
		
		if (listType == sListRecent) {
			backText.setText(R.string.recent_read);
			return;
		}
		
		if (listType == sListSearch) {
			backText.setVisibility(View.INVISIBLE);
			TextView title = (TextView)this.findViewById(R.id.tv_action_title);
			title.setVisibility(View.VISIBLE);
			title.setText(R.string.search);
			
			searchBtn.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setupViews() {
		Intent intent = getIntent();
		int listType = intent.getIntExtra(sListTypeKey, 0);
		
		TextView dragMore = (TextView)this.findViewById(R.id.tv_load_more);
		if (listType == sListCategory) {
			dragMore.setVisibility(View.VISIBLE);
		}
		
		if (listType == sListSearch) {
			EditText searchEdit = (EditText)this.findViewById(R.id.et_search);
			searchEdit.setVisibility(View.VISIBLE);
			
			RelativeLayout searchHeader = (RelativeLayout)this.findViewById(R.id.rl_search_header);
			searchHeader.setVisibility(View.VISIBLE);
			
			RelativeLayout shakeChange = (RelativeLayout)this.findViewById(R.id.rl_shake_change);
			shakeChange.setVisibility(View.VISIBLE);
			
			dragMore.setVisibility(View.GONE);
		}
		
	}
	
	private void setupBooksList() {
		ListView listView = (ListView)this.findViewById(R.id.lv_books_list);
		BooksListAdapter listAdapter = new BooksListAdapter(this);
		listView.setAdapter(listAdapter);
	}
	
	private void getListData() {
		Intent intent = getIntent();
		int listType = intent.getIntExtra(sListTypeKey, 0);
		if (listType == sListCategory) {
			return;
		}
		
		if (listType == sListRecent) {
			return;
		}
		
		if (listType == sListSearch) {
			return;
		}
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
				convertView = mInflater.inflate(R.layout.book_list_item, null);
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
