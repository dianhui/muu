package com.muu.ui;

import java.util.ArrayList;

import com.muu.cartoon.test.R;
import com.muu.data.UpdateInfo;
import com.muu.server.MuuServerWrapper;
import com.muu.update.AppUpdateService;
import com.muu.util.PkgMrgUtil;
import com.muu.util.StorageUtil;
import com.muu.util.StorageUtil.StorageInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private ProgressBar mChkUpdateProgressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity_layout);
		
		setupActionBar();
		setupContentView();
	}
	
	private void setupActionBar() {
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.INVISIBLE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.INVISIBLE);
		
		ImageButton recentButton = (ImageButton) this
				.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.INVISIBLE);
		
		TextView title = (TextView)this.findViewById(R.id.tv_action_title);
		title.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.settings));
	}
	
	private void setupContentView() {
		mChkUpdateProgressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
		
		TextView tv = (TextView)this.findViewById(R.id.tv_select_path);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSelectPathDialog();
			}
		});
		
		tv = (TextView)this.findViewById(R.id.tv_check_update);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AppUpdateService.isDownloading()) {
					Toast.makeText(SettingsActivity.this, SettingsActivity.this.getString(R.string.download_in_process),
							Toast.LENGTH_LONG).show();
					return;
				}
				
				new CheckUpdateTask().execute();
			}
		});
		
		tv = (TextView)this.findViewById(R.id.tv_about);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAboutDialog();
			}
		});
	}
	
	private void showSelectPathDialog() {
		ArrayList<StorageInfo> list = StorageUtil.getStorageList();
		if (list == null || list.size() < 1) {
			Toast.makeText(this, this.getString(R.string.no_extral_storage),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent();
		intent.setClass(this, SelectPathDialogActivity.class);
		this.startActivity(intent	);
	}
	
	private void showAboutDialog() {
		Intent intent = new Intent();
		intent.setClass(this, AboutDialogActivity.class);
		this.startActivity(intent	);
	}
	
	private class CheckUpdateTask extends AsyncTask<Void, Integer, UpdateInfo> {

		@Override
		protected void onPreExecute() {
			mChkUpdateProgressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected UpdateInfo doInBackground(Void... params) {
			MuuServerWrapper muuServerWrapper = new MuuServerWrapper(SettingsActivity.this);
			return muuServerWrapper.getUpdateInfo(PkgMrgUtil.getAppVersion(SettingsActivity.this));
		}
		
		@Override
		protected void onPostExecute(UpdateInfo info) {
			mChkUpdateProgressBar.setVisibility(View.GONE);
			
			if (info == null || !info.hasUpdate) {
				Toast.makeText(SettingsActivity.this,
						SettingsActivity.this.getString(R.string.no_update),
						Toast.LENGTH_SHORT).show();
				return;
			}

			LiveUpadateDialog.startAppUpdateDialog(SettingsActivity.this, info.updatePath);
		}
	}
}