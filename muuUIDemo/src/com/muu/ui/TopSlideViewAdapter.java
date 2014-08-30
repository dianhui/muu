package com.muu.ui;

import java.util.ArrayList;

import com.android.volley.toolbox.NetworkImageView;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class TopSlideViewAdapter extends PagerAdapter {
	private ArrayList<NetworkImageView> mViews = null;
	public TopSlideViewAdapter(ArrayList<NetworkImageView> views) {
		mViews = views;
	}
	
	public void updateData(ArrayList<NetworkImageView> list) {
		mViews = list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(mViews.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(mViews.get(position));
		
		return mViews.get(position);
	}
}
