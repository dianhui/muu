package com.muu.ui;

import com.muu.uidemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ReadPageActivity extends Activity implements OnGestureListener {
	private Boolean mScrolling = false;
	private GestureDetector mGestureDetector = null;
	
	private RelativeLayout mActionBarLayout = null;
	private RelativeLayout mPageIdxLayout = null;
	private ImageView mContentImage = null;
	private Boolean mInReadingMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_page_layout);
		
		mGestureDetector = new GestureDetector(this, this);
		
		mActionBarLayout = (RelativeLayout)this.findViewById(R.id.rl_action_bar);
		mPageIdxLayout = (RelativeLayout)this.findViewById(R.id.rl_page_index);
		
		mContentImage = (ImageView)this.findViewById(R.id.imv_content);
		mContentImage.setOnTouchListener(new OnTouchListener() {
			
			@Override
            public boolean onTouch(View v, MotionEvent event) {
				if (mGestureDetector.onTouchEvent(event)) {
	                return true;
	            }
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
	                if (mScrolling) {
	                    mScrolling = false;
                    }
                }
				
	            return false;
            } 
		});
		
	}

	@Override
    public boolean onDown(MotionEvent e) {
		return true;
    }

	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
	    return false;
    }

	@Override
    public void onLongPress(MotionEvent e) {
    }

	@Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
		if (mScrolling) {
	        return true;
        }
		
		if (distanceX > 20 && distanceY < 20) {
			mScrolling = true;
			Toast.makeText(this, "onScroll ...", Toast.LENGTH_SHORT).show();
        }
		
	    return true;
    }

	@Override
    public void onShowPress(MotionEvent e) {
    }

	@Override
    public boolean onSingleTapUp(MotionEvent e) {
		if (mInReadingMode) {
			mActionBarLayout.setVisibility(View.VISIBLE);
			mPageIdxLayout.setVisibility(View.GONE);
        } else {
        	mActionBarLayout.setVisibility(View.GONE);
    		mPageIdxLayout.setVisibility(View.VISIBLE);
        }
		mInReadingMode = !mInReadingMode;
		
	    return true;
    }
}
