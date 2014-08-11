package com.muu.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.data.CartoonInfo;
import com.muu.data.ChapterInfo;
import com.muu.data.Comment;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.RECENT_HISTORY_COLUMN;
//import com.muu.db.DatabaseMgr.RECENT_HISTORY_COLUMN;
import com.muu.server.MuuServerWrapper;
import com.muu.service.DownloadMgr;
import com.muu.service.DownloadMgr.DownloadStatus;
import com.muu.uidemo.R;
import com.muu.util.ShareUtil;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
//import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsPageActivity extends Activity {
	public static final String sCartoonIdExtraKey = "cartoon_id";
	
	private SlidingMenu mChaptersSlideView = null;
	private TextView mActionBarTitle = null;
	private ImageView mCoverImageView = null;
	private int mCartoonId = -1;
	private int mChapterCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_page_layout);
		mCartoonId = getIntent().getIntExtra(sCartoonIdExtraKey, -1);

		setupActionBar();
		setupContentViews();
		new RetrieveChaptersTask().execute(mCartoonId);
		new RetrieveCommentsTask().execute();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setupReadButton();
	}
	
	@Override
	protected void onDestroy() {
		BitmapDrawable bmpDrawable = (BitmapDrawable)mCoverImageView.getDrawable();
		if (bmpDrawable != null && bmpDrawable instanceof BitmapDrawable) {
			bmpDrawable.getBitmap().recycle();
			mCoverImageView = null;
		}
		
		super.onDestroy();
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
				if (mChaptersSlideView != null) {
					mChaptersSlideView.toggle();
				}
			}
		});
	}
	
	private void setupSlidingView(ArrayList<ChapterInfo> chapters) {
		mChaptersSlideView = new SlidingMenu(this);
		mChaptersSlideView.setMode(SlidingMenu.RIGHT);
		mChaptersSlideView.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mChaptersSlideView.setShadowWidthRes(R.dimen.chapter_shadow_width);
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
		RelativeLayout introLayout = (RelativeLayout)this.findViewById(R.id.rl_intro);
		introLayout.setOnClickListener(new OnClickListener() {
			Boolean ellipSizable = true;

			@Override
			public void onClick(View v) {
				TextView tv = (TextView)DetailsPageActivity.this.findViewById(R.id.tv_introduction);
				ImageButton moreBtn = (ImageButton)DetailsPageActivity.this.findViewById(R.id.imv_btn_more);
				
				if (ellipSizable) {
					ellipSizable = false;
					tv.setEllipsize(null);
					tv.setMaxLines(Integer.MAX_VALUE);
					moreBtn.setVisibility(View.INVISIBLE);
				} else {
					ellipSizable = true;
					tv.setEllipsize(TextUtils.TruncateAt.END);
					tv.setMaxLines(3);
					moreBtn.setVisibility(View.VISIBLE);
				}
			}
		});
		
		TextView shareBtn = (TextView) this.findViewById(R.id.tv_share);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareUtil.onShareClicked(DetailsPageActivity.this,
						mActionBarTitle.getText().toString());
			}
		});
		
		TextView downloadBtn = (TextView) this.findViewById(R.id.tv_download);
		downloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DownloadStatus status = new DownloadMgr().download(getApplicationContext(), mCartoonId);
				switch (status) {
				case OK:
					Toast.makeText(getApplicationContext(),
							getString(R.string.download_started),
							Toast.LENGTH_LONG).show();
					break;
				case DOWNLOADING:
					Toast.makeText(getApplicationContext(),
							getString(R.string.download_in_process),
							Toast.LENGTH_LONG).show();
					break;
				case DOWANLOADED:
					Toast.makeText(getApplicationContext(),
							getString(R.string.download_done),
							Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		});
		
		if (mCartoonId < 0) return;
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), mCartoonId));
		Cursor cur = dbMgr.query(uri, null, null, null, null);
		if (cur == null) return;
		if (!cur.moveToFirst()) {
			cur.close();
			return;
		}
		
		CartoonInfo info = new CartoonInfo(cur);
		cur.close();
		dbMgr.closeDatabase();
		mChapterCount = info.chapterCount;
		mCoverImageView = (ImageView)this.findViewById(R.id.imv_icon);
		WeakReference<Bitmap> bmpRef = new TempDataLoader().getCartoonCover(info.id);
		if (bmpRef != null && bmpRef.get() != null) {
			mCoverImageView.setImageBitmap(bmpRef.get());
		}
		
		ImageView imv = (ImageView)this.findViewById(R.id.imv_status);
		int resId = info.isComplete == 0 ? R.drawable.ic_status_continue
				: R.drawable.ic_status_complete;
		imv.setImageResource(resId);
		
		TextView tv = (TextView)this.findViewById(R.id.tv_category);
		tv.setText(getString(R.string.category, TempDataLoader.getTopicString(info.topicCode)));
		
		tv = (TextView)this.findViewById(R.id.tv_author);
		tv.setText(getString(R.string.author, info.author));
		
		tv = (TextView)this.findViewById(R.id.tv_update_time);
		tv.setText(getString(R.string.update_time,
				info.updateDate.substring(0, info.updateDate.indexOf(' '))));
		
		tv = (TextView)this.findViewById(R.id.tv_size);
		tv.setText(getString(R.string.size, info.size/(1024.0f*1024.0f)));
		
		tv = (TextView)this.findViewById(R.id.tv_introduction);
		tv.setText(info.abst);
		
		mActionBarTitle.setText(info.name);
	}
	
	private void setupReadButton() {
		DatabaseMgr dbMgr = new DatabaseMgr(DetailsPageActivity.this);
		Cursor cursor = getHistoryCursor(dbMgr, mCartoonId);
		int chapterIdx = 0;
		int pageIdx = 0;
		if (cursor != null && cursor.moveToFirst()) {
			chapterIdx = cursor.getInt(cursor.getColumnIndex(RECENT_HISTORY_COLUMN.CHAPTER_IDX));
			pageIdx = cursor.getInt(cursor.getColumnIndex(RECENT_HISTORY_COLUMN.PAGE_IDX));
			
			cursor.close();
		}
		
		final int finalChapterIdx = chapterIdx;
		final int finalPageIdx = pageIdx;
		ImageView imv = (ImageView)this.findViewById(R.id.imv_icon);
		imv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startReadActivity(finalChapterIdx, finalPageIdx);
			}
		});
		
		Button btnRead = (Button)this.findViewById(R.id.btn_read);
		btnRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startReadActivity(finalChapterIdx, finalPageIdx);
			}
		});
	}
	
	private void startReadActivity(int chapterIdx, int pageIdx) {
		Intent intent = new Intent();
		intent.setClass(DetailsPageActivity.this, ReadPageActivity.class);
		intent.putExtra(sCartoonIdExtraKey, mCartoonId);
		intent.putExtra(ReadPageActivity.sChapterIdxExtraKey, chapterIdx);
		intent.putExtra(ReadPageActivity.sPageIdxExtraKey, pageIdx);
		DetailsPageActivity.this.startActivity(intent);
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
	
	private void setupCommentsView(ArrayList<Comment> commentList) {
		if (commentList == null) {
			return;
		}
		
		int maxComments = commentList.size() <= 5 ? commentList.size() : 5;
		
		Resources res = this.getResources();
		for (int i = 0; i < maxComments; i++) {
			int resId = res.getIdentifier(
					String.format("tv_comment_%d", i+1), "id",
					this.getPackageName());
			
			TextView tv = (TextView)this.findViewById(resId);
			tv.setText(commentList.get(i).content);
		}
	}
	
	private class ChapterListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		private ArrayList<ChapterInfo> mChapters;
		
		public ChapterListAdapter(Context ctx, ArrayList<ChapterInfo> chapters) {
			mCtx = ctx;
			mInflater = LayoutInflater.from(ctx);
			mChapters = chapters;
		}
		
		@Override
        public int getCount() {
	        return mChapters.size();
        }

		@Override
        public Object getItem(int position) {
	        return mChapters.get(position);
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

			holder.text.setText(String.format("%d. %s", position + 1,
					mChapters.get(position).name));
			convertView.setClickable(false);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(mCtx, ReadPageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(sCartoonIdExtraKey, mCartoonId);
					intent.putExtra(ReadPageActivity.sChapterIdxExtraKey,
							position);
					intent.putExtra(ReadPageActivity.sPageIdxExtraKey, 0);
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
			AsyncTask<Integer, Integer, ArrayList<ChapterInfo>> {
		@Override
		protected ArrayList<ChapterInfo> doInBackground(Integer... params) {
			return new MuuServerWrapper(getApplicationContext())
					.getChapterInfo(mCartoonId, 0, mChapterCount);
		}
		
		@Override
		protected void onPostExecute(ArrayList<ChapterInfo> result) {
			setupSlidingView(result);
		}
	}
	
	private class RetrieveCommentsTask extends
			AsyncTask<Void, Integer, ArrayList<Comment>> {

		@Override
		protected ArrayList<Comment> doInBackground(Void... params) {
			return new MuuServerWrapper(getApplicationContext()).getComments(
					mCartoonId, 0, 5);
		}
		
		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			setupCommentsView(result);
		}

	}
}
