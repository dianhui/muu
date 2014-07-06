package com.muu.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
	private String[] mMenuItems;
	private SlidingMenu mSlidingMenu;
	private int mCurIdx = 1;

	public CategoryListAdapter(Context ctx, SlidingMenu slidingMenu) {
		mCtx = ctx.getApplicationContext();
		mSlidingMenu = slidingMenu;
		
		mInflater = LayoutInflater.from(ctx);
		mMenuItems = ctx.getResources().getStringArray(
		        R.array.slid_menu_items_array);
	}

	@Override
	public int getCount() {
		return mMenuItems.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private final static int sSearchIdx = 0;
	private final static int sFirstPageIdx = 1;
	private final static int sCategoryIdx = 2;
	private final static int sOffLineReadIdx = 3;
	private final static int sSettingsIdx = 4;
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

		holder.text.setText(mMenuItems[position]);
		holder.text.setCompoundDrawables(getLeftDrawable(position), null, null, null);
		if (position == mCurIdx) {
			holder.text.setSelected(true);
		} else {
			holder.text.setSelected(false);
		}
		
		convertView.setClickable(true);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mCurIdx = position;
				CategoryListAdapter.this.notifyDataSetChanged();
				
				switch (position) {
				case sSearchIdx:
					startActivity(SearchActivity.class);
					break;
				case sFirstPageIdx:
					mSlidingMenu.toggle();
					break;
				case sCategoryIdx:
					startActivity(CategoryActivity.class);
					break;
				case sOffLineReadIdx:
					startActivity(OfflineReadActivity.class);
					break;
				case sSettingsIdx:
					startActivity(SettingsActivity.class);
					break;

				default:
					break;
				}
				
			}
		});

		return convertView;
	}

	static class ViewHolder {
		TextView text;
	}
	
	private void startActivity(Class<?> activityClass) {
		Intent intent = new Intent(mCtx.getApplicationContext(), activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mCtx.startActivity(intent);
	}
	
	private Drawable getLeftDrawable(int pos) {
		Resources res = mCtx.getResources();
		Drawable drawable = null;
		switch (pos) {
		case sSearchIdx:
			drawable = res.getDrawable(R.drawable.ic_slide_search);
			break;
		case sFirstPageIdx:
			drawable = res.getDrawable(R.drawable.ic_slide_first_page);
			break;
		case sCategoryIdx:
			drawable = res.getDrawable(R.drawable.ic_slide_category);
			break;
		case sOffLineReadIdx:
			drawable = res.getDrawable(R.drawable.ic_slide_offline_read);
			break;
		case sSettingsIdx:
			drawable = res.getDrawable(R.drawable.ic_slide_settings);
			break;

		default:
			drawable = res.getDrawable(R.drawable.ic_slide_search);
			break;
		}
		
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}
}