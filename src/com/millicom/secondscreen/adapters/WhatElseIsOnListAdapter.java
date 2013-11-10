package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WhatElseIsOnListAdapter extends BaseAdapter {

	private static final String		TAG				= "WhatElseIsOnListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mFollowingEpisodes;
	private ImageLoader				mImageLoader;
	private int						duration 		= 0;



	public WhatElseIsOnListAdapter(Activity activity, ArrayList<Broadcast> followingBroadcasts) {
		this.mFollowingEpisodes = followingBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(activity, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if (mFollowingEpisodes != null) {
			return mFollowingEpisodes.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mFollowingEpisodes != null) {
			return mFollowingEpisodes.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Broadcast broadcast = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = mLayoutInflater.inflate(R.layout.layout_whatelseison_listitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mImageIv = (ImageView) rowView.findViewById(R.id.whatelseison_listitem_image);
			viewHolder.mImagePb = (ProgressBar) rowView.findViewById(R.id.whatelseison_listitem_image_pb);
			
			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.whatelseison_listitem_title);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.whatelseison_listitem_time);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.whatelseison_listitem_channel);
			viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.whatelseison_listitem_type);
			viewHolder.mTimeleftTv = (TextView) rowView.findViewById(R.id.whatelseison_listitem_timeleft);
			viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.whatelseison_listitem_progressbar);
			
			viewHolder.mDivider = (View) rowView.findViewById(R.id.whatelseison_listitem_divider);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			mImageLoader.displayImage(broadcast.getProgram().getPosterLUrl(), holder.mImageIv, holder.mImagePb, ImageLoader.IMAGE_TYPE.GALLERY);
			
			//MC - Set the title of the broadcast.
			String title = broadcast.getProgram().getTitle();
			if (title != null) {
				holder.mTitleTv.setText(title);
			} 
			else {
				holder.mTitleTv.setText("");
			}
			//TODO: Set icon in front of title
			String channel = broadcast.getChannel().getName();
			if (title != null) {
				holder.mTitleTv.setText(channel);
			} 
			else {
				holder.mTitleTv.setText("");
			}
			
			//MC - Set the begin time of the broadcast.
			try {
				holder.mTimeTv.setText(DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
			} 
			catch (ParseException e) {
				e.printStackTrace();
				holder.mTimeTv.setText("");
			}
			
			//MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
				duration = (int) (startTime - endTime) / (1000 * 60);
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			holder.mDurationPb.setMax(duration);

			//MC - Calculate the current progress of the ProgressBar and update.
			int initialProgress = 0;
			long difference = 0;
			try {
				difference = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}

			if (difference < 0) {
				holder.mDurationPb.setVisibility(View.GONE);
				initialProgress = 0;
				holder.mDurationPb.setProgress(0);
			} 
			else {
				try {
					initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime()) / (1000 * 60);
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
				holder.mTimeleftTv.setText(duration-initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + 
						" " + mActivity.getResources().getString(R.string.left));
				holder.mDurationPb.setProgress(initialProgress);
				holder.mDurationPb.setVisibility(View.VISIBLE);
			}
			//Set program type
			String type = broadcast.getProgram().getProgramType();
			if (type != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(type)) {
					ArrayList<String> tags = broadcast.getProgram().getTags();
					holder.mDescTv.setText(tags.get(2) + " " + mActivity.getResources().getString(R.string.from) + " " +
							broadcast.getProgram().getYear());
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(type)) {
					holder.mDescTv.setText(mActivity.getResources().getString(R.string.season) + " " + 
							broadcast.getProgram().getSeason().getNumber() + " " + 
							mActivity.getResources().getString(R.string.episode) + " " +
							String.valueOf(broadcast.getProgram().getEpisodeNumber()));
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(type)) {
					ArrayList<String> tags = broadcast.getProgram().getTags();
					holder.mDescTv.setText(tags.get(tags.size()-1));
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(type)) {
					ArrayList<String> tags = broadcast.getProgram().getTags();
					holder.mDescTv.setText(tags.get(tags.size()-1));
				}
			}
			else {
				holder.mDescTv.setText("");
			}
		
			if (position == (this.getCount() - 1)) {
				holder.mDivider.setVisibility(View.GONE);
			}
			
			//Placeholder data
//			holder.mTitleTv.setText("This is a title");
//			holder.mTimeTv.setText("Time");
//			holder.mChannelTv.setText("Channel");
//			holder.mDescTv.setText("Type");
//			holder.mTimeleftTv.setText("43 min left");
//			holder.mDurationPb.setMax(60);
//			holder.mDurationPb.setProgress(30);
		} 
		else {
			holder.mTitleTv.setText("");
			holder.mTimeTv.setText("");
			holder.mChannelTv.setText("");
			holder.mDescTv.setText("");
			holder.mTimeleftTv.setText("");
		}

		// animate the item - available for higher api levels only
		// TranslateAnimation animation = null;
		// if (position > mLastPosition) {
		// animation = new TranslateAnimation(
		// Animation.RELATIVE_TO_SELF,
		// 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		// Animation.RELATIVE_TO_SELF, 1.0f,
		// Animation.RELATIVE_TO_SELF, 0.0f);
		//
		// animation.setDuration(1000);
		// rowView.startAnimation(animation);
		// mLastPosition = position;
		// }

		return rowView;
	}

	static class ViewHolder {
		ImageView 	mImageIv;
		ProgressBar mImagePb;
		TextView	mTitleTv;
		TextView	mTimeTv;
		TextView	mChannelTv;
		TextView 	mDescTv;
		TextView	mTimeleftTv;
		ProgressBar mDurationPb;
		View 		mDivider;
	}
}
