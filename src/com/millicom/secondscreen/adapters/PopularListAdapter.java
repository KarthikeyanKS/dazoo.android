package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.nostra13.universalimageloader.core.ImageLoader;

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
			rowView = mLayoutInflater.inflate(R.layout.element_poster_broadcast, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_header_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_header_tv);
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_container);
			viewHolder.mPosterIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
			viewHolder.mChannelNameTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
			viewHolder.mDetailsTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
			viewHolder.mProgressBarTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
			viewHolder.mProgressBar = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);

			viewHolder.mTitleTv.setTag(Integer.valueOf(position));
			Log.d(TAG, "set tag: " + Integer.valueOf(position));

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		if (broadcast != null) {
			// Get the correct date name index
			int dateIndex = 0;
			for (int i = 0; i < mTvDates.size(); i++) {
				if (broadcast.getBeginTimeStringGmt().contains(mTvDates.get(i).getDate())) {
					dateIndex = i;
					break;
				}
			}
			try {
				mCurrentPosition = (Integer) holder.mTitleTv.getTag();
				Log.d(TAG, "currentPosition:" + mCurrentPosition);
				if (position % Consts.MILLICOM_SECONDSCREEN_API_POPULAR_COUNT_DEFAULT == 0) {
					if (mTvDates != null && mTvDates.isEmpty() != true) {
						holder.mHeaderTv.setText(mTvDates.get(dateIndex).getName() + " " + DateUtilities.tvDateStringToDatePickerString(mTvDates.get(dateIndex).getDate()));
						holder.mHeaderContainer.setVisibility(View.VISIBLE);
					}
				} else {
					holder.mHeaderContainer.setVisibility(View.GONE);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// go to the corresponding Broadcast page
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

				}
			});

			// different details about the broadcast program depending on the type
			String programType = broadcast.getProgram().getProgramType();

			ImageLoader.getInstance().displayImage(broadcast.getProgram().getPortMUrl(), holder.mPosterIv);
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				holder.mTitleTv.setText(broadcast.getProgram().getSeries().getName());
			} else {
				holder.mTitleTv.setText(broadcast.getProgram().getTitle());
			}

			holder.mTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());

			holder.mChannelNameTv.setText(broadcast.getChannel().getName());

			if (broadcast.isRunning()) {
				holder.mProgressBar.setMax(broadcast.getDurationInMinutes() + 1);

				// Calculate the current progress of the ProgressBar and update.
				int initialProgress = 0;

				if (broadcast.getTimeToBegin() > 0) {
					holder.mProgressBar.setVisibility(View.GONE);
					holder.mProgressBarTitleTv.setVisibility(View.GONE);
					initialProgress = 0;
					holder.mProgressBar.setProgress(0);
				} else {

					initialProgress = broadcast.minutesSinceStart();

					int timeLeft = broadcast.getDurationInMinutes() - initialProgress;

					// different representation of "X min left" for Spanish and all other languages
					if (Locale.getDefault().getLanguage().endsWith("es")) {
						if (timeLeft > 60) {
							int hours = timeLeft / 60;
							int minutes = timeLeft - hours * 60;
							holder.mProgressBarTitleTv.setText(mActivity.getResources().getQuantityString(R.plurals.left, hours) + " " + hours + " " + 
															   mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
															   mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
															   mActivity.getResources().getString(R.string.minutes));
						}
						else {
							holder.mProgressBarTitleTv.setText(mActivity.getResources().getString(R.string.left) + " " + String.valueOf(timeLeft) + " " + 
															   mActivity.getResources().getString(R.string.minutes));
						}
					} 
					else {
						if (timeLeft > 60) {
							int hours = timeLeft / 60;
							int minutes = timeLeft - hours * 60;
							holder.mProgressBarTitleTv.setText(hours + " " + mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
															   mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
															   mActivity.getResources().getString(R.string.minutes) + " " + 
															   mActivity.getResources().getString(R.string.left));
						}
						else {
							holder.mProgressBarTitleTv.setText(timeLeft + " " + mActivity.getResources().getString(R.string.minutes) + " " + 
															   mActivity.getResources().getString(R.string.left));
						}
					}

					holder.mProgressBar.setProgress(initialProgress + 1);
					holder.mProgressBar.setVisibility(View.VISIBLE);
					holder.mProgressBarTitleTv.setVisibility(View.VISIBLE);
				}
			} else {
				holder.mProgressBar.setVisibility(View.GONE);
				holder.mProgressBarTitleTv.setVisibility(View.GONE);
			}

			if (programType != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mDetailsTv.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					String season = broadcast.getProgram().getSeason().getNumber();
					int episode = broadcast.getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode > 0) {
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
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);
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
		RelativeLayout	mContainer;
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
