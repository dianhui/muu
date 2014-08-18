package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;
//import com.muu.db.DatabaseMgr.CHAPTERS_COLUMN;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class CartoonInfo {
	private static final String TAG = "CartoonInfo";
	
	public int id;
	public String name;
	public String author;
	public String updateDate;
	public String abst;
	public String topicCode;
	public int isComplete;
	public String coverUrl;
	public int chapterCount;
	public int size;  //in Bytes
	
	public String toString() {
		return id + "|" + name + "|" + author + "|" + updateDate + "|" + abst + "|"
				+ topicCode + "|" + isComplete + "|" + coverUrl + "|"
				+ chapterCount + "|" + size;
	}

	public CartoonInfo() {
	}
	
	public CartoonInfo(Cursor cur) {
		if (cur == null) {
			Log.d(TAG, "Invalid cursor.");
			return;
		}
		
		id = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.ID));
		name = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.NAME));
		author = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.AUTHOR));
		updateDate = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.UPDATE_DATE));
		abst = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.ABSTRACT));
		topicCode = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.CATEGORY));
		isComplete = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.IS_COMPLETE));
		chapterCount = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.CHAPTER_COUNT));
		size = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.SIZE));
		coverUrl = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.COVER_URL));
	}
	
	/**
	 * Format of cartoonInfo:
	 * {"author":"author of cartoon","categoryCode":"006","chapterCount":239,"cover":"url of cover","createdTime":"2014-06-04 16:50:17.0",
	 * 		"id":12918,"introduction":"introduction of cartoon","name":"name of cartoon","progress":"false","topicCode":"11",
	 * 		"updatedTime":"2014-07-05 17:34:58.0"}
	 * 
	 * */
	public CartoonInfo(JSONObject cartoonInfo) {
		try {
			id = cartoonInfo.getInt("id");
			updateDate = cartoonInfo.getString("updatedTime");
			isComplete = cartoonInfo.getBoolean("progress") ? 1 : 0;
			name = cartoonInfo.getString("name").replaceAll("\\\\", "");
			author = cartoonInfo.getString("author").replaceAll("\\\\", "");
			abst = cartoonInfo.getString("introduction").replaceAll("\\\\", "");
			topicCode = cartoonInfo.getString("topicCode").replaceAll("\\\\", "");
			coverUrl = cartoonInfo.getString("cover").replaceAll("\\\\", "");
			chapterCount = cartoonInfo.getInt("chapterCount");
//			size = cartoonInfo.getInt("size");
			size = 0;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Boolean equals(CartoonInfo cartoonInfo) {
		if (this.id == cartoonInfo.id && this.name.equals(cartoonInfo.name)
				&& this.author.equals(cartoonInfo.author)
				&& this.updateDate.equals(cartoonInfo.updateDate)
				&& this.abst.equals(cartoonInfo.abst)
				&& this.topicCode.equals(cartoonInfo.topicCode)
				&& this.isComplete == cartoonInfo.isComplete
				&& this.chapterCount == cartoonInfo.chapterCount
				&& this.coverUrl.equals(cartoonInfo.coverUrl)) {
			return true;
		}
		return false;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(CARTOONS_COLUMN.ID, id);
		values.put(CARTOONS_COLUMN.NAME, name);
		values.put(CARTOONS_COLUMN.AUTHOR, author);
		values.put(CARTOONS_COLUMN.UPDATE_DATE, updateDate);
		values.put(CARTOONS_COLUMN.ABSTRACT, abst);
		values.put(CARTOONS_COLUMN.CATEGORY, topicCode);
		values.put(CARTOONS_COLUMN.IS_COMPLETE, isComplete);
		values.put(CARTOONS_COLUMN.CHAPTER_COUNT, chapterCount);
		values.put(CARTOONS_COLUMN.SIZE, size);
		values.put(CARTOONS_COLUMN.COVER_URL, coverUrl);
		
		return values;
	}
}
