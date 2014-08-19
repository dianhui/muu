package com.muu.ui;

import java.lang.ref.WeakReference;
import java.util.Random;

import com.android.volley.toolbox.NetworkImageView;
import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;
import com.muu.volley.VolleyHelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ReadFinishDialog extends android.app.Dialog {

	private Context mCtx;
	private static final int SENSOR_SHAKE = 10;
	private SensorManager mSensorMgr;
	private Vibrator mVibrator;
	private SensorEventListener mSensorListener;
	
	public ReadFinishDialog(Context context, int theme) {
		super(context, theme);
		
		mCtx = context.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.read_finish_layout);
		setupRecommendView(getRandomCartoon());
		
		mSensorMgr = (SensorManager) mCtx.getSystemService(Context.SENSOR_SERVICE);
		mVibrator = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
		mSensorListener = new ShakeListener();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (mSensorMgr == null) return;
		mSensorMgr.registerListener(mSensorListener,
				mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(mSensorListener);
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
						mCtx, R.anim.shake);
				findViewById(R.id.imv_shake).startAnimation(shake);

				setupRecommendView(getRandomCartoon());
				break;
			}
		}
	};
	
	private void setupRecommendView(final CartoonInfo info) {
		if (info == null) {
			return;
		}
		
		if (TextUtils.isEmpty(info.coverUrl)) {
			return;
		}
		
		NetworkImageView imv = (NetworkImageView)this.findViewById(R.id.imv_cartoon_cover);
		imv.setImageUrl(info.coverUrl, VolleyHelper.getInstanse(mCtx).getDefaultImageLoader());
		imv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startDetailActivity(info.id);
			}
		});
	}
	
	private void startDetailActivity(int cartoonId) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(DetailsPageActivity.sCartoonIdExtraKey, cartoonId);
		intent.setClass(mCtx, DetailsPageActivity.class);
		mCtx.startActivity(intent);
	}
	
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
	
	private CartoonInfo getRandomCartoon() {
		DatabaseMgr dbMgr = new DatabaseMgr(mCtx);
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
		
		Random random = new Random(System.currentTimeMillis());
		int tmp = Math.abs(random.nextInt()) % cur.getCount();
		if (!cur.moveToPosition(tmp - 1)) {
			return null;
		}
		
		return new CartoonInfo(cur);
	}
}
