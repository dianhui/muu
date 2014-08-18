package com.muu.ui;

import com.muu.uidemo.R;
import com.muu.util.TempDataLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class ReadFinishDialog extends android.app.Dialog {

	private int mCartoonId;
	
	public ReadFinishDialog(Context context, int theme, int cartoonId) {
		super(context, theme);
		
		mCartoonId = cartoonId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.read_finish_layout);

		Bitmap bmp = new TempDataLoader().getCartoonCover(mCartoonId).get();
		if (bmp != null) {
			ImageView img = (ImageView)this.findViewById(R.id.imv_cartoon_cover);
			img.setImageBitmap(bmp);
		}
	}

}
