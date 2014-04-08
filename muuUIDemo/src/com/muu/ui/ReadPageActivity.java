package com.muu.ui;

import com.muu.uidemo.R;
import com.muu.util.PkgMrgUtil;
import com.muu.widget.TouchImageView;
import com.muu.widget.TouchImageView.OnGestureListener;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ReadPageActivity extends Activity implements OnGestureListener {
	private RelativeLayout mActionBarLayout = null;
	private RelativeLayout mPageIdxLayout = null;
	private RelativeLayout mBottomLayout = null;
	private TouchImageView mContentImage = null;
	private Boolean mTouchedMode = false;
	private Boolean mRecomment = false;
	private View mShareDropView = null;
	private PopupWindow mSharePopup = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_page_layout);
		
		setupActionBar();
		setupContentView();
		setupBottomView();
		
	}

	private void setupActionBar() {
		mActionBarLayout = (RelativeLayout)this.findViewById(R.id.rl_action_bar);
		RelativeLayout backBtn = (RelativeLayout)this.findViewById(R.id.rl_back);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ReadPageActivity.this.finish();
			}
		});
		
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
		
		setupDropdownView();
	}
	
	private void setupDropdownView() {
		boolean isSinaWbInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.SINA_WEIBO_PKG);
		boolean isQQWbInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.TENCENT_WEIBO_PKG);
		boolean isQQInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.QQ_PKG);
		boolean isWeichatInstalled = PkgMrgUtil.isPkgInstalled(this, PkgMrgUtil.WEIXIN_PKG);

		final ImageButton shareBtn = (ImageButton) this
				.findViewById(R.id.imv_btn_share);
		if (!isSinaWbInstalled && !isQQWbInstalled && !isQQInstalled
				&& !isWeichatInstalled) {
			shareBtn.setVisibility(View.GONE);
			return;
		}
		
		mShareDropView = LayoutInflater.from(this).inflate(
				R.layout.share_drop_down_layout, null);
		ImageView imv = (ImageView) mShareDropView
				.findViewById(R.id.imv_sina_weibo);
		if (isSinaWbInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		imv = (ImageView)mShareDropView.findViewById(R.id.imv_qq_weibo);
		if (isQQWbInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		imv = (ImageView)mShareDropView.findViewById(R.id.imv_qq);
		if (isQQInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		imv = (ImageView)mShareDropView.findViewById(R.id.imv_wechat);
		if (isWeichatInstalled) {
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setVisibility(View.GONE);
		}
		
		mSharePopup = new PopupWindow(mShareDropView,
		        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mSharePopup.setTouchable(true);
		mSharePopup.setOutsideTouchable(true);
		mSharePopup.setBackgroundDrawable(new ColorDrawable(0));
		
		shareBtn.setVisibility(View.VISIBLE);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharePopup.showAsDropDown(shareBtn, -28, 24);
			}
		});
		
	}

	private void setupContentView() {
		mContentImage = (TouchImageView)this.findViewById(R.id.imv_content);
		mContentImage.setSwipObserver(this);
	}
	
	private void setupBottomView() {
		mPageIdxLayout = (RelativeLayout)this.findViewById(R.id.rl_page_index);
		mBottomLayout = (RelativeLayout)this.findViewById(R.id.rl_bottom_chapter);
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
