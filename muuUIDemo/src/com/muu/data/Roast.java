package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.muu.db.DatabaseMgr.ROASTS_COLUMN;

//ÍÂ²Û
public class Roast {
	private static final String TAG = "Roast";

	public int id;
	public String content;
	public String createTime; 
	public int cartoonId;
	
	public Roast() {}
	
	public Roast(Cursor cur) {
		if (cur == null) {
			Log.d(TAG, "Invalid cursor.");
			return;
		}
		
		id = cur.getInt(cur.getColumnIndex(ROASTS_COLUMN.ID));
		createTime = cur.getString(cur.getColumnIndex(ROASTS_COLUMN.CREATE_TIME));
		content = cur.getString(cur.getColumnIndex(ROASTS_COLUMN.CONTENT));
		cartoonId = cur.getInt(cur.getColumnIndex(ROASTS_COLUMN.CARTOON_ID));
	}
	
	public Roast(JSONObject jsonRoast, int cartoonId) {
		this.cartoonId = cartoonId;
		try {
			id = jsonRoast.getInt("id");
			createTime = jsonRoast.getString("timestamp");
			content = jsonRoast.getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean equals(Roast roast) {
		if (this.id == roast.id && this.createTime.equals(roast.createTime)
				&& this.content.equals(roast.content)
				&& this.cartoonId == roast.cartoonId) {
			return true;
		}
		return false;
	}
	
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(ROASTS_COLUMN.ID, id);
		values.put(ROASTS_COLUMN.CREATE_TIME, createTime);
		values.put(ROASTS_COLUMN.CONTENT, content);
		values.put(ROASTS_COLUMN.CARTOON_ID, cartoonId);
		return values;
	}
}
