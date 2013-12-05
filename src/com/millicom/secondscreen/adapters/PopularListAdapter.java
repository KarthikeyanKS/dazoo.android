package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopularListAdapter extends BaseAdapter {

	public static final String		TAG					= "PopularListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mPopularBroadcasts;
	private ImageLoader				mImageLoader;
	private String					mToken;
	private int						mCurrentPosition	= -1;
	
	private DazooStore				dazooStore;
	private ArrayList<TvDate>		mTvDates;

	public PopularListAdapter(Activity activity, String token, ArrayList<Broadcast> popularBroadcasts) {
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
		this.mImageLoader = new ImageLoader(activity, R.color.white);
		this.mToken = token;

		dazooStore = DazooStore.getInstance();
		mTvDates = dazooStore.getTvDates();
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

			viewHolder.mTitleTv.setTag(Integer.valueOf(position));
			Log.d(TAG, "set tag: " + Integer.valueOf(position));

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		if (broadcast != null) {

			String tvDate = "";
			try {
				tvDate = DateUtilities.isoStringToDayOfWeek(broadcast.getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			//Get the correct date name index
			int dateIndex = 0;
			for (int i = 0; i < mTvDates.size(); i++) {
				if (broadcast.getBeginTime().contains(mTvDates.get(i).getDate())) {
					dateIndex = i;
					break;
				}
			}
			try {
				mCurrentPosition = (Integer) holder.mTitleTv.getTag();
				Log.d(TAG,"currentPosition:" + mCurrentPosition);
				if(position % Consts.MILLICOM_SECONDSCREEN_API_POPULAR_COUNT_DEFAULT == 0){
					holder.mHeaderTv.setText(mTvDates.get(dateIndex).getName() + " " + DateUtilities.tvDateStringToDatePickerString(mTvDates.get(dateIndex).getDate()));
					holder.mHeaderContainer.setVisibility(View.VISIBLE);
				}
				else {
					holder.mHeaderContainer.setVisibility(View.GONE);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
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

			mImageLoader.displayImage(broadcast.getProgram().getPortMUrl(), holder.mPosterIv, ImageLoader.IMAGE_TYPE.POSTER);
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
			long timeSinceBegin = 0;
			long timeToEnd = 0;
			// MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
				timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				timeToEnd = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
				duration = (int) (startTime - endTime) / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (timeSinceBegin > 0 && timeToEnd < 0) {
				holder.mProgressBar.setMax(duration);
	
				// Calculate the current progress of the ProgressBar and update.
				int initialProgress = 0;
				long difference = 0;
				try {
					difference = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	
				if (difference < 0) {
					holder.mProgressBar.setVisibility(View.GONE);
					holder.mProgressBarTitleTv.setVisibility(View.GONE);
					initialProgress = 0;
					holder.mProgressBar.setProgress(0);
				} else {
					try {
						initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					holder.mProgressBarTitleTv.setText(duration - initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
					holder.mProgressBar.setProgress(initialProgress);
					holder.mProgressBar.setVisibility(View.VISIBLE);
					holder.mProgressBarTitleTv.setVisibility(View.VISIBLE);
				}
			}
			else {
				holder.mProgressBar.setVisibility(View.GONE);
				holder.mProgressBarTitleTv.setVisibility(View.GONE);
			}

			if (programType != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcast.getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					String season = broadcast.getProgram().getSeason().getNumber();
					int episode = broadcast.getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
					}
					holder.mDetailsTv.setText(seasonEpisode);
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getSportType().getName() + " " + broadcast.getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getCategory());
				}
			}
			holder.mContainer.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					try {
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE,  DateUtilities.isoStringToDayOfWeek(broadcast.getBeginTime()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
					
				}
			});
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
