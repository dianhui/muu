package com.muu.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.muu.uidemo.R;

public class CategoryListAdapter extends BaseAdapter {

	private Context mCtx;
	private LayoutInflater mInflater;
	private String[] mCategoryTextArray;
	private SlidingMenu mSlidingMenu;

	public CategoryListAdapter(Context ctx, SlidingMenu slidingMenu) {
		mCtx = ctx.getApplicationContext();
		mSlidingMenu = slidingMenu;
		
		mInflater = LayoutInflater.from(ctx);
		mCategoryTextArray = ctx.getResources().getStringArray(
		        R.array.category_text_array);
	}

	@Override
	public int getCount() {
		return mCategoryTextArray.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.slide_category_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView
			        .findViewById(R.id.tv_category_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(mCategoryTextArray[position]);
		convertView.setClickable(true);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (position == 0) {
					mSlidingMenu.toggle();
				} else {
					Intent intent = new Intent(mCtx, BooksListActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(BooksListActivity.sListTypeKey,
							BooksListActivity.sListCategory);
					intent.putExtra(BooksListActivity.sCategoryIdx, position);
					mCtx.startActivity(intent);
				}
				
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						mSlidingMenu.toggle();
//					}
//				}, 100);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		TextView text;
	}
}
