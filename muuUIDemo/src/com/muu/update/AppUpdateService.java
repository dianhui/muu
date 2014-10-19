package com.muu.update;

import java.io.File;

import com.muu.cartoon.test.R;
import com.muu.util.PropertyMgr;

import de.greenrobot.event.EventBus;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

public class AppUpdateService extends Service {
	public static final String sAppUrl = "app_url_key";
	private String mAppUrl;
	
	private static boolean sDownloading = false;
	public static void startService(Context ctx, String appUrl) {
		Intent intent = new Intent();
		intent.setClass(ctx, AppUpdateService.class);
		intent.putExtra(sAppUrl, appUrl);
		ctx.startService(intent);
	}

	@Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null == intent) {
			return START_NOT_STICKY;
		}
		mAppUrl = intent.getStringExtra(sAppUrl);

		// showDownloadNotification();
		// EventBus.getDefault().post(new DownloadNewVersionEvent(mUrl,
		// mAppName));
		EventBus.getDefault().post(new DownloadAppEvent());
		return Service.START_NOT_STICKY;
	}
	
	@Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }
	
	public void onEventBackgroundThread(DownloadAppEvent event) {
		AppDownloader.downloadApp(this, mAppUrl);
	}
	
	public void onEventMainThread(UpdateProgressEvent event) {
		switch (event.getStatus()) {
		case UpdateProgressEvent.sStatusBegin:
		case UpdateProgressEvent.sStatusDownloading:
			sDownloading = true;
			Toast.makeText(this, this.getString(R.string.download_started), Toast.LENGTH_LONG).show();
			break;
		case UpdateProgressEvent.sStatusSucc:
			sDownloading = false;
			Toast.makeText(this, this.getString(R.string.succ_to_download), Toast.LENGTH_LONG).show();
			installNewVersion();
			break;
		case UpdateProgressEvent.sStatusFail:
			sDownloading = false;
			Toast.makeText(this, this.getString(R.string.fail_to_download), Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
	}
	
	public static boolean isDownloading() {
		return sDownloading;
	}
	
	private void installNewVersion() {
		Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri file = Uri.fromFile(new File(PropertyMgr.getInstance().getUpdatePath(), AppDownloader.sNewAppFileName));
        intent.setDataAndType(file, "application/vnd.android.package-archive");
        this.startActivity(intent);
	}

	private class DownloadAppEvent {}
}
