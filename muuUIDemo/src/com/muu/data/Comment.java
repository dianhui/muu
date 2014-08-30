package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.muu.db.DatabaseMgr.COMMENTS_COLUMN;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class Comment {
	private static final String TAG = "Comment";
	
	public int id;
	public String user;
	public String createTime;
	public String content;
	public int cartoonId;

	public Comment() {}
	
	public Comment(Cursor cur) {
		if (cur == null) {
			Log.d(TAG, "Invalid cursor.");
			return;
		}
		
		id = cur.getInt(cur.getColumnIndex(COMMENTS_COLUMN.ID));
		user = cur.getString(cur.getColumnIndex(COMMENTS_COLUMN.USER));
		createTime = cur.getString(cur.getColumnIndex(COMMENTS_COLUMN.CREATE_TIME));
		content = cur.getString(cur.getColumnIndex(COMMENTS_COLUMN.CONTENT));
		cartoonId = cur.getInt(cur.getColumnIndex(COMMENTS_COLUMN.CARTOON_ID));
	}
	
	public Comment(JSONObject jsonComment, int cartoonId) {
		this.cartoonId = cartoonId;
		
		try {
			id = jsonComment.getInt("id");
			user = jsonComment.getString("user");
			createTime = jsonComment.getString("createdTime");
			content = jsonComment.getString("comment");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean equals(Comment comment) {
		if (this.id == comment.id && this.createTime.equals(comment.createTime)
				&& this.user.equals(comment.user)
				&& this.content.equals(comment.content)
				&& this.cartoonId == comment.cartoonId) {
			return true;
		}
		return false;
	}
	
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(COMMENTS_COLUMN.ID, id);
		values.put(COMMENTS_COLUMN.USER, user);
		values.put(COMMENTS_COLUMN.CREATE_TIME, createTime);
		values.put(COMMENTS_COLUMN.CONTENT, content);
		values.put(COMMENTS_COLUMN.CARTOON_ID, cartoonId);
		return values;
	}
	
}
