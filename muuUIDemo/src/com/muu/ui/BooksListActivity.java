package com.muu.ui;

import com.muu.uidemo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
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
		setupViews();
		getListData();
		setupBooksList();
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
			EditText searchEdit = (EditText) this.findViewById(R.id.et_search);
			searchEdit.setVisibility(View.VISIBLE);
			searchEdit.setOnEditorActionListener(new EditActionListener());

			RelativeLayout searchHeader = (RelativeLayout) this
					.findViewById(R.id.rl_search_header);
			searchHeader.setVisibility(View.VISIBLE);

			RelativeLayout shakeChange = (RelativeLayout) this
					.findViewById(R.id.rl_shake_change);
			shakeChange.setVisibility(View.VISIBLE);

			dragMore.setVisibility(View.GONE);

			mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			mSensorListener = new ShakeListener();
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
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				mVibrator.vibrate(300);
				Animation shake = AnimationUtils.loadAnimation(
						BooksListActivity.this, R.anim.shake);
				findViewById(R.id.rl_shake_change).startAnimation(shake);

				//TODO: change list.
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
		Toast.makeText(this, v.getText().toString(), Toast.LENGTH_LONG).show();
		 
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}
