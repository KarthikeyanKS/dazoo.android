package com.millicom.secondscreen.content.activity;

import java.text.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class ActivityPopularBlockPopulator {

	private static final String	TAG	= "ActivityPopularBlockPopulator";

	private Activity			mActivity;
	private LinearLayout		mContainerView;
	private ImageLoader			mImageLoader;

	public ActivityPopularBlockPopulator(Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
	}

	public void createBlock(FeedItem popularItem) {
		View headerView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular_header, null);
		TextView headerTv = (TextView) headerView.findViewById(R.id.block_popular_header_tv);
		headerTv.setText(popularItem.getTitle());
		mContainerView.addView(headerView);

		for (int i = 0; i < popularItem.getBroadcasts().size(); i++) {
			View rowView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular_listitem, null);

			LinearLayout mContainer = (LinearLayout) rowView.findViewById(R.id.block_popular_feed_container);
			ImageView mPoster = (ImageView) rowView.findViewById(R.id.block_feed_popular_listitem_iv);
			ProgressBar mImageProgressBar = (ProgressBar) rowView.findViewById(R.id.block_feed_popular_listitem_iv_progressbar);
			ImageView mIcon = (ImageView) rowView.findViewById(R.id.block_popular_feed_details_icon);
			TextView mTitle = (TextView) rowView.findViewById(R.id.block_popular_feed_details_title_tv);
			TextView mTime = (TextView) rowView.findViewById(R.id.block_popular_feed_details_time_tv);
			TextView mChannelName = (TextView) rowView.findViewById(R.id.block_popular_feed_details_channel_tv);
			TextView mDetails = (TextView) rowView.findViewById(R.id.block_popular_feed_details_extra_tv);
			TextView mProgressBarTitle = (TextView) rowView.findViewById(R.id.block_popular_feed_timeleft_tv);
			ProgressBar mProgressBar = (ProgressBar) rowView.findViewById(R.id.block_popular_feed_progressbar);

			final Broadcast broadcast = popularItem.getBroadcasts().get(i);

			if (broadcast != null) {
				// different details about the broadcast program depending on the type
				String programType = broadcast.getProgram().getProgramType();

				mImageLoader.displayImage(broadcast.getProgram().getPortLUrl(), mPoster, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					mTitle.setText(broadcast.getProgram().getSeries().getName());
				} else {
					mTitle.setText(broadcast.getProgram().getTitle());
				}
				try {
					mTime.setText(DateUtilities.isoStringToDayOfWeek(broadcast.getBeginTime()) + " - " +DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mChannelName.setText(broadcast.getChannel().getName());

				if (programType != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
						mDetails.setText(broadcast.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcast.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						mDetails.setText(mActivity.getResources().getString(R.string.season) + " " + broadcast.getProgram().getSeason().getNumber() + " "
								+ mActivity.getResources().getString(R.string.episode) + " " + broadcast.getProgram().getEpisodeNumber());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
						mDetails.setText(broadcast.getProgram().getSportType() + " " + broadcast.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
						mDetails.setText(broadcast.getProgram().getCategory());
					}
				}
			}

			mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(broadcast.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);

					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});

			mContainerView.addView(rowView);
		}

		View footerView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular_footer, null);

		TextView showAllBtn = (TextView) footerView.findViewById(R.id.block_popular_show_more_btn);
		showAllBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, PopularPageActivity.class);
				// ADD THE URL TO THE POPULAR LIST AS AN ARGUMENT?
				mActivity.startActivity(intent);
			}
		});

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(0, 0, 0, 20);
		mContainerView.addView(footerView, layoutParams);
	}
}
