package com.muu.ui;

import java.util.ArrayList;

import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.RECENT_READ_COLUMN;
import com.muu.uidemo.R;
import com.muu.util.PkgMrgUtil;
import com.muu.util.ShareUtil;
import com.muu.util.TempDataLoader;
import com.muu.widget.TouchImageView;
import com.muu.widget.TouchImageView.OnGestureListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ReadPageActivity extends Activity implements OnGestureListener {
	public static final String sChapterIdxExtraKey = "chapter_idx";
	public static final String sPageIdxExtraKey = "page_idx";
	
	private RelativeLayout mActionBarLayout = null;
	private RelativeLayout mPageIdxLayout = null;
	private RelativeLayout mBottomLayout = null;
	private TouchImageView mContentImage = null;
	private Boolean mTouchedMode = false;
	private Boolean mRecomment = false;
	private View mShareDropView = null;
	private PopupWindow mSharePopup = null;
	private int mCartoonId = -1;
	private int mChapterIdx = -1;
	private int mPageIdx = -1;
	private int mPageCount = 0;
	private ArrayList<String> mChaptersList = null;
	private CartoonInfo mCartoonInfo = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_page_layout);

		mCartoonId = getIntent().getIntExtra(DetailsPageActivity.sCartoonIdExtraKey, -1);
		mChapterIdx = getIntent().getIntExtra(sChapterIdxExtraKey, 1);
		mPageIdx = getIntent().getIntExtra(sPageIdxExtraKey, 1);
		
		retrieveCartoonInfo();
		setupActionBar();
		setupContentView();
		setupBottomView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		recordReadHistory();
	}
	
	private void recordReadHistory() {
		ContentValues values = new ContentValues();
		values.put(RECENT_READ_COLUMN.CARTOON_ID, mCartoonId);
		values.put(RECENT_READ_COLUMN.CHAPTER_IDX, mChapterIdx);
		values.put(RECENT_READ_COLUMN.PAGE_IDX, mPageIdx);
		
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
		
		TextView tv = (TextView)this.findViewById(R.id.tv_title);
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
		
		mShareDropView = LayoutInflater.from(this).inflate(
				R.layout.share_drop_down_layout, null);
		ImageView imv = (ImageView) mShareDropView
				.findViewById(R.id.imv_sina_weibo);
		setupShareClicking(imv, PkgMrgUtil.SINA_WEIBO_PKG);
		if (isSinaWbInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		imv = (ImageView)mShareDropView.findViewById(R.id.imv_qq_weibo);
		setupShareClicking(imv, PkgMrgUtil.TENCENT_WEIBO_PKG);
		if (isQQWbInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		imv = (ImageView)mShareDropView.findViewById(R.id.imv_qq);
		setupShareClicking(imv, PkgMrgUtil.QQ_PKG);
		if (isQQInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		imv = (ImageView)mShareDropView.findViewById(R.id.imv_wechat);
		setupShareClicking(imv, PkgMrgUtil.WEIXIN_PKG);
		if (isWeichatInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		mSharePopup = new PopupWindow(mShareDropView,
		        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mSharePopup.setTouchable(true);
		mSharePopup.setOutsideTouchable(true);
		mSharePopup.setBackgroundDrawable(new ColorDrawable(0));
		
		shareBtn.setVisibility(View.VISIBLE);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharePopup.showAsDropDown(shareBtn, -28, 24);
			}
		});
		
	}
	
	private void setupShareClicking(ImageView imv, final String pkg) {
		imv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent shareIntent = ShareUtil.getShareIntent(ReadPageActivity.this, pkg);
				if (shareIntent == null) {
					return;
				}
				
				shareIntent.putExtra(Intent.EXTRA_SUBJECT,
						ReadPageActivity.this.getString(R.string.share_sub));
				shareIntent.putExtra(Intent.EXTRA_TEXT, ReadPageActivity.this
						.getString(R.string.share_text, mCartoonInfo.name));
				ReadPageActivity.this.startActivity(shareIntent);
			}
		});
	}

	private void setupContentView() {
		mContentImage = (TouchImageView)this.findViewById(R.id.imv_content);
		mContentImage.setSwipObserver(this);
		
		Bitmap bmp = new TempDataLoader().getCartoonImage(mCartoonId, mChapterIdx, mPageIdx);
		if (bmp != null) {
			mContentImage.setImageBitmap(bmp);
		}
	}
	
	private void setupBottomView() {
		new RetrieveChapterListTask().execute(mCartoonId);
		new RetrievePageCountTask().execute(mCartoonId, mChapterIdx);
		
		mPageIdxLayout = (RelativeLayout)this.findViewById(R.id.rl_page_index);
		TextView tv = (TextView)this.findViewById(R.id.tv_chapter);
		tv.setText(getString(R.string.chapter_idx_comma, mChapterIdx));
		
		mBottomLayout = (RelativeLayout)this.findViewById(R.id.rl_bottom_chapter);
		tv = (TextView)this.findViewById(R.id.tv_chapter_num);
		tv.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
	}
	
	@Override
	public void onSwipPrevious() {
		Bitmap bmp = getPreviousPage();
		if (bmp != null) {
			mContentImage.setImageBitmap(bmp);
		} else {
			Toast.makeText(getApplicationContext(),
					getString(R.string.it_is_start), Toast.LENGTH_LONG).show();
			return;
		}
		
		TextView tv = (TextView)this.findViewById(R.id.tv_chapter);
		tv.setText(getString(R.string.chapter_idx_comma, mChapterIdx));
		tv = (TextView)this.findViewById(R.id.tv_chapter_num);
		tv.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
		tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
		if (mChaptersList != null) {
			tv.setText(mChaptersList.get(mChapterIdx - 1));
		}
	}

	@Override
	public void onSwipNext() {
		Bitmap bmp = getNextPage();
		if (bmp != null) {
			mContentImage.setImageBitmap(bmp);
		} else {
			ReadFinishDialog dialog = new ReadFinishDialog(this,
					R.style.FloatDialogTheme, mCartoonId);
			dialog.show();
		}
		
		TextView tv = (TextView)this.findViewById(R.id.tv_chapter);
		tv.setText(getString(R.string.chapter_idx_comma, mChapterIdx));
		tv = (TextView)this.findViewById(R.id.tv_chapter_num);
		tv.setText(getString(R.string.chapter_idx_dot, mChapterIdx));
		tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
		if (mChaptersList != null) {
			tv.setText(mChaptersList.get(mChapterIdx - 1));
		}
	}
	
	private Bitmap getNextPage() {
		TempDataLoader dataLoader = new TempDataLoader();
		if (mPageIdx < mPageCount - 1) {
			mPageIdx++;
			TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_page_idx);
			tv.setText(getString(R.string.page, mPageIdx, mPageCount));
			tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
			tv.setText(getString(R.string.page, mPageIdx, mPageCount));
			
			return dataLoader.getCartoonImage(mCartoonId, mChapterIdx,
					mPageIdx);
		}
		
		if (mChaptersList != null && mChapterIdx < mChaptersList.size() - 1) {
			mChapterIdx++;
			new RetrievePageCountTask().execute(mCartoonId, mChapterIdx);
			mPageIdx = 1;
			return dataLoader.getCartoonImage(mCartoonId, mChapterIdx, mPageIdx);
		}
		
		return null;
	}

	private Bitmap getPreviousPage() {
		TempDataLoader dataLoader = new TempDataLoader();
		if (mPageIdx > 1) {
			mPageIdx--;
			TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_page_idx);
			tv.setText(getString(R.string.page, mPageIdx, mPageCount));
			tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
			tv.setText(getString(R.string.page, mPageIdx, mPageCount));
			return dataLoader.getCartoonImage(mCartoonId, mChapterIdx,
					mPageIdx);
		}
		
		if (mChapterIdx > 1) {
			mChapterIdx--;
			new RetrievePageCountTask().execute(mCartoonId, mChapterIdx);
			
			//TODO: remember page count of previous chapter.
			mPageIdx = 1;
			return dataLoader.getCartoonImage(mCartoonId, mChapterIdx, mPageIdx);
		}
		
		return null;
	}
	
	@Override
	public void onSingleTapUp() {
		if (mTouchedMode) {
			mActionBarLayout.setVisibility(View.VISIBLE);
			mPageIdxLayout.setVisibility(View.GONE);
			mBottomLayout.setVisibility(View.VISIBLE);
        } else {
        	mActionBarLayout.setVisibility(View.GONE);
    		mPageIdxLayout.setVisibility(View.VISIBLE);
    		mBottomLayout.setVisibility(View.GONE);
        }
		mTouchedMode = !mTouchedMode;
	}
	
	private class RetrieveChapterListTask extends
			AsyncTask<Integer, Integer, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
			tv.setVisibility(View.GONE);
		}
		
		@Override
		protected ArrayList<String> doInBackground(Integer... params) {
			int cartoonId = params[0];
			TempDataLoader dataLoader = new TempDataLoader();
			mChaptersList = dataLoader.getChapters(cartoonId);
			
			return mChaptersList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_name);
			tv.setVisibility(View.VISIBLE);
			tv.setText(mChaptersList.get(mChapterIdx - 1));
		}
	}
	
	private class RetrievePageCountTask extends
			AsyncTask<Integer, Integer, Integer> {
		@Override
		protected void onPreExecute() {
			TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_page_idx);
			tv.setVisibility(View.GONE);
			
			tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
			tv.setVisibility(View.GONE);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int cartoonId = params[0];
			int chapterId = params[1];
			TempDataLoader dataLoader = new TempDataLoader();
			mPageCount = dataLoader.getChatperImgCount(cartoonId, chapterId);
			return mPageCount;
		}

		@Override
		protected void onPostExecute(Integer result) {
			TextView tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_page_idx);
			tv.setVisibility(View.VISIBLE);
			tv.setText(getString(R.string.page, mPageIdx, result));
			
			tv = (TextView)ReadPageActivity.this.findViewById(R.id.tv_chapter_page_idx);
			tv.setVisibility(View.VISIBLE);
			tv.setText(getString(R.string.page, mPageIdx, result));
		}
	}
}
