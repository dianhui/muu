package com.muu.util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.muu.data.CartoonInfo;
import com.muu.data.ChapterInfo;
import com.muu.data.Comment;
import com.muu.data.ImageInfo;
import com.muu.data.Roast;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;
import com.muu.db.DatabaseMgr.CHAPTERS_COLUMN;
import com.muu.db.DatabaseMgr.IMAGES_COLUMN;
import com.muu.cartoons.R;

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
	private static final String IMAGES = "images";
	private static final String COMMENTS50 = "comments50";
	
	public static final String WEEK_TOP20 = "week_top20";
	public static final String HOT_TOP20 = "hot_top20";
	public static final String NEW_TOP20 = "new_top20";
	public static final ArrayList<String> sFakeCommentsPool = new ArrayList<String>();
	
	private static final int sCoversInSampleSize = 5;
	private static final int sCartoonImgInSampleSize = 1;
	
	/**
	 * store cartoons into db. 
	 * if the cartoon already in db, check if need update, if no update then ignore.
	 * @param
	 *  - ctx, application context, NOT NULL.
	 * 	- cartoonsList - list of cartoons to be stored.
	 * */
	public void storeCartoonsToDB(Context ctx, ArrayList<CartoonInfo> cartoonsList) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for (CartoonInfo cartoonInfo : cartoonsList) {
			Log.d("XXX", "cartoonInfo: " + cartoonInfo.toString());
			
			Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.MUU_CARTOONS_ALL_URL.toString(), cartoonInfo.id));
			Cursor cur = dbMgr.query(uri, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				CartoonInfo cartoon = new CartoonInfo(cur);
				
				Log.d("XXX", "cartoon: " + cartoon.toString());
				
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
	
	/**
	 * store chapters into db.
	 * if the chapter already in db, check if need update, if no update then ignore.
	 * 
	 * @param
	 * 	- ctx, application context, not null.
	 * 	- chaptersList, list of chapters to be stored.
	 * */
	public void storeChaptersToDB(Context ctx, ArrayList<ChapterInfo> chaptersList) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for (ChapterInfo chapterInfo : chaptersList) {
			Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.CHAPTER_ALL_URL.toString(), chapterInfo.id));
			Cursor cur = dbMgr.query(uri, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				ChapterInfo chapter = new ChapterInfo(cur);
				cur.close();
				if (chapterInfo.equals(chapter)) continue;
				
				dbMgr.update(uri, chapterInfo.toContentValues(), null, null);
				continue;
			}
			
			if (cur != null) cur.close();
			dbMgr.insert(uri, chapterInfo.toContentValues());
		}
		dbMgr.closeDatabase();
	}
	
	public void storeCommentsToDB(Context ctx, ArrayList<Comment> commentsList) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for (Comment comment : commentsList) {
			Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.COMMENTS_ALL_URL.toString(), comment.id));
			Cursor cur = dbMgr.query(uri, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				Comment commentInDb = new Comment(cur);
				cur.close();
				if (comment.equals(commentInDb)) continue;
				
				dbMgr.update(uri, comment.toContentValues(), null, null);
				continue;
			}
			
			if (cur != null) cur.close();
			dbMgr.insert(uri, comment.toContentValues());
		}
		dbMgr.closeDatabase();
	}
	
	public void storeRoastsToDB(Context ctx, ArrayList<Roast> roastsList) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for (Roast roast : roastsList) {
			Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.ROASTS_ALL_URL.toString(), roast.id));
			Cursor cur = dbMgr.query(uri, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				Roast roastInDb = new Roast(cur);
				cur.close();
				if (roast.equals(roastInDb)) continue;
				
				dbMgr.update(uri, roast.toContentValues(), null, null);
				continue;
			}
			
			if (cur != null) cur.close();
			dbMgr.insert(uri, roast.toContentValues());
		}
		dbMgr.closeDatabase();
	}
	
	public void storeImagesToDB(Context ctx, ArrayList<ImageInfo> imagesList, int chapterId) {
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		for(ImageInfo image : imagesList) {
			Uri uri = Uri.parse(String.format("%s/%d", DatabaseMgr.IMAGES_ALL_URL.toString(), image.id));
			Cursor cur = dbMgr.query(uri, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				ImageInfo imgInDb = new ImageInfo(cur);
				cur.close();
				if (image.equals(imgInDb)) continue;
				
				dbMgr.update(uri, image.toContentValues(), null, null);
				continue;
			}
			
			if (cur != null) cur.close();
			dbMgr.insert(uri, image.toContentValues());
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
	
	public WeakReference<Bitmap> getCartoonCover(int id) {
		String path = PropertyMgr.getInstance().getCoverPath(id) + "/cover"
				+ FileFormatUtil.JPG_POSTFIX;
		File file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCoversInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getCoverPath(id) + "/cover" + FileFormatUtil.PNG_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCoversInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getCoverPath(id) + "/cover" + FileFormatUtil.GIF_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCoversInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getCoverPath(id) + "/cover";
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCoversInSampleSize);
		}
		
		Log.d(TAG, ".... File not exists: " + path);
		return null;
	}
	
	public String getCartoonCoverPath(int id) {
		String path = PropertyMgr.getInstance().getCoverPath(id) + "/cover"
				+ FileFormatUtil.JPG_POSTFIX;
		File file = new File(path);
		if (file.exists()) {
			return path;
		}
		
		path = PropertyMgr.getInstance().getCoverPath(id) + "/cover" + FileFormatUtil.PNG_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return path;
		}
		
		path = PropertyMgr.getInstance().getCoverPath(id) + "/cover" + FileFormatUtil.GIF_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return path;
		}
		
		path = PropertyMgr.getInstance().getCoverPath(id) + "/cover";
		file = new File(path);
		if (file.exists()) {
			return path;
		}
		
		Log.d(TAG, ".... File not exists: " + path);
		return null;
	}
	
	public WeakReference<Bitmap> getImage(int cartoonId, int imgId) {
		
		String path = PropertyMgr.getInstance().getCartoonPath(cartoonId)+"/" + imgId
				+ FileFormatUtil.JPG_POSTFIX;
		File file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getCartoonPath(cartoonId) + imgId + FileFormatUtil.PNG_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getCartoonPath(cartoonId) + imgId + FileFormatUtil.GIF_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getCartoonPath(cartoonId) + imgId;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		Log.d(TAG, ".... File not exists: " + path);
		return null;
	}
	
	public WeakReference<Bitmap> getActivityCover(String title){
		String path = PropertyMgr.getInstance().getActivityCoverPath() + title
				+ FileFormatUtil.JPG_POSTFIX;
		File file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCoversInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getActivityCoverPath() + title + FileFormatUtil.PNG_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getActivityCoverPath() + title + FileFormatUtil.GIF_POSTFIX;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		path = PropertyMgr.getInstance().getActivityCoverPath() + title;
		file = new File(path);
		if (file.exists()) {
			return getBitmap(path, sCartoonImgInSampleSize);
		}
		
		Log.d(TAG, ".... File not exists: " + path);
		return null;
	}
	
	private WeakReference<Bitmap> getBitmap(String path, int inSampleSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = inSampleSize;
		return new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
	}
	
	public static void recycleBmp(Bitmap bmp) {
		if (bmp == null || bmp.isRecycled()) {
			return;
		}
		
		bmp.recycle();
		bmp = null;
		System.gc();
	}

	public ArrayList<ChapterInfo> getChapters(Context ctx, int id) {
		ArrayList<ChapterInfo> chapterList = new ArrayList<ChapterInfo>();
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		Cursor cur = dbMgr.query(DatabaseMgr.CHAPTER_ALL_URL, null,
				String.format("%s=%d", CHAPTERS_COLUMN.CARTOON_ID, id), null,
				null);
		if (cur == null) return chapterList;
		
		if (cur.getCount() <= 0) {
			cur.close();
			return chapterList;
		}

		while (cur.moveToNext()) {
			chapterList.add(new ChapterInfo(cur));
		}
		
		cur.close();
		return chapterList;
	}
	
	public ArrayList<ImageInfo> getChapterImageInfos(Context ctx, int chapterId) {
		ArrayList<ImageInfo> imagesList = new ArrayList<ImageInfo>();
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		Cursor cur = dbMgr.query(DatabaseMgr.IMAGES_ALL_URL, null,
				String.format("%s=%d", IMAGES_COLUMN.CHAPTER_ID, chapterId), null,
				null);
		if (cur == null) return imagesList;
		
		if (cur.getCount() <= 0) {
			cur.close();
			return imagesList;
		}

		while (cur.moveToNext()) {
			imagesList.add(new ImageInfo(cur));
		}
		
		cur.close();
		return imagesList;
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
	
	private static final ArrayList<String> sSortedTopicCode = new ArrayList<String>();
	private static final HashMap<String, String> sCodeTopicStrMap = new HashMap<String, String>();
	public void initCategoryMap(Context ctx) {
		sSortedTopicCode.add("01");
		sSortedTopicCode.add("08");
		sSortedTopicCode.add("09");
		sSortedTopicCode.add("15");
		sSortedTopicCode.add("02");
		sSortedTopicCode.add("06");
		sSortedTopicCode.add("13");
		sSortedTopicCode.add("04");
		sSortedTopicCode.add("12");
		sSortedTopicCode.add("14");
		sSortedTopicCode.add("11");
		sSortedTopicCode.add("17");
		sSortedTopicCode.add("16");
		
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
	}
	
	public static Drawable getTopicTagDrawable(Context ctx, String topicCode) {
		Resources res = ctx.getResources();
		int resId = res.getIdentifier(
				String.format("ic_topic_tag_%s", topicCode), "drawable",
				ctx.getPackageName());
		Drawable drawable = res.getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}
	
	public static Drawable getTopicBgDrawable(Context ctx, String topicCode) {
		Resources res = ctx.getResources();
		int resId = res.getIdentifier(
				String.format("bg_topic_%s", topicCode), "drawable",
				ctx.getPackageName());
		Drawable drawable = res.getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}
	
	public static ArrayList<String> getTopicCodeList() {
		return sSortedTopicCode;
	}
	
	public static String getTopicString(String topicCode) {
		return sCodeTopicStrMap.get(topicCode);
	}
	
	public static void removeDownloadedCartoon(Context ctx, int cartoonId) {
		String path = PropertyMgr.getInstance().getCartoonPath(cartoonId);
		File file = new File(path);
		FileReaderUtil.deleteFiles(file);
		
		ContentValues values = new ContentValues();
		values.put(CARTOONS_COLUMN.IS_DOWNLOAD, 0);
		values.put(CARTOONS_COLUMN.DOWNLOAD_PROGRESS, 0);
		
		DatabaseMgr dbMgr = new DatabaseMgr(ctx);
		dbMgr.update(DatabaseMgr.MUU_CARTOONS_ALL_URL, values, String.format("%s=%d", CARTOONS_COLUMN.ID, cartoonId), null);
	}
}
