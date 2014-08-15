package com.muu.ui;

import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;
import com.muu.service.DownloadMgr;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OfflineReadActivity extends Activity {
	private static final int IDX_DOWNLOADING_TAB = 0;
	private static final int IDX_DOWNLOADED_TAB = 1;
	
	private static final int ENTER_EDIT_MODE = 0;
	private static final int EXIT_EDIT_MODE = 1;
	private static final int REFRESH_UI = 2;
	
	public static final String sCartoonIdExtraKey = "cartoon_id";
	
	private Button mDownloadingBtn = null;
	private Button mDownloadedBtn = null;
	private GridView mDownloadingGdView = null;
	private GridView mDownloadedGdView = null;
	
	private DownloadContentObserver mContentObserver;
	
	private Boolean mIsEditMode = false;
	private int mCurrentTabIndex = -1;
    private Handler mHandler;
    private Handler mPercentHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_read_activity_layout);
		
		setupActionBar();
		
		mDownloadingBtn = (Button)this.findViewById(R.id.btn_downloading);
		mDownloadingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentTab(IDX_DOWNLOADING_TAB);
			}
		});
		
		mDownloadedBtn = (Button)this.findViewById(R.id.btn_downloaded);
		mDownloadedBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentTab(IDX_DOWNLOADED_TAB);
			}
		});
		
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.arg1) {
				case ENTER_EDIT_MODE:
					setupDownloadingView();
					setupDownloadedView();
					break;
				case EXIT_EDIT_MODE:
					setupDownloadingView();
					setupDownloadedView();
					break;
				case REFRESH_UI:
					setupDownloadingView();
					setupDownloadedView();
				default:
					break;
				}
			}
		};
		mPercentHandler = new Handler();
		mContentObserver = new DownloadContentObserver(mPercentHandler);
		
		setupDownloadingView();
		setupDownloadedView();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		getContentResolver().registerContentObserver(
				DatabaseMgr.MUU_CARTOONS_ALL_URL, false, mContentObserver);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		getContentResolver().unregisterContentObserver(mContentObserver);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (!mIsEditMode) {
	    		return super.onKeyDown(keyCode, event);
			}
	    	
	    	mIsEditMode = false;
    		mHandler.sendEmptyMessage(EXIT_EDIT_MODE);
    		return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
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
				OfflineReadActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.INVISIBLE);
		
		ImageButton recentButton = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SearchActivity.class);
				OfflineReadActivity.this.startActivity(intent);
			}
		});
		
		TextView title = (TextView)this.findViewById(R.id.tv_action_title);
		title.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.offline_read));
	}
	
	private void setupDownloadingView() {
		mDownloadingGdView = (GridView)this.findViewById(R.id.gv_downloading);
		
		Cursor cur = new DatabaseMgr(getApplicationContext()).query(DatabaseMgr.MUU_CARTOONS_ALL_URL,
				null, CARTOONS_COLUMN.IS_DOWNLOAD + "=1 and "
						+ CARTOONS_COLUMN.DOWNLOAD_PROGRESS + "<100", null,
				null);
		if (cur == null) {
			return;
		}
		
		if (cur.getCount() < 1) {
			cur.close();
			return;
		}
		
		mDownloadingGdView.setAdapter(new DownloadingAdpter(getApplicationContext(), cur, true));
	}

	private void setupDownloadedView() {
		if (mDownloadedGdView == null) {
			mDownloadedGdView = (GridView)this.findViewById(R.id.gv_downloaded);
		} else {
			mDownloadedGdView.setAdapter(null);
		}
		
		Cursor cur = new DatabaseMgr(getApplicationContext()).query(DatabaseMgr.MUU_CARTOONS_ALL_URL,
				null, CARTOONS_COLUMN.IS_DOWNLOAD + "=1 and "
						+ CARTOONS_COLUMN.DOWNLOAD_PROGRESS + "=100", null,
				null);
		if (cur == null) {
			return;
		}
		
		if (cur.getCount() < 1) {
			cur.close();
			return;
		}
		
		mDownloadedGdView.setAdapter(new DownloadedAdpter(getApplicationContext(), cur, true));
	}
	
	private void setCurrentTab(int tabIndx) {
		if (tabIndx == -1) {
			tabIndx = IDX_DOWNLOADING_TAB;
		}
		
		if (tabIndx == mCurrentTabIndex) {
			return;
		}
		
		mCurrentTabIndex = tabIndx;
		switch (tabIndx) {
		case IDX_DOWNLOADING_TAB:
			mDownloadingGdView.setVisibility(View.VISIBLE);
			mDownloadedGdView.setVisibility(View.INVISIBLE);
			
			mDownloadingGdView.setAdapter(null);
        	setupDownloadingView();
			
			mDownloadingBtn.setBackgroundResource(R.drawable.tabs_selected_default);
			mDownloadedBtn.setBackgroundResource(R.drawable.tabs_unselected_default);
			break;
		case IDX_DOWNLOADED_TAB:
			mDownloadingGdView.setVisibility(View.INVISIBLE);
			mDownloadedGdView.setVisibility(View.VISIBLE);
			
			mDownloadedGdView.setAdapter(null);
        	setupDownloadedView();
			
			mDownloadingBtn.setBackgroundResource(R.drawable.tabs_unselected_default);
			mDownloadedBtn.setBackgroundResource(R.drawable.tabs_selected_default);
			break;
		default:
			break;
		}
	}
	
	private class DownloadingAdpter extends CursorAdapter {
//		private Context mCtx;
		private LayoutInflater mInflater;
		
		public DownloadingAdpter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			
//			mCtx = context.getApplicationContext();
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, final Context ctx, Cursor cursor) {
			final int cartoonId = cursor.getInt(cursor.getColumnIndex(CARTOONS_COLUMN.ID));
			Bitmap cover = new TempDataLoader().getCartoonCover(cartoonId).get();
			if (cover != null) {
				ImageView coverImv = (ImageView)view.findViewById(R.id.imv_icon);
				coverImv.setImageBitmap(cover);
			}
			
			String cartoonName = cursor.getString(cursor.getColumnIndex(CARTOONS_COLUMN.NAME));
			TextView tv = (TextView)view.findViewById(R.id.tv_name);
			tv.setText(cartoonName);
			
			ImageView imv = (ImageView)view.findViewById(R.id.imv_remove);
			if (mIsEditMode) {
				imv.setVisibility(View.VISIBLE);
			} else {
				imv.setVisibility(View.GONE);
			}
			
			imv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							DownloadMgr.getInstanse().cancel(cartoonId);
							TempDataLoader.removeDownloadedCartoon(ctx, cartoonId);
							mHandler.sendEmptyMessage(REFRESH_UI);
						}
					}).start();
				}
			});
			
			int percent = cursor.getInt(cursor.getColumnIndex(CARTOONS_COLUMN.DOWNLOAD_PROGRESS));
			tv = (TextView)view.findViewById(R.id.tv_percent);
			tv.setText(percent+"%");
			
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					mIsEditMode = true;
					mHandler.sendEmptyMessage(ENTER_EDIT_MODE);
					return true;
				}
			});
		}

		@Override
		public View newView(Context ctx, Cursor cursor, ViewGroup viewGroup) {
			RelativeLayout layout = null;
			layout = (RelativeLayout)mInflater.inflate(R.layout.download_item_layout, null);
			layout.setClickable(false);
			return layout;
		}
	}
	
	private class DownloadedAdpter extends CursorAdapter implements RecyclerListener {
//		private Context mCtx;
		private LayoutInflater mInflater;
		
		public DownloadedAdpter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			
//			mCtx = context.getApplicationContext();
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, final Context ctx, Cursor cursor) {
			final int cartoonId = cursor.getInt(cursor.getColumnIndex(CARTOONS_COLUMN.ID));
			Bitmap cover = new TempDataLoader().getCartoonCover(cartoonId).get();
			if (cover != null) {
				ImageView coverImv = (ImageView)view.findViewById(R.id.imv_icon);
				coverImv.setImageBitmap(cover);
			}
			
			final String cartoonName = cursor.getString(cursor.getColumnIndex(CARTOONS_COLUMN.NAME));
			TextView tv = (TextView)view.findViewById(R.id.tv_name);
			tv.setText(cartoonName);
			
			tv = (TextView)view.findViewById(R.id.tv_percent);
			tv.setVisibility(View.GONE);
			
			ImageView imv = (ImageView)view.findViewById(R.id.imv_remove);
			if (mIsEditMode) {
				imv.setVisibility(View.VISIBLE);
			} else {
				imv.setVisibility(View.GONE);
			}
			
			imv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							TempDataLoader.removeDownloadedCartoon(ctx, cartoonId);
							mHandler.sendEmptyMessage(REFRESH_UI);
						}
					}).start();
				}
			});
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mIsEditMode) {
						return;
					}
					startReadActivity(cartoonId, 0, 0);
				}
			});
			
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					mIsEditMode = true;
					mHandler.sendEmptyMessage(ENTER_EDIT_MODE);
					return true;
				}
			});
			
		}

		@Override
		public View newView(Context ctx, Cursor cursor, ViewGroup viewGroup) {
			RelativeLayout layout = null;
			layout = (RelativeLayout)mInflater.inflate(R.layout.download_item_layout, null);
			return layout;
		}

		@Override
		public void onMovedToScrapHeap(View view) {
			ImageView imv = (ImageView)view.findViewById(R.id.imv_icon);
			recycleImvBmp(imv);
		}
		
		private void recycleImvBmp(ImageView imv) {
			if (imv == null) {
				return;
			}
			
			Drawable drawable = (Drawable) imv.getDrawable();
			if (drawable != null && drawable instanceof BitmapDrawable) {
				BitmapDrawable bmpDrawable = (BitmapDrawable)drawable;
				if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
					bmpDrawable.getBitmap().recycle();
				}
			}
			
			imv = null;
		}
		
		private void startReadActivity(int cartoonId, int chapterIdx, int pageIdx) {
			Intent intent = new Intent();
			intent.setClass(OfflineReadActivity.this, ReadPageActivity.class);
			intent.putExtra(sCartoonIdExtraKey, cartoonId);
			intent.putExtra(ReadPageActivity.sChapterIdxExtraKey, chapterIdx);
			intent.putExtra(ReadPageActivity.sPageIdxExtraKey, pageIdx);
			OfflineReadActivity.this.startActivity(intent);
		}
	}
	
	private class DownloadContentObserver extends ContentObserver {
		public DownloadContentObserver(Handler handler) {
			super(handler);
		}
		
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
        	mDownloadingGdView.setAdapter(null);
        	setupDownloadingView();
		}
	}
}
