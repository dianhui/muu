package com.muu.ui;

import com.muu.cartoon.test.R;

import android.app.Activity;
import android.os.Bundle;

public class AboutDialogActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about_dialog_layout);
		setFinishOnTouchOutside(true);
	}
}
