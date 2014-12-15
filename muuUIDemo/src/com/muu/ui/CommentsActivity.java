package com.muu.ui;

import java.util.ArrayList;

import com.muu.cartoons.R;
import com.muu.data.Comment;
import com.muu.server.MuuServerWrapper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommentsActivity extends StatisticsBaseActivity {
	private ProgressBar mProgressBar;
	private ListView mCommentsListView;
	private RelativeLayout mEmpLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_activity_layout);
		setupActionBar();
		
		mProgressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
		mEmpLayout = (RelativeLayout)this.findViewById(R.id.rl_empty);
		mCommentsListView = (ListView)this.findViewById(R.id.comments_list);
		int cartoonId = getIntent().getIntExtra("cartoon_id", 0);
		new RetrieveCommentsTask().execute(cartoonId);
	}

	private void setupActionBar() {
		ImageButton settingsImage = (ImageButton)this.findViewById(R.id.imbtn_slide_category);
		settingsImage.setVisibility(View.INVISIBLE);
		
		RelativeLayout topLayout = (RelativeLayout)this.findViewById(R.id.rl_top_btn);
		topLayout.setVisibility(View.INVISIBLE);
		
		RelativeLayout backLayout = (RelativeLayout)this.findViewById(R.id.rl_back);
		backLayout.setVisibility(View.VISIBLE);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentsActivity.this.finish();
			}
		});
		TextView backText = (TextView)this.findViewById(R.id.tv_back_text);
		backText.setVisibility(View.INVISIBLE);
		
		ImageButton recentButton = (ImageButton)this.findViewById(R.id.imbtn_recent_history);
		recentButton.setVisibility(View.INVISIBLE);
		
		ImageButton searchBtn = (ImageButton) this
				.findViewById(R.id.imbtn_search);
		searchBtn.setVisibility(View.INVISIBLE);
		
		TextView title = (TextView)this.findViewById(R.id.tv_action_title);
		title.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.comment));
	}
	
	
	private class CommentListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private ArrayList<Comment> mList;

		public CommentListAdapter(Context ctx, ArrayList<Comment> list) {
			mInflater = LayoutInflater.from(ctx);
			mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.comment_list_item,
						null);
				holder = new ViewHolder();
				holder.user = (TextView) convertView
						.findViewById(R.id.user_name);
				holder.time = (TextView) convertView
						.findViewById(R.id.create_time);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Comment comment = mList.get(position);
			holder.user.setText(comment.user);
			holder.time.setText(comment.createTime);
			holder.content.setText(comment.content);

			return convertView;
		}

		private class ViewHolder {
			TextView user;
			TextView time;
			TextView content;
		}

	}

	private class RetrieveCommentsTask extends
			AsyncTask<Integer, Integer, ArrayList<Comment>> {

		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<Comment> doInBackground(Integer... params) {
			return new MuuServerWrapper(getApplicationContext()).getComments(
					params[0], 0, Integer.MAX_VALUE);
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			mProgressBar.setVisibility(View.GONE);

			if (result == null || result.size() < 1) {
				mEmpLayout.setVisibility(View.VISIBLE);
				mCommentsListView.setVisibility(View.INVISIBLE);
				return;
			}

			mEmpLayout.setVisibility(View.GONE);
			mCommentsListView.setVisibility(View.VISIBLE);
			mCommentsListView.setAdapter(new CommentListAdapter(
					getApplicationContext(), result));
		}
	}

}