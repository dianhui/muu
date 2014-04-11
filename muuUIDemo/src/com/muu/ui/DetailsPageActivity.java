package com.muu.ui;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsPageActivity extends Activity {
	public static final String sCartoonIdExtraKey = "cartoon_id";
	
	private static final String TAG = "DetailsPageActivity";
	private SlidingMenu mChaptersSlideView = null;
	private TextView mActionBarTitle = null;
	private int mCartoonId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_page_layout);
		mCartoonId = getIntent().getIntExtra(sCartoonIdExtraKey, -1);
		new CaculateCartoonSizeTask().execute(mCartoonId);

		setupActionBar();
		setupContentViews();
		setupCommentsView();
		
		new RetrieveChaptersTask().execute(mCartoonId);
	}
	
	private void setupActionBar() {
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DetailsPageActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.INVISIBLE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		mActionBarTitle = (TextView)this.findViewById(R.id.tv_action_title);
		mActionBarTitle.setVisibility(View.VISIBLE);
		
		ImageButton btnRecent = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		btnRecent.setVisibility(View.INVISIBLE);
		
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
	
	private void setupSlidingView(ArrayList<String> chapters) {
		mChaptersSlideView = new SlidingMenu(this);
		mChaptersSlideView.setMode(SlidingMenu.RIGHT);
		mChaptersSlideView.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mChaptersSlideView.setShadowWidthRes(R.dimen.shadow_width);
		mChaptersSlideView.setShadowDrawable(R.drawable.img_menu_shadow);
		mChaptersSlideView.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mChaptersSlideView.setFadeDegree(0.35f);
		mChaptersSlideView.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mChaptersSlideView.setMenu(R.layout.chapter_list_layout);

		ListView listView = (ListView) mChaptersSlideView
		        .findViewById(R.id.lv_chapters);
		listView.setDividerHeight(0);
		
		listView.setAdapter(new ChapterListAdapter(getApplicationContext(),
				chapters));
	}
	
	private void setupContentViews() {
		Button btnRead = (Button)this.findViewById(R.id.btn_read);
		btnRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DetailsPageActivity.this, ReadPageActivity.class);
				intent.putExtra(sCartoonIdExtraKey, mCartoonId);
				intent.putExtra(ReadPageActivity.sChapterIdxExtraKey, 1);
				intent.putExtra(ReadPageActivity.sPageIdxExtraKey, 1);
				DetailsPageActivity.this.startActivity(intent);
			}
		});
		
		ImageButton shareBtn = (ImageButton)this.findViewById(R.id.imv_btn_share);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
	            intent.setAction(Intent.ACTION_SEND);
	            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_sub));
				intent.putExtra(
						Intent.EXTRA_TEXT,
						getString(R.string.share_text));
	            intent.setType("text/plain");
	            List<ResolveInfo> list = DetailsPageActivity.this.getPackageManager().queryIntentActivities(
	                    intent, intent.getFlags());
	            if(list == null || list.size() == 0) {
	                Log.i(TAG, "No app handling share intent is found.");
	                Toast.makeText(DetailsPageActivity.this,
	                        getString(R.string.no_app_share),
	                        Toast.LENGTH_SHORT).show();
	            }
	            startActivity(intent);
			}
		});
		
		if (mCartoonId < 0) return;
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), mCartoonId));
		Cursor cur = dbMgr.query(uri, null, null, null, null);
		if (cur == null) return;
		if (cur.getCount() < 1) {
			cur.close();
			return;
		}
		
		CartoonInfo info = new CartoonInfo(cur);
		dbMgr.closeDatabase();
		ImageView imv = (ImageView)this.findViewById(R.id.imv_icon);
		Bitmap bmp = new TempDataLoader().getCartoonCover(info.id);
		imv.setImageBitmap(bmp);
		
		TextView tv = (TextView)this.findViewById(R.id.tv_category);
		tv.setText(getString(R.string.category, info.category));
		
		tv = (TextView)this.findViewById(R.id.tv_author);
		tv.setText(getString(R.string.author, info.author));
		
		tv = (TextView)this.findViewById(R.id.tv_update_time);
		tv.setText(getString(R.string.update_time, info.date));
		
//		tv = (TextView)this.findViewById(R.id.tv_size);
		
		tv = (TextView)this.findViewById(R.id.tv_introduction);
		tv.setText(info.abst);
		
		mActionBarTitle.setText(info.name);
	}
	
	private void setupCommentsView() {
		ArrayList<String> list = new TempDataLoader().getRandomComments(5);
		TextView tv = (TextView)this.findViewById(R.id.tv_comment_1);
		tv.setText(list.get(0));
		
		tv = (TextView)this.findViewById(R.id.tv_comment_2);
		tv.setText(list.get(1));
		
		tv = (TextView)this.findViewById(R.id.tv_comment_3);
		tv.setText(list.get(2));
		
		tv = (TextView)this.findViewById(R.id.tv_comment_4);
		tv.setText(list.get(3));
		
		tv = (TextView)this.findViewById(R.id.tv_comment_5);
		tv.setText(list.get(4));
	}
	
	private class ChapterListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		private ArrayList<String> mTextsList;
		
		public ChapterListAdapter(Context ctx, ArrayList<String> texts) {
			mCtx = ctx;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.chapter_list_item,
						null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.tv_chapter_text);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(mTextsList.get(position));
			convertView.setClickable(false);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(mCtx, ReadPageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(sCartoonIdExtraKey, mCartoonId);
					intent.putExtra(ReadPageActivity.sChapterIdxExtraKey,
							position + 1);
					intent.putExtra(ReadPageActivity.sPageIdxExtraKey, 1);
					mCtx.startActivity(intent);
				}
			});

			return convertView;
        }
		
		private class ViewHolder {
			TextView text;
		}
	}
	
	private class RetrieveChaptersTask extends
			AsyncTask<Integer, Integer, ArrayList<String>> {
		@Override
		protected ArrayList<String> doInBackground(Integer... params) {
			int cartoonId = params[0];
			TempDataLoader dataLoader = new TempDataLoader();
			return dataLoader.getChapters(cartoonId);
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			setupSlidingView(result);
		}
	}
	
	private class CaculateCartoonSizeTask extends AsyncTask<Integer, Integer, Float> {
		
		@Override
		protected Float doInBackground(Integer... params) {
			int cartoonId = params[0];
			return new TempDataLoader().getCartoonSize(cartoonId);
		}
		
		@Override
		protected void onPostExecute(Float result) {
			TextView tv = (TextView)DetailsPageActivity.this.findViewById(R.id.tv_size);
			tv.setVisibility(View.VISIBLE);
			tv.setText(getString(R.string.size, result));
		}
	}
}
