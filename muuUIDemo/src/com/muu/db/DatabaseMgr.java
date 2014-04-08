package com.muu.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DatabaseMgr {
	private static final String TAG = "DatabaseMgr";
	private static final int DATA_BASE_VERSION = 1;
	private static final String DATA_BASE_NAME = "cartoon.db";
	private static final String TABLE_CARTOONS = "cartoons";
	
	private DatabaseHelper mDbHelper = null;
	
	public interface CARTOONS_COLUMN {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String AUTHOR = "author";
		public static final String UPDATE_DATE = "update_date";
		public static final String ABSTRACT = "abstract";
		public static final String CATEGORY = "category";
	}
	
	public static final Uri MUU_CARTOONS_ALL = Uri.parse(String.format(
			"content://%s", TABLE_CARTOONS));
	
	public DatabaseMgr(Context ctx) {
		mDbHelper = new DatabaseHelper(ctx);
	}
	
	/**
	 * Input input values to related table.
	 * 
	 * */
	public Uri insert(Uri uri, ContentValues values) {
		int match = sURLMatcher.match(uri);
		Log.v(TAG, "Insert uri=" + uri + ", match=" + match);
		
		String table = null;
		switch (match) {
		case MUU_CARTOON_ALL:
		case MUU_CARTOON_ID:
			table = TABLE_CARTOONS;
			break;

		default:
			table = TABLE_CARTOONS;
			break;
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long rowId = db.insert(table, null, values);
		if (rowId > 0) {
			return Uri.parse(table + "/" + rowId);
		} else {
			Log.d(TAG, "Insert: failed! " + values.toString());
		}
		return null;
	}
	
	/**
	 * Query database table.
	 * 
	 * */
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sURLMatcher.match(uri);
		Log.v(TAG, "Insert uri=" + uri + ", match=" + match);
		
		String table = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (match) {
		case MUU_CARTOON_ALL:
			table = TABLE_CARTOONS;
			break;
		case MUU_CARTOON_ID:
			table = TABLE_CARTOONS;
			queryBuilder.appendWhere(String.format("(%s = %s)",
					CARTOONS_COLUMN.ID, uri.getLastPathSegment()));
			break;

		default:
			break;
		}
		
		queryBuilder.setTables(table);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		return queryBuilder.query(db, projection, selection, selectionArgs, null,
				null, String.format("%s DESC", CARTOONS_COLUMN.ID));
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context ctx) {
			super(ctx, DATA_BASE_NAME, null, DATA_BASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createCartoonsTable = String
					.format("create table if not exists %s (%s integer primary key UNIQUE, %s text, %s text, %s text, %s text, %s text);",
							TABLE_CARTOONS, CARTOONS_COLUMN.ID,
							CARTOONS_COLUMN.NAME, CARTOONS_COLUMN.AUTHOR,
							CARTOONS_COLUMN.UPDATE_DATE,
							CARTOONS_COLUMN.ABSTRACT, CARTOONS_COLUMN.CATEGORY);
			db.execSQL(createCartoonsTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, String.format("upgrade database:  ? -> ?"
					, oldVersion, newVersion));

			String dropCartoonsTable = "drop table if exists " + TABLE_CARTOONS;
			db.execSQL(dropCartoonsTable);
		}
	}
	
	private static final int MUU_CARTOON_ALL = 0;
	private static final int MUU_CARTOON_ID = 1;
	
	private static final UriMatcher
		sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURLMatcher.addURI("cartoons", null, MUU_CARTOON_ALL);
		sURLMatcher.addURI("cartoons", "#", MUU_CARTOON_ID);
	}
	
//	private static String concatSelections(String selection1, String selection2) {
//		if (TextUtils.isEmpty(selection1)) {
//			return selection2;
//		} else if (TextUtils.isEmpty(selection2)) {
//			return selection1;
//		} else {
//			return selection1 + " AND " + selection2;
//		}
//	}
}
