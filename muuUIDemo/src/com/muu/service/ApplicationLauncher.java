package com.muu.service;

import com.muu.util.PreferenceUtil;
import com.muu.util.TempDataLoader;

import android.app.Application;

public class ApplicationLauncher extends Application {
	public static final String sHasRun = "has_run";
	
	@Override
	public void onCreate() {
		super.onCreate();

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean hasRun = PreferenceUtil.getBoolean(
						getApplicationContext(), sHasRun);
				if (hasRun) {
					return;
				}

				PreferenceUtil.setBoolean(getApplicationContext(), sHasRun,
						true);
				TempDataLoader dataLoader = new TempDataLoader();
				dataLoader.loadTempData(getApplicationContext());
				
			}
		});
		thread.start();
	}
}