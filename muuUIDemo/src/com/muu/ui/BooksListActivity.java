package com.muu.ui;

import java.util.ArrayList;
import java.util.Random;

import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;
import com.muu.db.DatabaseMgr.RECENT_HISTORY_COLUMN;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class BooksListActivity extends Activity {
	public static final String sListTypeKey = "list_type";
	public static final int sListCategory = 0;
	public static final int sListRecent = 1;
	public static final int sListSearch = 2;
	private static final int SENSOR_SHAKE = 10;
	
	public static String sCategoryIdx = "category_idx";
	
	private SensorManager mSensorMgr;
	private Vibrator mVibrator;
	private SensorEventListener mSensorListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list_layout);
		
		setupActionBar();
		updateViewsOnListType(getIntent().getIntExtra(sListTypeKey, 0));
		setupViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (mSensorMgr != null) {
			mSensorMgr.registerListener(mSensorListener,
					mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(mSensorListener);
		}
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
				BooksListActivity.this.finish();
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
						BooksListActivity.class);
				intent.putExtra(BooksListActivity.sListTypeKey,
						BooksListActivity.sListSearch);
				BooksListActivity.this.startActivity(intent);
			}
		});
	}
	
	private void updateViewsOnListType(int listType) {
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		Intent intent = getIntent();
		if (listType == sListCategory) {
			int categoryIdx = intent.getIntExtra(sCategoryIdx, 0);
			String categoryName = this.getResources().getStringArray(
			        R.array.category_text_array)[categoryIdx];
			backText.setText(categoryName);
			setupBooksList(getCategoryCartoons(categoryName));
			return;
		}
		
		if (listType == sListRecent) {
			backText.setText(R.string.recent_read);
			setupBooksList(getHistoryCartoons());
			return;
		}
		
		if (listType == sListSearch) {
			backText.setVisibility(View.INVISIBLE);
			TextView title = (TextView)this.findViewById(R.id.tv_action_title);
			title.setVisibility(View.VISIBLE);
			title.setText(R.string.search);
			
			ImageButton searchBtn = (ImageButton) this
					.findViewById(R.id.imbtn_search);
			searchBtn.setVisibility(View.INVISIBLE);
			
			setupBooksList(getRandomCartoons(3));
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
			EditText searchEdit = (EditText) this.findViewById(R.id.et_search);
			searchEdit.setVisibility(View.VISIBLE);
			searchEdit.setOnEditorActionListener(new EditActionListener());

			RelativeLayout searchHeader = (RelativeLayout) this
					.findViewById(R.id.rl_search_header);
			searchHeader.setVisibility(View.VISIBLE);
			dragMore.setVisibility(View.GONE);

			mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			mSensorListener = new ShakeListener();
		}
	}
	
	private void setupBooksList(ArrayList<CartoonInfo> list) {
		ListView listView = (ListView)this.findViewById(R.id.lv_books_list);
		BooksListAdapter listAdapter = new BooksListAdapter(this, list);
		listView.setAdapter(listAdapter);
	}
	
	private class BooksListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		ArrayList<CartoonInfo> mList;
		
		public BooksListAdapter(Context ctx, ArrayList<CartoonInfo> list) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mList = list;
		}
		
		@Override
        public int getCount() {
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
				convertView = mInflater.inflate(R.layout.book_list_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
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
				holder.name.setText(mList.get(position).name);
				holder.author.setText(getString(R.string.author,
						mList.get(position).author));
				holder.icon.setImageBitmap(new TempDataLoader()
						.getCartoonCover(mList.get(position).id));
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
			
			DatabaseMgr dbMgr = new DatabaseMgr(BooksListActivity.this);
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
			ImageView icon;
			TextView name;
			TextView author;
		}
	}
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				mVibrator.vibrate(300);
				Animation shake = AnimationUtils.loadAnimation(
						BooksListActivity.this, R.anim.shake);
				findViewById(R.id.imv_search_header).startAnimation(shake);

				setupBooksList(getRandomCartoons(3));
				break;
			}
		}
	};
	
	private class ShakeListener implements SensorEventListener {
		@Override
		public void onSensorChanged(SensorEvent event) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];
			int medumValue = 19;
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				 Message msg = new Message();
				 msg.what = SENSOR_SHAKE;
				 mHandler.sendMessage(msg);
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}
	
	private class EditActionListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH ||
	                actionId == EditorInfo.IME_ACTION_DONE ||
	                event.getAction() == KeyEvent.ACTION_DOWN &&
	                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	            onSearchAction(v);
	            return true;
	        }
			return false;
		}
	}
	
	private void onSearchAction(TextView v) {
		setupBooksList(getSearchedCartoons(v.getText().toString()));

		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	private ArrayList<CartoonInfo> getCategoryCartoons(String category) {
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Cursor cur = dbMgr.query(DatabaseMgr.MUU_CARTOONS_ALL_URL, null,
				String.format("%s=\'%s\'", CARTOONS_COLUMN.CATEGORY, category),
				null, null);
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
			list.add(new CartoonInfo(cur));
		}
		cur.close();
		dbMgr.closeDatabase();
		
		return list;
	}
	
	private ArrayList<CartoonInfo> getRandomCartoons(int count) {
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Cursor cur = dbMgr.query(DatabaseMgr.MUU_CARTOONS_ALL_URL, null, null,
				null, null);
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
		Random random = new Random(System.currentTimeMillis());
		while (list.size() < count) {
			int tmp = Math.abs(random.nextInt()) % cur.getCount();
			if (!cur.moveToPosition(tmp - 1)) {
				continue;
			}
			
			CartoonInfo info = new CartoonInfo(cur);
			if (!list.contains(info)) {
				list.add(info);
			}
		}
		
		return list;
	}
	
	private ArrayList<CartoonInfo> getSearchedCartoons(String searchStr) {
		DatabaseMgr dbMgr = new DatabaseMgr(this);
		Cursor cur = dbMgr.query(DatabaseMgr.MUU_CARTOONS_ALL_URL, null,
				CARTOONS_COLUMN.NAME + " LIKE \"%" + searchStr + "%\"", null,
				null);
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
			list.add(new CartoonInfo(cur));
		}
		cur.close();
		dbMgr.closeDatabase();
		
		return list;
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
			
			list.add(info);
		}
		cur.close();
		dbMgr.closeDatabase();
		
		return list;
	}
}
