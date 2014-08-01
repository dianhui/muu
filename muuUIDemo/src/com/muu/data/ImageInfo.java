package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.muu.db.DatabaseMgr.IMAGES_COLUMN;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class ImageInfo {
	private static final String TAG = "ImageInfo";
	
	public int id;
	public String imgUrl;
	public int chapterId;
	
	public ImageInfo(JSONObject jsonImgInfo, int chapterId) {
		try {
			id = jsonImgInfo.getInt("id");
			imgUrl = jsonImgInfo.getString("url").replaceAll("\\\\", "");
			this.chapterId = chapterId;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ImageInfo(Cursor cur){
		if (cur == null) {
			Log.d(TAG, "Invalid cursor.");
			return;
		}
		
		id = cur.getInt(cur.getColumnIndex(IMAGES_COLUMN.ID));
		imgUrl = cur.getString(cur.getColumnIndex(IMAGES_COLUMN.URL));
		chapterId = cur.getInt(cur.getColumnIndex(IMAGES_COLUMN.CHAPTER_ID));
	}
	
	public Boolean equals(ImageInfo info) {
		if (this.id == info.id && imgUrl.equals(info.imgUrl)) {
			return true;
		}
		
		return false;
	}
	
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(IMAGES_COLUMN.ID, this.id);
		values.put(IMAGES_COLUMN.URL, this.imgUrl);
		values.put(IMAGES_COLUMN.CHAPTER_ID, this.chapterId);
		return values;
	}
	
	public String toString() {
		return String.format("%d|%s|%d", id, imgUrl, chapterId);
	}
}
