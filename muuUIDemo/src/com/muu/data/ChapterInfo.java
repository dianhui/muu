package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.muu.db.DatabaseMgr.CHAPTERS_COLUMN;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class ChapterInfo {
	private static final String TAG = "ChapterInfo";
	
	public int id;
	public int cartoonId;
	public String name;
	public int idx;
	public int pageCount;
	
	public ChapterInfo() {
	}
	
	public ChapterInfo(Cursor cur) {
		if (cur == null) {
			Log.d(TAG, "Invalid cursor.");
			return;
		}
		
		id = cur.getInt(cur.getColumnIndex(CHAPTERS_COLUMN.ID));
		cartoonId = cur.getInt(cur.getColumnIndex(CHAPTERS_COLUMN.CARTOON_ID));
		name = cur.getString(cur.getColumnIndex(CHAPTERS_COLUMN.NAME));
		idx = cur.getInt(cur.getColumnIndex(CHAPTERS_COLUMN.INDEX));
		pageCount = cur.getInt(cur.getColumnIndex(CHAPTERS_COLUMN.PAGE_COUNT));
	}
	
	/**
	 * {"id":81660,"cnt":"1","createdTime":"2014-04-17 16:56:17.0",
	 * 	"updatedTime":"","name":"xxxx"}
	 */
	public ChapterInfo(JSONObject chapterInfo, int cartoonId, int idx) {
		try {
			id = chapterInfo.getInt("id");
			name = chapterInfo.getString("name");
			pageCount = Integer.parseInt(chapterInfo.getString("cnt"));
			
			this.cartoonId = cartoonId;
			this.idx = idx;
		} catch (JSONException e) {
			Log.d(TAG, e.getMessage());
		}
	}
	
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(CHAPTERS_COLUMN.ID, id);
		values.put(CHAPTERS_COLUMN.CARTOON_ID, cartoonId);
		values.put(CHAPTERS_COLUMN.NAME, name);
		values.put(CHAPTERS_COLUMN.INDEX, idx);
		values.put(CHAPTERS_COLUMN.PAGE_COUNT, pageCount);
		return values;
	}
}
