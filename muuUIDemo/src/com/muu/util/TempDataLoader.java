package com.muu.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CHAPTERS_COLUMN;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TempDataLoader {
	private static final String TAG = "TempDataLoader";
	
	private static final String MUU_TMP = "muu_tmp";
	private static final String CARTOONS = "cartoons";
	private static final String IMAGES = "images";
	private static final String INFO = "info";
	private static final String COVER = "cover";
	private static final String CONTENTS = "contents";
	private static final String COMMENTS50 = "comments50";
	private static final String TXT_POSTFIX = ".txt";
	private static final String PNG_POSTFIX = ".png";
	private static final String JPG_POSTFIX = ".jpg";

	public static final String WEEK_TOP20 = "week_top20";
	public static final String HOT_TOP20 = "hot_top20";
	public static final String NEW_TOP20 = "new_top20";
	public static final ArrayList<String> sFakeCommentsPool = new ArrayList<String>();
	
	public void loadTempData(Context ctx) {
		Log.d(TAG, "Load temp data ...");
		loadList(ctx, WEEK_TOP20);
		loadList(ctx, HOT_TOP20);
		loadList(ctx, NEW_TOP20);
		loadFakeComments();
	}

	public ArrayList<Integer> getCartoonIds(String whichList) {
		ArrayList<Integer> cartoonIds = new ArrayList<Integer>();
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + whichList
				+ TXT_POSTFIX;
		try {
			for (String str : FileReaderUtils.getFileContent(path)
					.split("；|;")) {
				cartoonIds.add(Integer.parseInt(str));
			}
		} catch (IOException e) {
			Log.d(TAG, "Exception: " + e.getMessage());
		}
		return cartoonIds;
	}
	
	public Bitmap getCartoonCover(int id) {
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + CARTOONS
				+ File.separator + id + File.separator + COVER + PNG_POSTFIX;
		File file = new File(path);
		if (!file.exists()) {
			path = android.os.Environment.getExternalStorageDirectory()
					+ File.separator + MUU_TMP + File.separator + CARTOONS
					+ File.separator + id + File.separator + COVER + JPG_POSTFIX;
			
			file = new File(path);
			if (!file.exists()) {
				Log.d(TAG, ".... File not exists: " + path);
				return null;
			}
		}
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		return BitmapFactory.decodeFile(path, opts);
	}
	
	private void loadList(Context ctx, String whichList) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for (Integer id : getCartoonIds(whichList)) {
			Log.d(TAG, getCartoonInfo(id).toString());
			CartoonInfo info = getCartoonInfo(id);
			dbMgr.insert(DatabaseMgr.MUU_CARTOONS_ALL_URL, info.toContentValues());
		}
		dbMgr.closeDatabase();
	}

	private CartoonInfo getCartoonInfo(int cartoonId) {
		CartoonInfo info = new CartoonInfo();
		String path = android.os.Environment.getExternalStorageDirectory()
				+ File.separator + MUU_TMP + File.separator + CARTOONS
				+ File.separator + cartoonId + File.separator + INFO
				+ TXT_POSTFIX;
		Log.d(TAG, "path: "+path);
		try {
			String[] list = FileReaderUtils.getFileContent(path).split("；|;");
//			info.id = Integer.parseInt(list[0]);
			info.id = cartoonId;
			info.name = list[1];
			info.author = list[2];
			info.date = list[3];
			info.abst = list[4];
			info.category = list[5];
			info.isComplete = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}
	
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
				+ TXT_POSTFIX;
		try {
			String[] list = FileReaderUtils.getFileContent(path).split("；|;");
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
				+ TXT_POSTFIX;
		try {
			for (String str : FileReaderUtils.getLines(path)) {
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
//				Log.d(TAG, "file: "+f.toString());
				return p.matcher(f.getName()).matches();
			}
		});
		
		int size = 0;
		for (File file : fileList) {
			size += file.length();
		}
		
		return size/(1024.0f*1024.0f);
	}
}
