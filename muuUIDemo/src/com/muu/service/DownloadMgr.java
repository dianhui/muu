package com.muu.service;

import com.muu.data.CartoonInfo;
import com.muu.db.DatabaseMgr;
import com.muu.db.DatabaseMgr.CARTOONS_COLUMN;
import com.muu.util.TempDataLoader;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

public class DownloadMgr implements DownloaderListener {
	public static enum DownloadStatus {
		OK("OK"),
		DOWNLOADING("downloading"),
		DOWANLOADED("downloaded");
		
		public String name;
		DownloadStatus(String name) {
			this.name = name;
		}
	}

	private Context mCtx;
	private DatabaseMgr mDbMgr;
	private SparseArray<DownloadWorker> mDownloadThreads = null;
	private static DownloadMgr mInstanse = new DownloadMgr();

	private DownloadMgr(){}
	
	public static DownloadMgr getInstanse() {
		return mInstanse;
	}
	
	public DownloadStatus download(Context ctx, CartoonInfo info) {
		if (mCtx == null) {
			mCtx = ctx;
		}
		
		if (mDbMgr == null) {
			mDbMgr = new DatabaseMgr(ctx);
		}
		
		if (mDownloadThreads == null) {
			mDownloadThreads = new SparseArray<DownloadWorker>();
		}
		
		if (isDownloading(info.id)) {
			return DownloadStatus.DOWNLOADING;
		}
		
		if (isDownloaded(info.id)) {
			return DownloadStatus.DOWANLOADED;
		}
		
		DownloadWorker worker = mDownloadThreads.get(info.id);
		if (worker == null) {
			worker = new DownloadWorker(ctx, info.id, info.coverUrl, this);
			mDownloadThreads.append(info.id, worker);
		}
		
		new Thread(worker).start();
		return DownloadStatus.OK;
	}
	
	public void cancel(int cartoonId) {
		if (mDownloadThreads == null) {
			return;
		}
		
		DownloadWorker worker = mDownloadThreads.get(cartoonId);
		if (worker == null) {
			return;
		}
		
		worker.cancel();
		mDownloadThreads.remove(cartoonId);
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

	@Override
	public void onCanceled(int cartoonId) {
		TempDataLoader.removeDownloadedCartoon(mCtx, cartoonId);
	}

	@Override
	public void onDownloadSuccess(int cartoonId) {
	}

	@Override
	public void onDownloadFailed(int cartoonId) {
	}
}
