package com.muu.data;

import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class CartoonInfo {
	private static final String TAG = "CartoonInfo";
	
	public int id;
	public String name;
	public String author;
	public String date;
	public String abst;
	public String category;
	public int isComplete;
	
	public String toString() {
		return id + "|" + name + "|" + author + "|" + date + "|" + abst + "|"
				+ category + "|" + isComplete;
	}

	public CartoonInfo() {
	}
	
	public CartoonInfo(Cursor cur) {
		if (cur == null) {
			Log.d(TAG, "Invalid cursor.");
			return;
		}
		
		if (!cur.moveToFirst()) {
			Log.d(TAG, "Invalid cursor.");
			cur.close();
			return;
		}
		
		id = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.ID));
		name = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.NAME));
		author = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.AUTHOR));
		date = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.UPDATE_DATE));
		abst = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.ABSTRACT));
		category = cur.getString(cur.getColumnIndex(CARTOONS_COLUMN.CATEGORY));
		isComplete = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.IS_COMPLETE));
		
		cur.close();
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(CARTOONS_COLUMN.ID, id);
		values.put(CARTOONS_COLUMN.NAME, name);
		values.put(CARTOONS_COLUMN.AUTHOR, author);
		values.put(CARTOONS_COLUMN.UPDATE_DATE, date);
		values.put(CARTOONS_COLUMN.ABSTRACT, abst);
		values.put(CARTOONS_COLUMN.CATEGORY, category);
		values.put(CARTOONS_COLUMN.IS_COMPLETE, isComplete);
		
		return values;
	}
}
