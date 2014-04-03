package com.muu.ui;

import com.muu.uidemo.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ReadFinishDialog extends android.app.Dialog {

	public ReadFinishDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.read_finish_layout);

		ImageView closeBtn = (ImageView) this.findViewById(R.id.imv_close);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReadFinishDialog.this.dismiss();
			}
		});
	}

}
