package com.muu.update;

/*package*/ class UpdateProgressEvent {
	public static final int sStatusBegin = 0;
	public static final int sStatusDownloading = 1;
	public static final int sStatusSucc = 2;
	public static final int sStatusFail = 3;
	
	public UpdateProgressEvent(int status, int progress) {
		this.status = status;
		this.progress = progress;
	}
	
	private final int progress;
	private final int status;
	public int getProgress() {
		return progress;
	}
	
	public int getStatus() {
		return status;
	}
}
