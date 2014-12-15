package com.muu.ui;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.util.Log;

public class StatisticsBaseActivity extends Activity {
	private final static String TAG = "StatisticsBaseActivity";
	
	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "onResume().");
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Log.d(TAG, "onPause().");
		StatService.onPause(this);
	}
}
