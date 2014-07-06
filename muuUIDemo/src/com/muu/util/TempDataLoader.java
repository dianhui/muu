package com.muu.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CHAPTERS_COLUMN;
import com.muu.uidemo.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class TempDataLoader {
	private static final String TAG = "TempDataLoader";
	
	private static final String MUU_TMP = "muu_tmp";
	private static final String CARTOONS = "cartoons";
	private static final String IMAGES = "images";
//	private static final String INFO = "info";
	private static final String COVER = "cover";
	private static final String CONTENTS = "contents";
	private static final String COMMENTS50 = "comments50";
	
	public static final String WEEK_TOP20 = "week_top20";
	public static final String HOT_TOP20 = "hot_top20";
	public static final String NEW_TOP20 = "new_top20";
	public static final ArrayList<String> sFakeCommentsPool = new ArrayList<String>();
	
	/**
	 * store cartoons into db. 
	 * if the cartoon already in db, check if need update, if no update then ignore.
	 * @param
	 *  - ctx, application context, NOT NULL.
	 * 	- cartoonsList - list of cartoons to be store.
	 * */
	public void storeCartoonsToDB(Context ctx, ArrayList<CartoonInfo> cartoonsList) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for (CartoonInfo cartoonInfo : cartoonsList) {
			Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), cartoonInfo.id));
			Cursor cur = dbMgr.query(uri, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				CartoonInfo cartoon = new CartoonInfo(cur);
				
				cur.close();
				if (cartoonInfo.equals(cartoon)) continue;
				
				dbMgr.update(uri, cartoonInfo.toContentValues(), null, null);
				continue;
			}
			
			if (cur != null) cur.close();
			dbMgr.insert(uri, cartoonInfo.toContentValues());
		}
		dbMgr.closeDatabase();
	}

	public ArrayList<Integer> getCartoonIds(String whichList) {
		ArrayList<Integer> cartoonIds = new ArrayList<Integer>();
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + whichList
				+ FileFormatUtil.TXT_POSTFIX;
		try {
			for (String str : FileReaderUtil.getFileContent(path)
					.split("；|;")) {
				cartoonIds.add(Integer.parseInt(str));
			}
		} catch (IOException e) {
			Log.d(TAG, "Exception: " + e.getMessage());
		}
		return cartoonIds;
	}
	
	public Boolean isCoverExist(int id) {
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + CARTOONS
				+ File.separator + id + File.separator + COVER + FileFormatUtil.PNG_POSTFIX;
		File file = new File(path);
		if (file.exists()) return true;
		
		path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + CARTOONS
				+ File.separator + id + File.separator + COVER + FileFormatUtil.JPG_POSTFIX;
		file = new File(path);
		if (file.exists()) return true;
		
		path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + CARTOONS
				+ File.separator + id + File.separator + COVER + FileFormatUtil.GIF_POSTFIX;
		file = new File(path);
		if (file.exists()) return true;
		
		return false;
	}
	
	public Bitmap getCartoonCover(int id) {
		String path = PropertyMgr.getInstance().getCoverPath() + id
				+ FileFormatUtil.JPG_POSTFIX;
		File file = new File(path);
		if (file.exists()) {
			return getBitmap(path);
		}
		
		path = PropertyMgr.getInstance().getCoverPath() + id + FileFormatUtil.PNG_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path);
		}
		
		path = PropertyMgr.getInstance().getCoverPath() + id + FileFormatUtil.GIF_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path);
		}
		
		Log.d(TAG, ".... File not exists: " + path);
		return null;
	}
	
	private Bitmap getBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		return BitmapFactory.decodeFile(path, opts);
	}
	
//	private void loadList(Context ctx, String whichList) {
//		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
//		for (Integer id : getCartoonIds(whichList)) {
//			Log.d(TAG, getCartoonInfo(id).toString());
//			CartoonInfo info = getCartoonInfo(id);
//			dbMgr.insert(DatabaseMgr.MUU_CARTOONS_ALL_URL, info.toContentValues());
//		}
//		dbMgr.closeDatabase();
//	}

//	private CartoonInfo getCartoonInfo(int cartoonId) {
//		CartoonInfo info = new CartoonInfo();
//		String path = android.os.Environment.getExternalStorageDirectory()
//				+ File.separator + MUU_TMP + File.separator + CARTOONS
//				+ File.separator + cartoonId + File.separator + INFO
//				+ FileFormatUtil.TXT_POSTFIX;
//		Log.d(TAG, "path: "+path);
//		try {
//			String[] list = FileReaderUtil.getFileContent(path).split("；|;");
//			info.id = cartoonId;
//			info.name = list[1];
//			info.author = list[2];
//			info.updateDate = list[3];
//			info.abst = list[4];
//			info.topicCode = list[5];
//			info.isComplete = 1;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return info;
//	}
	
	public static void recycleBmp(Bitmap bmp) {
		if (bmp == null || bmp.isRecycled()) {
			return;
		}
		
		bmp.recycle();
		bmp = null;
		System.gc();
	}
	
	public void loadChapters(Context ctx, int id) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		ArrayList<String> chapterList = getChapters(id);
		for (int i = 0; i < chapterList.size(); i++) {
			ContentValues values = new ContentValues();
			values.put(CHAPTERS_COLUMN.CARTOON_ID, id);
			values.put(CHAPTERS_COLUMN.INDEX, i+1);
			values.put(CHAPTERS_COLUMN.NAME, chapterList.get(i));
			values.put(CHAPTERS_COLUMN.PAGE_COUNT, getChatperImgCount(id, i+1));
			
			dbMgr.insert(DatabaseMgr.CHAPTER_ALL_URL, values);
		}
		dbMgr.closeDatabase();
	}
	
	public ArrayList<String> getChapters(int id) {
		ArrayList<String> chapterList = new ArrayList<String>();
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + CARTOONS
				+ File.separator + id + File.separator + CONTENTS
				+ FileFormatUtil.TXT_POSTFIX;
		try {
			String[] list = FileReaderUtil.getFileContent(path).split("；|;");
			int i = 1;
			for (String str : list) {
				chapterList.add(i++ + ". " + str);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.d(TAG, "image count: " + getChatperImgCount(id, 1));
		
		return chapterList;
	}
	
	public void loadFakeComments() {
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + COMMENTS50
				+ FileFormatUtil.TXT_POSTFIX;
		try {
			for (String str : FileReaderUtil.getLines(path)) {
				sFakeCommentsPool.add(str);
			}
		} catch (IOException e) {
			Log.d(TAG, "Exception: " + e.getMessage());
		}
	}
	
	public ArrayList<String> getRandomComments(int count) {
		ArrayList<String> list = new ArrayList<String>(count);
		if (sFakeCommentsPool == null || sFakeCommentsPool.size() < 1) {
			return list;
		}
		
		Random random = new Random(System.currentTimeMillis());
		while (list.size() < count) {
			int tmp = Math.abs(random.nextInt()) % 50;
			if (list.contains(sFakeCommentsPool.get(tmp))) {
				continue;
			}
			
			list.add(sFakeCommentsPool.get(tmp));
		}
		return list;
	}
	
	public int getChatperImgCount(int cartoonId, int chapterId) {
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + IMAGES
				+ File.separator;
		Log.d(TAG, "path: "+path);
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			Log.d(TAG, "No temp folder: " + path);
			return 0;
		}
		final Pattern p = Pattern.compile(String.format("%d_%d_\\d{1,}.jpg",
				cartoonId, chapterId));
		File[] fileList = dirFile.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				return p.matcher(f.getName()).matches();
			}
		});
		
		return fileList.length;
	}
	
	public Bitmap getCartoonImage(int cartoonId, int chapterIdx, int pageIdx) {
		String path = String.format("%s/%s/%s/%d_%d_%d.jpg",
				android.os.Environment.getExternalStorageDirectory(), MUU_TMP,
				IMAGES, cartoonId, chapterIdx, pageIdx);
		Log.d(TAG, "path: "+path);
		
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		
		return BitmapFactory.decodeFile(path);
	}
	
	public float getCartoonSize(int cartoonId) {
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + IMAGES
				+ File.separator;
		Log.d(TAG, "path: "+path);
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			Log.d(TAG, "No temp folder: " + path);
			return 0;
		}
		final Pattern p = Pattern.compile(String.format("%d_\\d{1,}_\\d{1,}.jpg",
				cartoonId));
		Log.d(TAG, "pattern: "+p.toString());
		File[] fileList = dirFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return p.matcher(f.getName()).matches();
			}
		});
		
		int size = 0;
		for (File file : fileList) {
			size += file.length();
		}
		
		return size/(1024.0f*1024.0f);
	}
	
	private static final HashMap<String, String> sCodeTopicStrMap = new HashMap<String, String>();
	private static final HashMap<String, String> sTopicStrCodeMap = new HashMap<String, String>();
	public void initCategoryMap(Context ctx) {
		sCodeTopicStrMap.put("01", ctx.getString(R.string.humer));
		sCodeTopicStrMap.put("02", ctx.getString(R.string.hot_blood));
		sCodeTopicStrMap.put("04", ctx.getString(R.string.science_fiction));
		sCodeTopicStrMap.put("06", ctx.getString(R.string.fantasy));
		sCodeTopicStrMap.put("08", ctx.getString(R.string.love));
		sCodeTopicStrMap.put("09", ctx.getString(R.string.life));
		sCodeTopicStrMap.put("11", ctx.getString(R.string.sport));
		sCodeTopicStrMap.put("12", ctx.getString(R.string.ratiocination));
		sCodeTopicStrMap.put("13", ctx.getString(R.string.terror));
		sCodeTopicStrMap.put("14", ctx.getString(R.string.history));
		sCodeTopicStrMap.put("15", ctx.getString(R.string.tanbi));
		sCodeTopicStrMap.put("16", ctx.getString(R.string.other));
		sCodeTopicStrMap.put("17", ctx.getString(R.string.homosex));
		
		
		sTopicStrCodeMap.put(ctx.getString(R.string.humer), "01");
		sTopicStrCodeMap.put(ctx.getString(R.string.hot_blood), "02");
		sTopicStrCodeMap.put(ctx.getString(R.string.science_fiction), "04");
		sTopicStrCodeMap.put(ctx.getString(R.string.fantasy), "06");
		sTopicStrCodeMap.put(ctx.getString(R.string.love), "08");
		sTopicStrCodeMap.put(ctx.getString(R.string.life), "09");
		sTopicStrCodeMap.put(ctx.getString(R.string.sport), "11");
		sTopicStrCodeMap.put(ctx.getString(R.string.ratiocination), "12");
		sTopicStrCodeMap.put(ctx.getString(R.string.terror), "13");
		sTopicStrCodeMap.put(ctx.getString(R.string.history), "14");
		sTopicStrCodeMap.put(ctx.getString(R.string.tanbi), "15");
		sTopicStrCodeMap.put(ctx.getString(R.string.other), "16");
		sTopicStrCodeMap.put(ctx.getString(R.string.homosex), "17");
	}
	
	public static Drawable getTopicTagDrawable(Context ctx, String topicCode) {
		Resources res = ctx.getResources();
		int resId = res.getIdentifier(
				String.format("ic_category_tag_%s", topicCode), "drawable",
				ctx.getPackageName());
		Drawable drawable = res.getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}
	
	public static Object[] getTopicsArray() {
		return sCodeTopicStrMap.values().toArray();
	}
	
	public static String getTopicString(String topicCode) {
		return sCodeTopicStrMap.get(topicCode);
	}
	
	public static String getTopicCode(String topicStr) {
		return sTopicStrCodeMap.get(topicStr);
	}
}
