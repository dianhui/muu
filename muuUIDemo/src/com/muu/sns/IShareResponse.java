package com.muu.sns;

public interface IShareResponse {
	public void onShareSuccess();
	public void onShareFailed();
	public void onShareCanceled();
}
