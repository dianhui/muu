package com.muu.service;

public interface DownloaderListener {
	public void onDownloadSuccess(int cartoonId);
	public void onDownloadFailed(int cartoonId);
	public void onCanceled(int cartoonId);
}
