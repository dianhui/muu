package com.muu.ui;

import com.muu.uidemo.R;
import com.muu.widget.TouchImageView;
import com.muu.widget.TouchImageView.OnGestureListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ReadPageActivity extends Activity implements OnGestureListener {
	private RelativeLayout mActionBarLayout = null;
	private RelativeLayout mPageIdxLayout = null;
	private RelativeLayout mBottomLayout = null;
	private TouchImageView mContentImage = null;
	private Boolean mTouchedMode = false;
	private Boolean mRecomment = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_page_layout);
		
		
		mActionBarLayout = (RelativeLayout)this.findViewById(R.id.rl_action_bar);
		mPageIdxLayout = (RelativeLayout)this.findViewById(R.id.rl_page_index);
		mBottomLayout = (RelativeLayout)this.findViewById(R.id.rl_bottom_chapter);
		
		mContentImage = (TouchImageView)this.findViewById(R.id.imv_content);
		mContentImage.setSwipObserver(this);
		
		final ImageButton recommentBtn = (ImageButton)this.findViewById(R.id.imv_btn_recomment);
		recommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecomment = !mRecomment;
				
				if (mRecomment) {
					recommentBtn.setBackgroundResource(R.drawable.ic_recomment_selected);
				} else {
					recommentBtn.setBackgroundResource(R.drawable.ic_recomment_normal);
				}
			}
		});
		
		RelativeLayout backBtn = (RelativeLayout)this.findViewById(R.id.rl_back);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ReadPageActivity.this.finish();
			}
		});
	}

	@Override
	public void onSwipPrevious() {
		Toast.makeText(getApplicationContext(), "onSwipPrevious",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSwipNext() {
		Toast.makeText(getApplicationContext(), "onSwipNext",
				Toast.LENGTH_SHORT).show();
		
		ReadFinishDialog dialog = new ReadFinishDialog(this,
				R.style.FloatDialogTheme);
		dialog.show();
	}

	@Override
	public void onSingleTapUp() {
		if (mTouchedMode) {
			mActionBarLayout.setVisibility(View.VISIBLE);
			mPageIdxLayout.setVisibility(View.GONE);
			mBottomLayout.setVisibility(View.VISIBLE);
        } else {
        	mActionBarLayout.setVisibility(View.GONE);
    		mPageIdxLayout.setVisibility(View.VISIBLE);
    		mBottomLayout.setVisibility(View.GONE);
        }
		mTouchedMode = !mTouchedMode;
	}
}
