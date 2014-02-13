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

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.model.Broadcast;
import com.mitv.tvguide.BroadcastPageActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class BlockPopularListViewAdapter extends BaseAdapter {

	private static final String		TAG	= "BlockPopularListViewAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mPopularBroadcasts;

	public BlockPopularListViewAdapter(Activity activity, ArrayList<Broadcast> popularBroadcasts) {
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
	public Broadcast getItem(int position) {
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

		final Broadcast broadcast = getItem(position);
		Log.d(TAG,"BROADCAST NAME: " + broadcast.getProgram().getTitle());

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

		if (broadcast != null) {
			// different details about the broadcast program depending on the type
			String programType = broadcast.getProgram().getProgramType();

			ImageAware imageAware = new ImageViewAware(holder.mPoster, false);
			ImageLoader.getInstance().displayImage(broadcast.getProgram().getPortMUrl(), imageAware);
			if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				holder.mTitle.setText(broadcast.getProgram().getSeries().getName());
			} else {
				holder.mTitle.setText(broadcast.getProgram().getTitle());
			}
			
			holder.mTime.setText(broadcast.getDayOfWeekWithTimeString());
		
			holder.mChannelName.setText(broadcast.getChannel().getName());

			if (programType != null) {
				if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mDetails.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
				} else if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					holder.mDetails.setText(mActivity.getResources().getString(R.string.season) + " " + broadcast.getProgram().getSeason().getNumber() + " "
							+ mActivity.getResources().getString(R.string.episode) + " " + broadcast.getProgram().getEpisodeNumber());
				} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
					holder.mDetails.setText(broadcast.getProgram().getSportType().getName() + " " + broadcast.getProgram().getTournament());
				} else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
					holder.mDetails.setText(broadcast.getProgram().getCategory());
				}
			}

		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());

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
