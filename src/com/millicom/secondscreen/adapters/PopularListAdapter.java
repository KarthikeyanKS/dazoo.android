package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopularListAdapter extends BaseAdapter {

	public static final String		TAG	= "PopularListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mPopularBroadcasts;
	private ImageLoader				mImageLoader;
	private String					mToken;

	public PopularListAdapter(Activity activity, String token, ArrayList<Broadcast> popularBroadcasts) {
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
		this.mImageLoader = new ImageLoader(activity, R.drawable.loadimage);
		this.mToken = token;
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

		final Broadcast broadcast = getItem(position);

		View rowView = convertView;
		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.row_popular_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_popular_header_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.row_popular_header_tv);
			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.row_popular_container);
			viewHolder.mPosterIv = (ImageView) rowView.findViewById(R.id.row_popular_listitem_iv);
			viewHolder.mImageProgressBar = (ProgressBar) rowView.findViewById(R.id.row_popular_listitem_iv_progressbar);
			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.row_popular_details_title_tv);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.row_popular_details_time_tv);
			viewHolder.mChannelNameTv = (TextView) rowView.findViewById(R.id.row_popular_details_channel_tv);
			viewHolder.mDetailsTv = (TextView) rowView.findViewById(R.id.row_popular_details_extra_tv);
			viewHolder.mProgressBarTitleTv = (TextView) rowView.findViewById(R.id.row_popular_timeleft_tv);
			viewHolder.mProgressBar = (ProgressBar) rowView.findViewById(R.id.row_popular_progressbar);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		if (broadcast != null) {

			String tvDate = "";
			try {
				tvDate = DateUtilities.isoDateStringToTvDateString(broadcast.getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// TODO: SORTING BY DAY: SHOW/HIDE HEADER
			holder.mHeaderContainer.setVisibility(View.VISIBLE);
			holder.mHeaderTv.setText(tvDate);
			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String channelDate = "";
					try {
						channelDate = DateUtilities.isoDateStringToTvDateString(broadcast.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// go to the corresponding Broadcast page
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, channelDate);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

				}
			});

			// different details about the broadcast program depending on the type
			String programType = broadcast.getProgram().getProgramType();

			mImageLoader.displayImage(broadcast.getProgram().getPosterMUrl(), holder.mPosterIv, ImageLoader.IMAGE_TYPE.POSTER);
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				holder.mTitleTv.setText(broadcast.getProgram().getSeries().getName());
			} else {
				holder.mTitleTv.setText(broadcast.getProgram().getTitle());
			}
			try {
				holder.mTimeTv.setText(DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.mChannelNameTv.setText(broadcast.getChannel().getName());

			
			int duration = 0;
			//MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
				duration = (int) (startTime - endTime) / (1000 * 60);
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			holder.mProgressBar.setMax(duration);

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
				holder.mProgressBar.setVisibility(View.GONE);
				holder.mProgressBarTitleTv.setVisibility(View.GONE);
				initialProgress = 0;
				holder.mProgressBar.setProgress(0);
			} 
			else {
				try {
					initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime()) / (1000 * 60);
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
				holder.mProgressBarTitleTv.setText(duration-initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + 
						" " + mActivity.getResources().getString(R.string.left));
				holder.mProgressBar.setProgress(initialProgress);
				holder.mProgressBar.setVisibility(View.VISIBLE);
				holder.mProgressBarTitleTv.setVisibility(View.VISIBLE);
			}
			
			if (programType != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcast.getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					holder.mDetailsTv.setText(mActivity.getResources().getString(R.string.season) + " " + broadcast.getProgram().getSeason().getNumber() + " "
							+ mActivity.getResources().getString(R.string.episode) + " " + broadcast.getProgram().getEpisodeNumber());
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getSportType() + " " + broadcast.getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getCategory());
				}
			}
		}
		return rowView;
	}

	public static class ViewHolder {
		RelativeLayout	mHeaderContainer;
		TextView		mHeaderTv;
		LinearLayout	mContainer;
		ImageView		mPosterIv;
		ProgressBar		mImageProgressBar;
		TextView		mTitleTv;
		TextView		mTimeTv;
		TextView		mChannelNameTv;
		TextView		mDetailsTv;
		TextView		mProgressBarTitleTv;
		ProgressBar		mProgressBar;
	}
}
