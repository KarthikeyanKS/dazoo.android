package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class WhatElseIsOnListAdapter extends BaseAdapter {

	private static final String		TAG				= "WhatElseIsOnListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mFollowingEpisodes;
	private ImageLoader				mImageLoader;

	public WhatElseIsOnListAdapter(Activity activity, ArrayList<Broadcast> followingBroadcasts) {
		this.mFollowingEpisodes = followingBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(activity, R.color.white);
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

			rowView = mLayoutInflater.inflate(R.layout.row_whatelseison_list, null);
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
			mImageLoader.displayImage(broadcast.getProgram().getPortLUrl(), holder.mImageIv, holder.mImagePb, ImageLoader.IMAGE_TYPE.GALLERY);
			
			//MC - Set the title of the broadcast.
			String title = broadcast.getProgram().getTitle();
			if (title != null) {
				holder.mTitleTv.setText(title);
			} 
			else {
				holder.mTitleTv.setText("");
			}
			//TODO: Set icon in front of title
			String channel = "";
			try {
			channel = broadcast.getChannel().getName();
			} catch (NullPointerException e) {
				//Toast.makeText(mActivity, "Something wrong with input data", Toast.LENGTH_LONG).show();
				Log.d(TAG,"!!! Something is wrong with the input data !!!");
			}
			if (channel != null) {
				holder.mChannelTv.setText(channel);
			} 
			else {
				holder.mChannelTv.setText("");
			}
			
			//MC - Set the begin time of the broadcast.
		
			holder.mTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
			holder.mDurationPb.setMax(broadcast.getDurationInMinutes());

			//MC - Calculate the current progress of the ProgressBar and update.
			int initialProgress = 0;

			if (broadcast.getTimeSinceBegin() < 0) {
				holder.mDurationPb.setVisibility(View.GONE);
				initialProgress = 0;
				holder.mDurationPb.setProgress(0);
			} 
			else {
		
				initialProgress = (int) broadcast.getTimeSinceBegin() / (1000 * 60);
		
				holder.mTimeleftTv.setText(broadcast.getDurationInMinutes() - initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + 
						" " + mActivity.getResources().getString(R.string.left));
				holder.mDurationPb.setProgress(initialProgress);
				holder.mDurationPb.setVisibility(View.VISIBLE);
			}
			//Set program type
			String type = broadcast.getProgram().getProgramType();
			if (type != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(type)) {
					holder.mDescTv.setText(broadcast.getProgram().getGenre() + " " + mActivity.getResources().getString(R.string.from) + " " +
							broadcast.getProgram().getYear());
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(type)) {
					holder.mDescTv.setText(mActivity.getResources().getString(R.string.season) + " " + 
							broadcast.getProgram().getSeason().getNumber() + " " + 
							mActivity.getResources().getString(R.string.episode) + " " +
							String.valueOf(broadcast.getProgram().getEpisodeNumber()));
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(type)) {
					holder.mDescTv.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(type)) {
					holder.mDescTv.setText(broadcast.getProgram().getCategory());
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
