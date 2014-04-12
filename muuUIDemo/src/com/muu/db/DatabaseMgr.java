package com.muu.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DatabaseMgr {
	private static final String TAG = "DatabaseMgr";
	private static final int DATA_BASE_VERSION = 1;
	private static final String DATA_BASE_NAME = "cartoon.db";
	private static final String TABLE_CARTOONS = "cartoons";
	private static final String TABLE_CHAPTERS = "chapters";
	private static final String TABLE_RECENT_READ = "recent_read";
	
	private static final String VIEW_RECENT_HISTORY = "v_recent_history";
	
	private DatabaseHelper mDbHelper = null;
	
	public interface CARTOONS_COLUMN {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String AUTHOR = "author";
		public static final String UPDATE_DATE = "update_date";
		public static final String ABSTRACT = "abstract";
		public static final String CATEGORY = "category";
		public static final String IS_COMPLETE = "is_complete";
	}
	
	public interface CHAPTERS_COLUMN {
		public static final String ID = "_id";
		public static final String INDEX = "idx";
		public static final String NAME = "name";
		public static final String CARTOON_ID = "cartoon_id";
		public static final String PAGE_COUNT = "page_count";
	}
	
	public interface RECENT_READ_COLUMN {
		public static final String ID = "_id";
		public static final String CARTOON_ID = "cartoon_id";
		public static final String CHAPTER_IDX = "chapter_idx";
		public static final String PAGE_IDX = "page_idx";
	}
	
	public interface RECENT_HISTORY_COLUMN {
		public static final String CARTOON_ID = "cartoon_id";
		public static final String CARTOON_NAME = "cartoon_name";
		public static final String CARTOON_AUTHOR = "cartoon_author";
		public static final String CHAPTER_IDX = "chapter_idx";
		public static final String PAGE_IDX = "page_idx";
	}
	
	public static final Uri MUU_CARTOONS_ALL_URL = Uri.parse(String.format(
			"content://%s", TABLE_CARTOONS));
	public static final Uri CHAPTER_ALL_URL = Uri.parse(String.format(
			"content://%s", TABLE_CHAPTERS));
	public static final Uri RECENT_READ_ALL_URL = Uri.parse(String.format(
			"content://%s", TABLE_RECENT_READ));
	public static final Uri RECENT_HISTORY_ALL_URL = Uri.parse(String.format(
			"content://%s", VIEW_RECENT_HISTORY));
	
	public DatabaseMgr(Context ctx) {
		mDbHelper = new DatabaseHelper(ctx);
	}
	
	public void closeDatabase() {
		mDbHelper.close();
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
		case MUU_CHAPTER_ALL:
			table = TABLE_CHAPTERS;
			break;
		case MUU_RECENT_READ_ALL:
			table = TABLE_RECENT_READ;
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
		Log.v(TAG, "Query uri=" + uri + ", match=" + match);
		
		String table = null;
		switch (match) {
		case MUU_CARTOON_ALL:
			table = TABLE_CARTOONS;
			break;
		case MUU_CARTOON_ID:
			table = TABLE_CARTOONS;
			selection = concatSelections(selection, String.format("(%s = %s)",
					CARTOONS_COLUMN.ID, uri.getLastPathSegment()));
			break;
		case MUU_CHAPTER_ALL:
			table = TABLE_CHAPTERS;
			break;
		case MUU_RECENT_READ_ALL:
			table = TABLE_RECENT_READ;
			break;
		case MUU_RECENT_HISTORY_ALL:
			table = VIEW_RECENT_HISTORY;
			break;
		default:
			break;
		}
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
	}
	
	public int update(Uri uri, ContentValues values, String selection,
	        String[] selectionArgs) {
		int match = sURLMatcher.match(uri);
		Log.v(TAG, "Update uri=" + uri + ", match=" + match);
		
		String table = null;
		switch (match) {
		case MUU_CARTOON_ALL:
			table = TABLE_CARTOONS;
			break;
		case MUU_CARTOON_ID:
			table = TABLE_CARTOONS;
			selection = concatSelections(selection, String.format("(%s = %s)",
					CARTOONS_COLUMN.ID, uri.getLastPathSegment()));
			break;
		case MUU_CHAPTER_ALL:
			table = TABLE_CHAPTERS;
			break;
		case MUU_RECENT_READ_ALL:
			table = TABLE_RECENT_READ;
			break;
		default:
			break;
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		return db.update(table, values, selection, selectionArgs);
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context ctx) {
			super(ctx, DATA_BASE_NAME, null, DATA_BASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createCartoonsTable = String
					.format("create table if not exists %s (%s integer primary key, %s text, %s text, %s text, %s text, %s text, %s integer);",
							TABLE_CARTOONS, CARTOONS_COLUMN.ID,
							CARTOONS_COLUMN.NAME, CARTOONS_COLUMN.AUTHOR,
							CARTOONS_COLUMN.UPDATE_DATE,
							CARTOONS_COLUMN.ABSTRACT, CARTOONS_COLUMN.CATEGORY,
							CARTOONS_COLUMN.IS_COMPLETE);
			db.execSQL(createCartoonsTable);
			
			String createChaptersTable = String
					.format("create table if not exists %s (%s integer primary key, %s integer, %s integer, %s text, %s integer, foreign key(%s) references %s(%s), UNIQUE(%s, %s));",
							TABLE_CHAPTERS, CHAPTERS_COLUMN.ID,
							CHAPTERS_COLUMN.INDEX, CHAPTERS_COLUMN.CARTOON_ID,
							CHAPTERS_COLUMN.NAME, CHAPTERS_COLUMN.PAGE_COUNT,
							CHAPTERS_COLUMN.CARTOON_ID, TABLE_CARTOONS,
							CARTOONS_COLUMN.ID, CHAPTERS_COLUMN.INDEX,
							CHAPTERS_COLUMN.CARTOON_ID);
			db.execSQL(createChaptersTable);
			
			String createRecentReadTable = String
					.format("create table if not exists %s (%s integer primary key, %s integer, %s integer, %s integer, foreign key(%s) references %s(%s), UNIQUE(%s));",
							TABLE_RECENT_READ, RECENT_READ_COLUMN.ID,
							RECENT_READ_COLUMN.CARTOON_ID,
							RECENT_READ_COLUMN.CHAPTER_IDX,
							RECENT_READ_COLUMN.PAGE_IDX,
							RECENT_READ_COLUMN.CARTOON_ID, TABLE_CARTOONS,
							CARTOONS_COLUMN.ID, RECENT_READ_COLUMN.CARTOON_ID);
			db.execSQL(createRecentReadTable);
			
			String createRecentHistoryView = String
					.format("create view if not exists %s as select %s.%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s from %s, %s where %s.%s=%s.%s",
							VIEW_RECENT_HISTORY,
							TABLE_CARTOONS, CARTOONS_COLUMN.ID, RECENT_HISTORY_COLUMN.CARTOON_ID,
							TABLE_CARTOONS, CARTOONS_COLUMN.NAME, RECENT_HISTORY_COLUMN.CARTOON_NAME,
							TABLE_CARTOONS, CARTOONS_COLUMN.AUTHOR, RECENT_HISTORY_COLUMN.CARTOON_AUTHOR,
							TABLE_RECENT_READ, RECENT_READ_COLUMN.CHAPTER_IDX, RECENT_HISTORY_COLUMN.CHAPTER_IDX,
							TABLE_RECENT_READ, RECENT_READ_COLUMN.PAGE_IDX, RECENT_HISTORY_COLUMN.PAGE_IDX,
							TABLE_RECENT_READ, TABLE_CARTOONS,
							TABLE_RECENT_READ, RECENT_READ_COLUMN.CARTOON_ID, TABLE_CARTOONS, CARTOONS_COLUMN.ID);
			db.execSQL(createRecentHistoryView);
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
	private static final int MUU_CHAPTER_ALL = 2;
	private static final int MUU_RECENT_READ_ALL = 3;
	private static final int MUU_RECENT_HISTORY_ALL = 4;
	
	private static final UriMatcher
		sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURLMatcher.addURI("cartoons", null, MUU_CARTOON_ALL);
		sURLMatcher.addURI("cartoons", "#", MUU_CARTOON_ID);
		sURLMatcher.addURI("chapters", null, MUU_CHAPTER_ALL);
		sURLMatcher.addURI("recent_read", null, MUU_RECENT_READ_ALL);
		sURLMatcher.addURI("v_recent_history", null, MUU_RECENT_HISTORY_ALL);
	}
	
	private static String concatSelections(String selection1, String selection2) {
		if (TextUtils.isEmpty(selection1)) {
			return selection2;
		} else if (TextUtils.isEmpty(selection2)) {
			return selection1;
		} else {
			return selection1 + " AND " + selection2;
		}
	}
}
