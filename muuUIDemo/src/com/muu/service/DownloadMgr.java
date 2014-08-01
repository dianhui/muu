package com.muu.service;

import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;

import android.content.Context;
import android.database.Cursor;

public class DownloadMgr {
	public static enum DownloadStatus {
		OK("OK"),
		DOWNLOADING("downloading"),
		DOWANLOADED("downloaded");
		
		public String name;
		DownloadStatus(String name) {
			this.name = name;
		}
	}
	
	private DatabaseMgr mDbMgr;
	public DownloadStatus download(Context ctx, int cartoonId) {
		if (mDbMgr == null) {
			mDbMgr = new DatabaseMgr(ctx);
		}
		
		if (isDownloading(cartoonId)) {
			return DownloadStatus.DOWNLOADING;
		}
		
		if (isDownloaded(cartoonId)) {
			return DownloadStatus.DOWANLOADED;
		}
		
		new Thread(new DownloadWorker(ctx, cartoonId)).start();
		
		return DownloadStatus.OK;
	}
	
	private Boolean isDownloading(int cartoonId) {
		Cursor cur = mDbMgr.query(DatabaseMgr.MUU_CARTOONS_ALL_URL, null,
				CARTOONS_COLUMN.ID + "=" + cartoonId, null, null);
		if (cur == null) {
			return false;
		}
		
		if (!cur.moveToFirst()) {
			cur.close();
			return false;
		}
		
		int isDownload = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.IS_DOWNLOAD));
		int downloadProgress = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.DOWNLOAD_PROGRESS));
		cur.close();
		
		return (isDownload == 1) && (downloadProgress < 100);
	}
	
	private Boolean isDownloaded(int cartoonId) {
		Cursor cur = mDbMgr.query(DatabaseMgr.MUU_CARTOONS_ALL_URL, null,
				CARTOONS_COLUMN.ID + "=" + cartoonId, null, null);
		if (cur == null) {
			return false;
		}
		
		if (!cur.moveToFirst()) {
			cur.close();
			return false;
		}
		
		int isDownload = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.IS_DOWNLOAD));
		int downloadProgress = cur.getInt(cur.getColumnIndex(CARTOONS_COLUMN.DOWNLOAD_PROGRESS));
		cur.close();
		
		return (isDownload == 1) && (downloadProgress == 100);
		
	}
}
