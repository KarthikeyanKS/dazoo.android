package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class BlockPopularListViewAdapter extends BaseAdapter {

	private static final String		TAG	= "BlockPopularListViewAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<TVBroadcastWithChannelInfo>	mPopularBroadcasts;

	public BlockPopularListViewAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts) {
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
	}

	@Override
	public int getCount() {
		if (mPopularBroadcasts != null) {
			return mPopularBroadcasts.size();
		} else return 0;
	}

	@Override
	public TVBroadcastWithChannelInfo getItem(int position) {
		if (mPopularBroadcasts != null) {
			return mPopularBroadcasts.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);
		Log.d(TAG,"BROADCAST NAME: " + broadcastWithChannelInfo.getProgram().getTitle());

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.block_feed_popular_listitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.block_popular_feed_container);
			viewHolder.mPoster = (ImageView) rowView.findViewById(R.id.block_feed_popular_listitem_iv);
			viewHolder.mImageProgressBar = (ProgressBar) rowView.findViewById(R.id.block_feed_popular_listitem_iv_progressbar);
			viewHolder.mIcon = (ImageView) rowView.findViewById(R.id.block_popular_feed_details_icon);
			viewHolder.mTitle = (TextView) rowView.findViewById(R.id.block_popular_feed_details_title_tv);
			viewHolder.mTime = (TextView) rowView.findViewById(R.id.block_popular_feed_details_time_tv);
			viewHolder.mChannelName = (TextView) rowView.findViewById(R.id.block_popular_feed_details_channel_tv);
			viewHolder.mDetails = (TextView) rowView.findViewById(R.id.block_popular_feed_details_extra_tv);
			viewHolder.mProgressBarTitle = (TextView) rowView.findViewById(R.id.block_popular_feed_timeleft_tv);
			viewHolder.mProgressBar = (ProgressBar) rowView.findViewById(R.id.block_popular_feed_progressbar);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null) {
			// different details about the broadcast program depending on the type

			ImageAware imageAware = new ImageViewAware(holder.mPoster, false);
			ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);
			
			//TODO NewArc veryfy that getBeginTimeDayOfTheWeekWithHourAndMinuteAsString is what we want here
			holder.mTime.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			holder.mChannelName.setText(broadcastWithChannelInfo.getChannel().getName());
			
			
			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
			if (programType == ProgramTypeEnum.TV_EPISODE) {
				holder.mTitle.setText(broadcastWithChannelInfo.getProgram().getSeries().getName());
			} else {
				holder.mTitle.setText(broadcastWithChannelInfo.getProgram().getTitle());
			}
			
			
			switch (programType) {
			case MOVIE: {
				holder.mDetails.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
				break;
			}
			case TV_EPISODE: {
				holder.mDetails.setText(mActivity.getResources().getString(R.string.season) + " " + broadcastWithChannelInfo.getProgram().getSeason().getNumber() + " "
						+ mActivity.getResources().getString(R.string.episode) + " " + broadcastWithChannelInfo.getProgram().getEpisodeNumber());
				break;
			}
			case SPORT: {
				holder.mDetails.setText(broadcastWithChannelInfo.getProgram().getSportType().getName() + " " + broadcastWithChannelInfo.getProgram().getTournament());
				break;
			}
			case OTHER: {
				holder.mDetails.setText(broadcastWithChannelInfo.getProgram().getCategory());
				break;
			}
			}
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				mActivity.startActivity(intent);
			}
		});

		return rowView;
	}

	private static class ViewHolder {
		LinearLayout	mContainer;
		ImageView		mPoster;
		ProgressBar		mImageProgressBar;
		ImageView		mIcon;
		TextView		mTitle;
		TextView		mTime;
		TextView		mChannelName;
		TextView		mDetails;
		TextView		mProgressBarTitle;
		ProgressBar		mProgressBar;
	}
}
