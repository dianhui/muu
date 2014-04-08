package com.muu.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TempDataLoader {
	private static final String TAG = "TempDataLoader";
	
	private static final String MUU_TMP = "muu_tmp";
	private static final String CARTOONS = "cartoons";
	private static final String INFO = "info";
	private static final String COVER = "cover";
	private static final String CONTENTS = "contents";
	private static final String TXT_POSTFIX = ".txt";
	private static final String PNG_POSTFIX = ".png";
	private static final String JPG_POSTFIX = ".jpg";

	public static final String WEEK_TOP20 = "week_top20";
	public static final String HOT_TOP20 = "hot_top20";
	public static final String NEW_TOP20 = "new_top20";
	
	public void loadTempData(Context ctx) {
		loadList(ctx, WEEK_TOP20);
		loadList(ctx, HOT_TOP20);
		loadList(ctx, NEW_TOP20);
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
			dbMgr.insert(DatabaseMgr.MUU_CARTOONS_ALL, info.toContentValues());
		}
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
}
