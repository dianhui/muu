package com.muu.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.COMMENTS_COLUMN;
import com.muu.server.MuuServerWrapper;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends Activity {
	private static final int sCountInOnePage = 4;
	
	private static final int SENSOR_SHAKE = 10;
	private SensorManager mSensorMgr;
	private Vibrator mVibrator;
	private SensorEventListener mSensorListener;
	private String mSearchStr;
	private int mCurPage = -1;
	private CartoonListAdapter mListAdapter;
	
	private ProgressBar mProgress;
	private PullToRefreshListView mPullToRefreshListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity_layout);
		setupActionBar();
		setupViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (mSensorMgr == null) {
			return;
		}
		
		mSensorMgr.registerListener(mSensorListener,
				mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
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
				SearchActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.INVISIBLE);
		
		ImageButton recentButton = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.INVISIBLE);
		
		TextView title = (TextView)this.findViewById(R.id.tv_action_title);
		title.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.search));
	}
	
	private void setupViews() {
		final EditText searchEdit = (EditText) this.findViewById(R.id.et_search);
		searchEdit.setVisibility(View.VISIBLE);
		searchEdit.setOnEditorActionListener(new EditActionListener());
		
		ImageView imv = (ImageView)this.findViewById(R.id.imv_search);
		imv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onSearchAction(searchEdit);
			}
		});

		RelativeLayout searchHeader = (RelativeLayout) this
				.findViewById(R.id.rl_search_header);
		searchHeader.setVisibility(View.VISIBLE);

		mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mSensorListener = new ShakeListener();
		
		mProgress = (ProgressBar)this.findViewById(R.id.progress_bar);
		mPullToRefreshListView = (PullToRefreshListView) this
				.findViewById(R.id.lv_books_list);
		mPullToRefreshListView.setOnRefreshListener(
				new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						new RetrieveSearchCartoonsTask().execute(mSearchStr);
					}
				});
		
		setupCartoonsList(getRandomCartoons(3));
	}
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				mVibrator.vibrate(300);
				Animation shake = AnimationUtils.loadAnimation(
						SearchActivity.this, R.anim.shake);
				findViewById(R.id.imv_search_header).startAnimation(shake);

				setupCartoonsList(getRandomCartoons(3));
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
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
		
		mSearchStr = v.getText().toString();
		if (TextUtils.isEmpty(mSearchStr))
			return;

		mCurPage = -1;
		new RetrieveSearchCartoonsTask().execute(mSearchStr);

		TextView title = (TextView) this.findViewById(R.id.tv_action_title);
		title.setVisibility(View.INVISIBLE);

		TextView backText = (TextView) this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.VISIBLE);
		backText.setText(String.format("\"%s\"%s", v.getText().toString(),
				getString(R.string.search_result)));
	}
	
	private void setupCartoonsList(ArrayList<CartoonInfo> list) {
		mListAdapter = new CartoonListAdapter(this, list);
		mPullToRefreshListView.getRefreshableView().setAdapter(mListAdapter);
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
	
	private class CartoonListAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		ArrayList<CartoonInfo> mList;
		private TempDataLoader tmpDataLoader;
		private DatabaseMgr mDbMgr;
		
		public CartoonListAdapter(Context ctx, ArrayList<CartoonInfo> list) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mList = list;
			tmpDataLoader = new TempDataLoader();
			mDbMgr = new DatabaseMgr(ctx);
		}
		
		public void addDataList(ArrayList<CartoonInfo> list) {
			for (CartoonInfo cartoonInfo : list) {
				mList.add(cartoonInfo);
			}
			
			this.notifyDataSetChanged();
		}
		
		@Override
        public int getCount() {
			if (mList == null) return 0;
			
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
				convertView = mInflater.inflate(R.layout.category_cartoon_list_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
				        .findViewById(R.id.imv_icon);
				holder.name = (TextView) convertView
				        .findViewById(R.id.tv_name);
				holder.author = (TextView) convertView
				        .findViewById(R.id.tv_author);
				holder.comment = (TextView) convertView
						.findViewById(R.id.tv_new_comment);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (mList != null && mList.get(position) != null) {
				holder.name.setText(mList.get(position).name);
				holder.author.setText(getString(R.string.author,
						mList.get(position).author));
				
				Drawable drawable = (Drawable) holder.icon.getDrawable();
				if (drawable != null && drawable instanceof BitmapDrawable) {
					BitmapDrawable bmpDrawable = (BitmapDrawable)drawable;
					if (bmpDrawable.getBitmap() != null) {
						bmpDrawable.getBitmap().recycle();
					}
				}
				holder.icon.setImageBitmap(null);
				
				WeakReference<Bitmap> bmpRef = tmpDataLoader.getCartoonCover(mList
						.get(position).id);
				if (bmpRef != null && bmpRef.get() != null) {
					holder.icon.setImageBitmap(bmpRef.get());
				}
				
				holder.comment.setVisibility(View.GONE);

				Cursor cursor = mDbMgr.query(DatabaseMgr.COMMENTS_ALL_URL,
						null, String.format("%s=%d",
								DatabaseMgr.COMMENTS_COLUMN.CARTOON_ID,
								mList.get(position).id), null, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						String comment = cursor.getString(cursor
								.getColumnIndex(COMMENTS_COLUMN.CONTENT));
						holder.comment.setVisibility(View.VISIBLE);
						holder.comment.setText(comment);
					}

					cursor.close();
				}
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
			intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, mList.get(position).id);
			intent.setClass(mCtx, DetailsPageActivity.class);
			mCtx.startActivity(intent);
		}
		
		private class ViewHolder {
			ImageView icon;
			TextView name;
			TextView author;
			TextView comment;
		}
	}
	
	private class RetrieveSearchCartoonsTask extends
			AsyncTask<String, Integer, ArrayList<CartoonInfo>> {
		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected ArrayList<CartoonInfo> doInBackground(String... params) {
			return getSearchCartoons(params[0]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<CartoonInfo> result) {
			mProgress.setVisibility(View.GONE);
			mPullToRefreshListView.onRefreshComplete();
			
			if (result == null || result.size() < 1) {
				Toast.makeText(getApplicationContext(),
						SearchActivity.this.getString(R.string.no_more_data),
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (mCurPage == -1) {
				setupCartoonsList(result);
			} else {
				mListAdapter.addDataList(result);
			}
			
			mCurPage++;
			super.onPostExecute(result);
		}
	}
	
	private ArrayList<CartoonInfo> getSearchCartoons(String str) {
		if (TextUtils.isEmpty(str)) {
			return null;
		}
		
		MuuServerWrapper muuWrapper = new MuuServerWrapper(this.getApplicationContext());
		ArrayList<CartoonInfo> list = muuWrapper.getSearchCartoons(str, mCurPage + 1, sCountInOnePage);
		for (CartoonInfo cartoonInfo : list) {
			muuWrapper.getComments(cartoonInfo.id, 0, 1);
		}
		
		return list;
	}
}
