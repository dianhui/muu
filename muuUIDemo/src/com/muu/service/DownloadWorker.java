package com.muu.service;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;

import com.muu.data.ChapterInfo;
import com.muu.data.ImageInfo;
import com.muu.db.DatabaseMgr;
import com.muu.server.MuuServerWrapper;
import com.muu.util.TempDataLoader;

public class DownloadWorker implements Runnable {
	private Context mCtx;
	private int mCartoonId;
	private String mCoverUrl;
	private DatabaseMgr mDbMgr;
	private MuuServerWrapper mServerWrapper;
	private Boolean mIsCancel;
	private DownloaderListener mListener;
	
	private int mTotalPageCount;
	private int mDownloadedPageCount;
	
	public DownloadWorker(Context ctx, int cartoonId, String coverUrl, DownloaderListener
			listener) {
		mCtx = ctx.getApplicationContext();
		mCartoonId = cartoonId;
		mCoverUrl = coverUrl;
		mDbMgr = new DatabaseMgr(ctx);
		mListener = listener;
	}
	
	@Override
	public void run() {
		mIsCancel = false;
		downloadCartoon();
	}
	
	public void cancel() {
		if (mIsCancel) {
			return;
		}
		
		mIsCancel = true;
	}
	
	private void downloadCartoon() {
		updateDownloadState();
		updateDownloadProgress(0);
		
		downloadCover();
		
		ArrayList<ChapterInfo> chapters = new TempDataLoader().getChapters(
				mCtx, mCartoonId);
		mDownloadedPageCount = 0;
		mTotalPageCount = getTotalPageCount(chapters);
		for(ChapterInfo chapterInfo : chapters) {
			if (mIsCancel) {
				mListener.onCanceled(mCartoonId);
				return;
			}
			downloadChapter(chapterInfo);
		}
		
		updateDownloadProgress(100);
	}
	
	private void updateDownloadState() {
		ContentValues values = new ContentValues();
		values.put(DatabaseMgr.CARTOONS_COLUMN.IS_DOWNLOAD, 1);
		
		mDbMgr.update(DatabaseMgr.MUU_CARTOONS_ALL_URL, values,
				DatabaseMgr.CARTOONS_COLUMN.ID + "=" + mCartoonId, null);
	}
	
	private void updateDownloadProgress(int progress) {
		ContentValues values = new ContentValues();
		values.put(DatabaseMgr.CARTOONS_COLUMN.DOWNLOAD_PROGRESS, progress);
		
		mDbMgr.update(DatabaseMgr.MUU_CARTOONS_ALL_URL, values,
				DatabaseMgr.CARTOONS_COLUMN.ID + "=" + mCartoonId, null);
	}
	
	private int getTotalPageCount(ArrayList<ChapterInfo> chapters) {
		int totalPageCount = 0;
		for (ChapterInfo chapterInfo : chapters) {
			totalPageCount += chapterInfo.pageCount;
		}
		return totalPageCount;
	}
	
	private void downloadCover() {
		if (mServerWrapper == null) {
			mServerWrapper = new MuuServerWrapper(mCtx);
		}
		
		mServerWrapper.downloadCartoonCover(mCartoonId, mCoverUrl);
	}
	
	private void downloadChapter(ChapterInfo chapter) {
		if (mServerWrapper == null) {
			mServerWrapper = new MuuServerWrapper(mCtx);
		}
		ArrayList<ImageInfo> chapterImgInfo = mServerWrapper.getChapterImgInfo(chapter.id,
				0, chapter.pageCount);
		if (chapterImgInfo == null) {
			return;
		}
		
		for (ImageInfo imageInfo : chapterImgInfo) {
			if (mIsCancel) {
				mListener.onCanceled(mCartoonId);
				return;
			}
			mServerWrapper.downloadCartoonImage(mCartoonId, imageInfo.id, imageInfo.imgUrl);
			
			mDownloadedPageCount++;
			updateDownloadProgress(mDownloadedPageCount*100/mTotalPageCount);
		}
	}
}
