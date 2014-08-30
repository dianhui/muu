package com.muu.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import android.os.Environment;
import android.util.Log;

public class PropertyMgr {
	private static final String TAG = "PropertyMgr";
	private static final String sDefaultBaseUrl = "http://testapi.muu.com.cn/mobileapi";
	private static final int sDefaultHttpTimeout = 60 * 1000;
	private static final int sDefaultSocketTimeout = 30 * 1000;
	private static final String sDefaultStoragePath = Environment
			.getExternalStorageDirectory().toString();
	private static final String sDefaultCachePath = sDefaultStoragePath
			+ "/muu_cache/";
	private static final String sVolleyPath = "volley";
	
	
	private static PropertyMgr mInstance;
	private Properties mProps = new Properties();
	private String mUserAgent;
	private String mMuuBaseUrl;
	private int mHttpTimeout;
	private int mSocketTimeout;
	private String mStoragePath;
	private String mCachePath;
	
	private PropertyMgr(InputStream in, String prodName, String prodVer) {
		initialize(in, prodName, prodVer);
	}
	
	private void initialize(InputStream in, String prodName, String prodVer) {
		mUserAgent = String.format("%s%s/Android/%s", prodName, prodVer,
				android.os.Build.VERSION.RELEASE);
		
		if (in == null) {
			mMuuBaseUrl = sDefaultBaseUrl;
			mHttpTimeout = sDefaultHttpTimeout;
			mSocketTimeout = sDefaultSocketTimeout;
			mStoragePath = sDefaultStoragePath;
			mCachePath = sDefaultCachePath;
			return;
		}
		
		try {
			mProps.load(in);
			
			mUserAgent = mProps.getProperty("user_agent", mUserAgent);
			mMuuBaseUrl = mProps.getProperty("muu_base_url", sDefaultBaseUrl);
			mHttpTimeout = getInt("http_timeout", sDefaultHttpTimeout);
			mSocketTimeout = getInt("socket_timeout", sDefaultSocketTimeout);
			mStoragePath = mProps.getProperty("storage_path", sDefaultStoragePath);
			mCachePath = mProps.getProperty("cache_path", sDefaultCachePath);
			
		} catch (Exception e) {
			Log.d(TAG, "Failed to load property.");
		}
	}
	
	public synchronized static void init(InputStream in, String prodName, String prodVer) {
		if (mInstance == null)
			mInstance = new PropertyMgr(in, prodName, prodVer);
	}
	
	public static PropertyMgr getInstance() {
		if (mInstance == null) {
			throw new NullPointerException(
					"You must call PropertyMgr.init() before using this method.");
		}

		return mInstance;
	}
	
	public String getUserAgent() {
		return mUserAgent;
	}
	
	public String getMuuBaseUrl() {
		return mMuuBaseUrl;
	}
	
	public int getHttpTimeout() {
		return mHttpTimeout;
	}
	
	public int getSocketTimeout() {
		return mSocketTimeout;
	}
	
	public String getStoragePath() {
		return mStoragePath;
	}
	
	public String getCachePath() {
		return mCachePath;
	}
	
	public File getVolleyCacheDir() {
		File cacheDir = new File(String.format("%s/%s", mCachePath, sVolleyPath));
		 if (!cacheDir.exists()) cacheDir.mkdirs();
		 
		 return cacheDir;
	}
	
	public String getCoverPath(int cartoonId) {
		return getCartoonPath(cartoonId);
	}
	
	public String getCartoonPath(int cartoonId) {
		return getCachePath() + "cartoons/" + cartoonId;
	}
	
	public String getActivityCoverPath() {
		return getCachePath() + "activities_cover/";
	}
	
	public void setStoragePath(String path) {
		mStoragePath = path;
		mProps.setProperty("storage_path", path);
	}
	
	public void setCachePath(String path){
		mCachePath = path;
		mProps.setProperty("cache_path", path);
	}
	
	private int getInt(String key, int defaultValue) {
		String value = mProps.getProperty(key);
		if (value == null) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			Log.w("PropertyMgr", key + e.getMessage());
		}

		return defaultValue;
	}
}