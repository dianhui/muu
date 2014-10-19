package com.muu.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.muu.cartoon.test.R;
import com.muu.update.AppUpdateService;
import com.muu.util.StorageUtil;

public class LiveUpadateDialog extends Activity implements OnClickListener{
	public static void startAppUpdateDialog(Context ctx, String appUrl) {
		Intent intent = new Intent();
		intent.setClass(ctx, LiveUpadateDialog.class);
		intent.putExtra(AppUpdateService.sAppUrl, appUrl);
		ctx.startActivity(intent);
	}
	
	private String mAppUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.liveupdate_dialog_layout);
		setFinishOnTouchOutside(true);
		
		Button btn = (Button)this.findViewById(R.id.btn_cancel);
		btn.setOnClickListener(this);
		
		btn = (Button)this.findViewById(R.id.btn_ok);
		btn.setOnClickListener(this);
		
		mAppUrl = getIntent().getStringExtra(AppUpdateService.sAppUrl);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			this.finish();
			break;
		case R.id.btn_ok:
			startDownloadApp();
			this.finish();
			break;
		default:
			break;
		}
	}
	
	private void startDownloadApp() {
		if (TextUtils.isEmpty(mAppUrl)) {
			Log.d("LiveUpadateDialog", "app remote path is null.");
			return;
		}
		if (!StorageUtil.hasSDCard()) {
			Toast.makeText(this, getString(R.string.no_extral_storage),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		AppUpdateService.startService(this, mAppUrl);
	}
}
