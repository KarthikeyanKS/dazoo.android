package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LikedBlockAdapter extends BaseAdapter {

	private static final String	TAG	= "LikedBlockAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private Program				mContent;

	private ImageLoader			mImageLoader;

	public LikedBlockAdapter(Activity activity, Program content) {
		this.mContent = content;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Program content = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.block_liked_activitypage, null);
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.mBlockContainer = (RelativeLayout) rowView.findViewById(R.id.block_liked_container);
			viewHolder.mBlockNameTv = (TextView) rowView.findViewById(R.id.block_liked_content_class_tv);
			viewHolder.mBlockPosterIv = (ImageView) rowView.findViewById(R.id.block_liked_content_iv);
			viewHolder.mBlockPosterTitleTv = (TextView) rowView.findViewById(R.id.block_liked_content_name_tv);
			viewHolder.mBlockPosterProgressBar = (ProgressBar) rowView.findViewById(R.id.block_liked_content_iv_progressbar);
			viewHolder.mContentIconIv = (ImageView) rowView.findViewById(R.id.block_liked_content_details_icon);
			viewHolder.mContentTimeTv = (TextView) rowView.findViewById(R.id.block_liked_content_details_time_tv);
			viewHolder.mContentDetailsTv = (TextView) rowView.findViewById(R.id.block_liked_content_details_episode_tv);
			viewHolder.mLikeButtonContainer = (RelativeLayout) rowView.findViewById(R.id.block_liked_like_button_container);
			viewHolder.mLikeButtonIv = (ImageView) rowView.findViewById(R.id.block_liked_like_button_iv);
			viewHolder.mLikeButtonTv = (TextView) rowView.findViewById(R.id.block_liked_like_button_tv);
			viewHolder.mShareButtonContainer = (RelativeLayout) rowView.findViewById(R.id.block_liked_share_button_container);
			viewHolder.mShareButtonIv = (ImageView) rowView.findViewById(R.id.block_liked_share_button_iv);
			viewHolder.mShareButtonTv = (TextView) rowView.findViewById(R.id.block_liked_share_button_tv);
			viewHolder.mRemindButtonContainer = (RelativeLayout) rowView.findViewById(R.id.block_liked_remind_button_container);
			viewHolder.mRemindButtonIv = (ImageView) rowView.findViewById(R.id.block_liked_remind_button_iv);
			
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		
		return rowView;
	}

	static class ViewHolder {
		RelativeLayout	mBlockContainer;
		TextView		mBlockNameTv;
		ImageView		mBlockPosterIv;
		TextView		mBlockPosterTitleTv;
		ProgressBar		mBlockPosterProgressBar;
		ImageView		mContentIconIv;
		TextView		mContentTimeTv;
		TextView		mContentDetailsTv;
		RelativeLayout	mLikeButtonContainer;
		ImageView		mLikeButtonIv;
		TextView		mLikeButtonTv;
		RelativeLayout	mShareButtonContainer;
		ImageView		mShareButtonIv;
		TextView		mShareButtonTv;
		RelativeLayout	mRemindButtonContainer;
		ImageView		mRemindButtonIv;
	}

	@Override
	public int getCount() {
		// block represent one item with content
		return 1;
	}

	@Override
	public Program getItem(int position) {
		return mContent;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

}