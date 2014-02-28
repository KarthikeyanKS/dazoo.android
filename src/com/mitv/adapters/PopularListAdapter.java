package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVDate;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class PopularListAdapter extends BaseAdapter {

	public static final String		TAG					= "PopularListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<TVBroadcastWithChannelInfo>	mPopularBroadcasts;
	private ImageLoader				mImageLoader;
//	private String					mToken;
	private int						mCurrentPosition	= -1;

//	private MiTVStore				mitvStore;
	private ArrayList<TVDate>		mTvDates;

	public PopularListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts) {
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
//		this.mToken = token;

//		mitvStore = MiTVStore.getInstance();
//		mTvDates = mitvStore.getTvDates();
		mTvDates = ContentManager.sharedInstance().getFromCacheTVDates();
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

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);

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
		if (broadcastWithChannelInfo != null) {

			holder.mHeaderContainer.setVisibility(View.GONE);
			if (position == 0 || broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString().equals(
					(getItem(position - 1)).getBeginTimeDayAndMonthAsString()) == false) {
				holder.mHeaderTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString() + " " + broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				holder.mHeaderContainer.setVisibility(View.VISIBLE);

			}
			/* Old implementation, not adjusting if received too much/little data from backend */
			//			try {
			//				mCurrentPosition = (Integer) holder.mTitleTv.getTag();
			//				Log.d(TAG, "currentPosition:" + mCurrentPosition);
			//				if (position % Consts.API_POPULAR_COUNT_DEFAULT == 0) {
			//					if (mTvDates != null && mTvDates.isEmpty() != true) {
			//						holder.mHeaderTv.setText(mTvDates.get(dateIndex).getName() + " " + DateUtilities.tvDateStringToDatePickerString(mTvDates.get(dateIndex).getDate()));
			//						holder.mHeaderContainer.setVisibility(View.VISIBLE);
			//					}
			//				} else {
			//					holder.mHeaderContainer.setVisibility(View.GONE);
			//				}
			//			} catch (ParseException e) {
			//				e.printStackTrace();
			//			}
			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					/* Go to the corresponding Broadcast page */
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					//TODO NewArc set return activity?? For detecting tab coloring
					mActivity.startActivity(intent);

				}
			});


			ImageAware imageAware = new ImageViewAware(holder.mPosterIv, false);
			ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);
			
			holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			holder.mChannelNameTv.setText(broadcastWithChannelInfo.getChannel().getName());

			if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) {
				ProgressBarUtils.setupProgressBar(mActivity, broadcastWithChannelInfo, holder.mProgressBar, holder.mProgressBarTitleTv);
			} else {
				holder.mProgressBar.setVisibility(View.GONE);
				holder.mProgressBarTitleTv.setVisibility(View.GONE);
			}
			
			// different details about the broadcast program depending on the type

			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
			
			if(programType == ProgramTypeEnum.TV_EPISODE) {
				holder.mTitleTv.setText(broadcastWithChannelInfo.getProgram().getSeries().getName());
			} else {
				holder.mTitleTv.setText(broadcastWithChannelInfo.getProgram().getTitle());
			}
			
			switch (programType) {
			case TV_EPISODE: {
				String season = broadcastWithChannelInfo.getProgram().getSeason().getNumber().toString();
				int episode = broadcastWithChannelInfo.getProgram().getEpisodeNumber();
				String seasonEpisode = "";
				if (!season.equals("0")) {
					seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
				}
				if (episode > 0) {
					seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
				}
				holder.mDetailsTv.setText(seasonEpisode);
				break;
			}
			case MOVIE: {
				holder.mDetailsTv.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
				break;
			}
			case SPORT: {
				holder.mDetailsTv.setText(broadcastWithChannelInfo.getProgram().getSportType().getName() + " " + broadcastWithChannelInfo.getProgram().getTournament());
				break;
			}
			case OTHER: {
				holder.mDetailsTv.setText(broadcastWithChannelInfo.getProgram().getCategory());
				break;
			}
			default: {
				break;
			}
			}
			
//			String programType = broadcast.getProgram().getProgramType();
	
//			if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
//				holder.mTitleTv.setText(broadcast.getProgram().getSeries().getName());
//			} else {
//				holder.mTitleTv.setText(broadcast.getProgram().getTitle());
//			}

//			if (programType != null) {
//				if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
//					holder.mDetailsTv.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
//				} else if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
//					String season = broadcast.getProgram().getSeason().getNumber().toString();
//					int episode = broadcast.getProgram().getEpisodeNumber();
//					String seasonEpisode = "";
//					if (!season.equals("0")) {
//						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
//					}
//					if (episode > 0) {
//						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
//					}
//					holder.mDetailsTv.setText(seasonEpisode);
//				} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
//					holder.mDetailsTv.setText(broadcast.getProgram().getSportType().getName() + " " + broadcast.getProgram().getTournament());
//				} else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
//					holder.mDetailsTv.setText(broadcast.getProgram().getCategory());
//				}
//			}
			
			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastWithChannelInfo.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastWithChannelInfo.getChannel().getChannelId().getChannelId());
					//TODO TMP DATA intercommunication
//					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, true);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

					mActivity.startActivity(intent);

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
