package com.muu.ui;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsPageActivity extends Activity {
	private SlidingMenu mChaptersSlideView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_page_layout);
		
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.GONE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DetailsPageActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.GONE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		TextView tvTopTitle = (TextView)this.findViewById(R.id.tv_action_title);
		tvTopTitle.setVisibility(View.VISIBLE);
		
		ImageButton btnRecent = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		btnRecent.setVisibility(View.INVISIBLE);
		
		Button btnRead = (Button)this.findViewById(R.id.btn_read);
		btnRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DetailsPageActivity.this, ReadPageActivity.class);
				DetailsPageActivity.this.startActivity(intent);
			}
		});
		
		setupActionBar();
		setupSlidingView();
		setupCommentsView();
	}
	
	private void setupActionBar() {
		ImageButton searchBtn = (ImageButton)this.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.INVISIBLE);
		
		TextView chapterName = (TextView)this.findViewById(R.id.tv_chapter_name);
		chapterName.setVisibility(View.VISIBLE);
		chapterName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mChaptersSlideView.toggle();
			}
		});
	}
	
	private void setupSlidingView() {
		mChaptersSlideView = new SlidingMenu(this);
		mChaptersSlideView.setMode(SlidingMenu.RIGHT);
		mChaptersSlideView.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mChaptersSlideView.setShadowWidthRes(R.dimen.shadow_width);
		mChaptersSlideView.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mChaptersSlideView.setFadeDegree(0.35f);
		mChaptersSlideView.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mChaptersSlideView.setMenu(R.layout.chapter_list_layout);

		ListView listView = (ListView) mChaptersSlideView
		        .findViewById(R.id.lv_chapters);
		listView.setDividerHeight(1);
		
		ArrayList<String> chapterList = new ArrayList<String>();
		for (int i = 0; i < 20; i++) {
			chapterList.add((i+1)+".xxxxxx"); 
        }
		
		listView.setAdapter(new TextListAdapter(getApplicationContext(),
		        chapterList, R.layout.chapter_list_item, R.id.tv_chapter_text));
	}
	
	private void setupCommentsView() {
		ListView listView = (ListView)this.findViewById(R.id.lv_new_comments);
		listView.setDividerHeight(0);
		
		ArrayList<String> commentsList = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			commentsList.add("new comments ...");
        }
		
		listView.setAdapter(new TextListAdapter(getApplicationContext(),
		        commentsList, R.layout.comment_list_item, R.id.tv_comment));
	}
	
	private class TextListAdapter extends BaseAdapter {
		private Context mCtx;
		private int mViewResId;
		private int mItemResId;
		private LayoutInflater mInflater;
		private ArrayList<String> mTextsList;
		
		public TextListAdapter(Context ctx, ArrayList<String> texts, int layoutResId, int viewResId) {
			mCtx = ctx;
			mViewResId = viewResId;
			mItemResId = layoutResId;
			mInflater = LayoutInflater.from(ctx);
			mTextsList = texts;
		}
		
		@Override
        public int getCount() {
	        return mTextsList.size();
        }

		@Override
        public Object getItem(int position) {
	        return mTextsList.get(position);
        }

		@Override
        public long getItemId(int position) {
	        return position;
        }

		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(mItemResId, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
				        .findViewById(mViewResId);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(mTextsList.get(position));
			convertView.setClickable(true);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
//					Intent intent = new Intent(mCtx, BooksListActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mCtx.startActivity(intent);
				}
			});

			return convertView;
        }
		
		private class ViewHolder {
			TextView text;
		}
	}
}
