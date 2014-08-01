package com.muu.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muu.data.CartoonInfo;
import com.muu.data.ChapterInfo;
import com.muu.data.ImageInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.RECENT_READ_COLUMN;
import com.muu.server.MuuServerWrapper;
import com.muu.uidemo.R;
import com.muu.util.PkgMrgUtil;
import com.muu.util.ShareUtil;
import com.muu.util.TempDataLoader;
import com.muu.widget.TouchImageView;
import com.muu.widget.TouchImageView.OnGestureListener;

public class ReadPageActivity extends Activity implements OnGestureListener {
	public static final String sChapterIdxExtraKey = "chapter_idx";
	public static final String sPageIdxExtraKey = "page_idx";
	
	private RelativeLayout mActionBarLayout = null;
	private RelativeLayout mBottomLayout = null;
	private TouchImageView mContentImage = null;
	private Boolean mTouchedMode = false;
	private Boolean mRecomment = false;
	private int mCartoonId = -1;
	private int mChapterIdx = -1;
	private int mPageIdx = -1;
	private ArrayList<ChapterInfo> mChaptersList = null;
	private CartoonInfo mCartoonInfo = null;
	private SparseArray<ArrayList<ImageInfo>> mChpImgInfoArray;
	private ProgressBar mProgressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_page_layout);

		mCartoonId = getIntent().getIntExtra(DetailsPageActivity.sCartoonIdExtraKey, -1);
		mChapterIdx = getIntent().getIntExtra(sChapterIdxExtraKey, 0);
		mPageIdx = getIntent().getIntExtra(sPageIdxExtraKey, 0);
		
		retrieveCartoonInfo();
		setupActionBar();
		setupContentView();
		setupBottomView();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		recordReadHistory();
	}
	
	private void recordReadHistory() {
		ContentValues values = new ContentValues();
		values.put(RECENT_READ_COLUMN.CARTOON_ID, mCartoonId);
		values.put(RECENT_READ_COLUMN.CHAPTER_IDX, mChapterIdx);
		values.put(RECENT_READ_COLUMN.PAGE_IDX, mPageIdx);
		values.put(RECENT_READ_COLUMN.READ_DATE, System.currentTimeMillis());
		
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Cursor cursor = dbMgr.query(DatabaseMgr.RECENT_READ_ALL_URL, null, RECENT_READ_COLUMN.CARTOON_ID+"="+mCartoonId, null, null);
		if (cursor == null) {
			dbMgr.insert(DatabaseMgr.RECENT_READ_ALL_URL, values);
			return;
		}
		
		if (cursor.moveToFirst()) {
			dbMgr.update(DatabaseMgr.RECENT_READ_ALL_URL, values, RECENT_READ_COLUMN.CARTOON_ID+"="+mCartoonId, null);
		} else {
			dbMgr.insert(DatabaseMgr.RECENT_READ_ALL_URL, values);
		}
		
		cursor.close();
		dbMgr.closeDatabase();
	}

	private void retrieveCartoonInfo() {
		if (mCartoonId == -1) {
			Log.d("ReadPageActivity", "Input cartoon id is not available.");
			return;
		}
		
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), mCartoonId));
		Cursor cur = dbMgr.query(uri, null, null, null, null);
		if (cur == null) return;
		
		if (!cur.moveToFirst()) {
			cur.close();
			return;
		}
		
		mCartoonInfo = new CartoonInfo(cur);
		cur.close();
		dbMgr.closeDatabase();
	}
	
	private void setupActionBar() {
		mActionBarLayout = (RelativeLayout)this.findViewById(R.id.rl_action_bar);
		RelativeLayout backBtn = (RelativeLayout)this.findViewById(R.id.rl_back);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ReadPageActivity.this.finish();
			}
		});
		
		TextView tv = (TextView)this.findViewById(R.id.tv_back_text);
		tv.setText(mCartoonInfo.name);
		
		final ImageButton recommentBtn = (ImageButton)this.findViewById(R.id.imv_btn_recomment);
		recommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecomment = !mRecomment;
				
				if (mRecomment) {
					recommentBtn.setBackgroundResource(R.drawable.ic_recomment_selected);
				} else {
					recommentBtn.setBackgroundResource(R.drawable.ic_recomment_normal);
				}
			}
		});
		
		setupDropdownView();
	}
	
	private void setupDropdownView() {
		boolean isSinaWbInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.SINA_WEIBO_PKG);
		boolean isQQWbInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.TENCENT_WEIBO_PKG);
		boolean isQQInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.QQ_PKG);
		boolean isWeichatInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.WEIXIN_PKG);

		final ImageButton shareBtn = (ImageButton) this
				.findViewById(R.id.imv_btn_share);
		if (!isSinaWbInstalled && !isQQWbInstalled && !isQQInstalled
				&& !isWeichatInstalled) {
			shareBtn.setVisibility(View.GONE);
			return;
		}
		
		shareBtn.setVisibility(View.VISIBLE);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareUtil.onShareClicked(ReadPageActivity.this, mCartoonInfo.name);
			}
		});
		
	}

	private void setupContentView() {
		mContentImage = (TouchImageView)this.findViewById(R.id.imv_content);
		mContentImage.setSwipObserver(this);
		
		mProgressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
		Bitmap bmp = new TempDataLoader().getCartoonImage(mCartoonId, mChapterIdx, mPageIdx);
		if (bmp != null) {
			mContentImage.setImageBitmap(bmp);
		}
	}
	
	private void setupBottomView() {
		mChaptersList = getChapterInfo(mCartoonId);
		new RetrieveCartoonImgTask().execute(mChapterIdx, mPageIdx);
		
		TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
		tv.setVisibility(View.VISIBLE);
		tv.setText(mChaptersList.get(mChapterIdx).name);
		
		tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
		tv.setVisibility(View.VISIBLE);
		tv.setText(getString(R.string.page, mPageIdx + 1, mChaptersList.get(mChapterIdx).pageCount));

		mBottomLayout = (RelativeLayout)this.findViewById(R.id.rl_bottom_chapter);
		tv = (TextView)this.findViewById(R.id.tv_chapter_num);
		tv.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
	}
	
	@Override
	public void onSwipPrevious() {
		TextView tvChapterPageIdx = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
		TextView tvChapterNum = (TextView)this.findViewById(R.id.tv_chapter_num);
		TextView tvChapterName = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
		if (mPageIdx >= 1) {
			mPageIdx--;
			
			new RetrieveCartoonImgTask().execute(mChapterIdx, mPageIdx);
			tvChapterPageIdx.setText(getString(R.string.page, mPageIdx + 1, mChaptersList.get(mChapterIdx).pageCount));
			tvChapterNum.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
			tvChapterName.setText(mChaptersList.get(mChapterIdx).name);
			return;
		}
		
		if (mChapterIdx >= 1) {
			mChapterIdx--;
			
			mPageIdx = mChaptersList.get(mChapterIdx).pageCount - 1;
			new RetrieveCartoonImgTask().execute(mChapterIdx, mPageIdx);
			
			tvChapterPageIdx.setText(getString(R.string.page, mPageIdx + 1, mChaptersList.get(mChapterIdx).pageCount));
			tvChapterNum.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
			tvChapterName.setText(mChaptersList.get(mChapterIdx).name);
			return;
		}
		
		Toast.makeText(getApplicationContext(),
				getString(R.string.it_is_start), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSwipNext() {
		TextView tvChapterPageIdx = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
		TextView tvChapterNum = (TextView)this.findViewById(R.id.tv_chapter_num);
		TextView tvChapterName = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
		
		if (mPageIdx < mChaptersList.get(mChapterIdx).pageCount - 1) {
			mPageIdx++;
			new RetrieveCartoonImgTask().execute(mChapterIdx, mPageIdx);
			tvChapterPageIdx.setText(getString(R.string.page, mPageIdx + 1, mChaptersList.get(mChapterIdx).pageCount));
			tvChapterNum.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
			tvChapterName.setText(mChaptersList.get(mChapterIdx).name);
			return;
		}
		
		if (mChaptersList != null && mChapterIdx < mChaptersList.size() - 1) {
			mChapterIdx++;
			mPageIdx = 0;
			new RetrieveCartoonImgTask().execute(mChapterIdx, mPageIdx); 
			tvChapterPageIdx.setText(getString(R.string.page, mPageIdx + 1, mChaptersList.get(mChapterIdx).pageCount));
			tvChapterNum.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
			tvChapterName.setText(mChaptersList.get(mChapterIdx).name);
			return;
		}
		
		ReadFinishDialog dialog = new ReadFinishDialog(this,
				R.style.FloatDialogTheme, mCartoonId);
		dialog.show();
	}
	
	@Override
	public void onSingleTapUp() {
		if (mTouchedMode) {
			mActionBarLayout.setVisibility(View.VISIBLE);
			mBottomLayout.setVisibility(View.VISIBLE);
        } else {
        	mActionBarLayout.setVisibility(View.GONE);
    		mBottomLayout.setVisibility(View.GONE);
        }
		mTouchedMode = !mTouchedMode;
	}
	
	private ArrayList<ChapterInfo> getChapterInfo(int cartoonId) {
		TempDataLoader dataLoader = new TempDataLoader();
		return dataLoader.getChapters(ReadPageActivity.this, cartoonId);
	}
	
	private class RetrieveCartoonImgTask extends AsyncTask<Integer, Integer, Bitmap> {
		private MuuServerWrapper mServerWrapper;

		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Bitmap doInBackground(Integer... params) {
			if (mChpImgInfoArray == null) {
				mChpImgInfoArray = new SparseArray<ArrayList<ImageInfo>>();
			}
			
			if (mServerWrapper == null) {
				mServerWrapper = new MuuServerWrapper(getApplicationContext());
			}
			
			int chapterIdx = params[0];
			int imgIdx = params[1];
			ChapterInfo chapter = mChaptersList.get(chapterIdx);
			ArrayList<ImageInfo> chapterImgInfo = mChpImgInfoArray.get(chapter.id);
			if (chapterImgInfo == null) {
				chapterImgInfo = mServerWrapper.getChapterImgInfo(chapter.id,
						0, chapter.pageCount);
				mChpImgInfoArray.append(chapter.id, chapterImgInfo);
			}

			if (imgIdx >= chapterImgInfo.size()) {
				return null;
			}
			
			return mServerWrapper.getImageByImageInfo(mCartoonId, chapterImgInfo.get(imgIdx));
		}
		
		@Override
		protected void onPostExecute(Bitmap bmp) {
			mProgressBar.setVisibility(View.GONE);
			if (bmp == null) return;
			
			mContentImage.setImageBitmap(bmp);
		}
	}
}
