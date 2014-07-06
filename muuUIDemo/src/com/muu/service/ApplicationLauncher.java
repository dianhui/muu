package com.muu.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import com.muu.util.PreferenceUtil;
import com.muu.util.PropertyMgr;
import com.muu.util.TempDataLoader;
//import com.muu.util.TempDataLoader;

import android.app.Application;
import android.os.Environment;

public class ApplicationLauncher extends Application {
	public static final String sHasRun = "has_run";
	
	@Override
	public void onCreate() {
		super.onCreate();

//		new TempDataLoader().loadFakeComments();
//		
//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				boolean hasRun = PreferenceUtil.getBoolean(
//						getApplicationContext(), sHasRun);
//				if (hasRun) {
//					return;
//				}
//
//				PreferenceUtil.setBoolean(getApplicationContext(), sHasRun,
//						true);
//				TempDataLoader dataLoader = new TempDataLoader();
//				dataLoader.loadTempData(getApplicationContext());
//			}
//		});
//		thread.start();
		
		new TempDataLoader().initCategoryMap(getApplicationContext());
		loadProperties();
	}
	
	private void loadProperties() {
		PropertyMgr.init(null, "muu", "1.0");
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "muu.properties");
		if (f.exists()) {
			try {
				PropertyMgr.init(new FileInputStream(f.getAbsoluteFile()),
						"Muu", "1.0");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			PropertyMgr.init(null, "Muu", "1.0");
		}
	}
}