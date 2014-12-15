package com.muu.ui;

import com.muu.cartoons.R;

import android.os.Bundle;

public class AboutDialogActivity extends StatisticsBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about_dialog_layout);
		setFinishOnTouchOutside(true);
	}
}
