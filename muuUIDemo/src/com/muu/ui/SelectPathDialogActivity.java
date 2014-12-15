package com.muu.ui;

import java.util.ArrayList;

import com.muu.cartoons.R;
import com.muu.util.PropertyMgr;
import com.muu.util.StorageUtil;
import com.muu.util.StorageUtil.StorageInfo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SelectPathDialogActivity extends StatisticsBaseActivity {
	private ListView mStorageList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.select_path_dialog_layout);
		initViews();
	}
	
	private void initViews() {
		mStorageList = (ListView)this.findViewById(R.id.lv_paths);
		mStorageList.setAdapter(new SelectPathAdapter(this, StorageUtil.getStorageList()));
		
		setFinishOnTouchOutside(true);
	}
	
	private class SelectPathAdapter extends BaseAdapter {
		private Context mCtx;
		private LayoutInflater mInflater;
		private ArrayList<StorageInfo> mStorageList;
		
		public SelectPathAdapter(Context ctx, ArrayList<StorageInfo>storageList) {
			mCtx = ctx.getApplicationContext();
			mInflater = LayoutInflater.from(ctx);
			mStorageList = storageList;
		}
		
		@Override
		public int getCount() {
			return mStorageList.size();
		}

		@Override
		public Object getItem(int position) {
			return mStorageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.select_path_item, null);
				holder.name = (TextView)convertView.findViewById(R.id.tv_storage_name);
				holder.path = (TextView)convertView.findViewById(R.id.tv_storage_path);
				holder.checkBox = (CheckBox)convertView.findViewById(R.id.chek_box);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			if (mStorageList == null) {
				return null;
			}
			
			final StorageInfo info = mStorageList.get(position);
			if (info == null) {
				return null;
			}
			
			holder.name.setText(mCtx.getString(R.string.storage_name, position+1));
			holder.path.setText(mCtx.getString(R.string.storage_path, info.path));
			holder.checkBox.setChecked(isPathSelected(info.path));
			
			final ViewHolder finalHolder = holder;
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finalHolder.checkBox.setSelected(true);
					PropertyMgr.getInstance().setStoragePath(info.path);
					SelectPathDialogActivity.this.finish();
				}
			});
			
			return convertView;
		}
		
		private class ViewHolder {
			TextView name;
			TextView path;
			CheckBox checkBox;
		}
		
		private boolean isPathSelected(String path) {
			String selectedPath = PropertyMgr.getInstance().getStoragePath();
			Log.d("XXX", "path: " + path);
			Log.d("XXX", "selected path: "+ selectedPath);
			return path.equals(selectedPath);
		}
	}
}
