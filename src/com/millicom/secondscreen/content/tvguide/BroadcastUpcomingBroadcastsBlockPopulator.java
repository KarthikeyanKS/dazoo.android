package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.UpcomingEpisodesListAdapter;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class BroadcastUpcomingBroadcastsBlockPopulator {

	private final static String	TAG	= "UpcomingBroadcastsBlockPopulator";
	private Activity			mActivity;
	private ScrollView			mContainerView;
	private ImageLoader			mImageLoader;

	public BroadcastUpcomingBroadcastsBlockPopulator(Activity activity, ScrollView containerView) {
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		this.mContainerView = containerView;
	}

	public void createBlock(ArrayList<Broadcast> upcomingBroadcasts) {
		LinearLayout containerView = (LinearLayout) mContainerView.findViewById(R.id.broacastpage_block_container_layout);

		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_broadcastpage_upcoming_episodes, null);

		TextView title = (TextView) contentView.findViewById(R.id.upcoming_episodes_title_textview);
		title.setText("Title generation");

		if (upcomingBroadcasts.size() > 0 && upcomingBroadcasts.get(0) != null) {
			Log.d(TAG, "UPCOMING BROADCASTS SIZE: " + upcomingBroadcasts.size());
			// first program
			RelativeLayout mFirstContainer = (RelativeLayout) contentView.findViewById(R.id.upcoming_episodes_one_info_container);
			mFirstContainer.setVisibility(View.VISIBLE);
			TextView mSeasonEpisodeOneTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_one_season_episode);
			TextView mTitleTimeOneTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_one_title_time);
			TextView mChannelOneTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_one_channel);
			ImageView mReminderOneIv = (ImageView) contentView.findViewById(R.id.upcoming_episodes_one_addreminder);

			Program program = upcomingBroadcasts.get(0).getProgram();

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
				mSeasonEpisodeOneTv.setText(mActivity.getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + "  " + mActivity.getResources().getString(R.string.episode)
						+ " " + program.getEpisodeNumber());
			} else {
				mSeasonEpisodeOneTv.setText(program.getTitle());
			}
			try {
				mTitleTimeOneTv.setText(DateUtilities.isoStringToDayOfWeekAndDate(upcomingBroadcasts.get(0).getBeginTime()) + " - "
						+ DateUtilities.isoStringToTimeString(upcomingBroadcasts.get(0).getBeginTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mChannelOneTv.setText(upcomingBroadcasts.get(0).getChannel().getName());
		}

		if (upcomingBroadcasts.size() > 1 && upcomingBroadcasts.get(1) != null) {
			// second program
			RelativeLayout mSecondContainer = (RelativeLayout) contentView.findViewById(R.id.upcoming_episodes_two_info_container);
			mSecondContainer.setVisibility(View.VISIBLE);
			TextView mSeasonEpisodeTwoTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_two_season_episode);
			TextView mTitleTimeTwoTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_two_title_time);
			TextView mChannelTwoTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_two_channel);
			ImageView mReminderTwoIv = (ImageView) contentView.findViewById(R.id.upcoming_episodes_two_addreminder);

			Program program = upcomingBroadcasts.get(1).getProgram();

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
				mSeasonEpisodeTwoTv.setText(mActivity.getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " " + mActivity.getResources().getString(R.string.episode)
						+ " " + program.getEpisodeNumber());
			} else {
				mSeasonEpisodeTwoTv.setText(program.getTitle());
			}
			try {
				mTitleTimeTwoTv.setText(DateUtilities.isoStringToDayOfWeekAndDate(upcomingBroadcasts.get(1).getBeginTime()) + " - "
						+ DateUtilities.isoStringToTimeString(upcomingBroadcasts.get(1).getBeginTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mChannelTwoTv.setText(upcomingBroadcasts.get(1).getChannel().getName());

		}
		// third program
		if (upcomingBroadcasts.size() > 2 && upcomingBroadcasts.get(2) != null) {
			RelativeLayout mThirdContainer = (RelativeLayout) contentView.findViewById(R.id.upcoming_episodes_three_info_container);
			mThirdContainer.setVisibility(View.VISIBLE);
			TextView mSeasonEpisodeThreeTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_three_season_episode);
			TextView mTitleTimeThreeTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_three_title_time);
			TextView mChannelThreeTv = (TextView) contentView.findViewById(R.id.upcoming_episodes_three_channel);
			ImageView mReminderThreeIv = (ImageView) contentView.findViewById(R.id.upcoming_episodes_three_addreminder);

			Program program = upcomingBroadcasts.get(2).getProgram();

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
				mSeasonEpisodeThreeTv.setText(mActivity.getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " " + mActivity.getResources().getString(R.string.episode)
						+ " " + program.getEpisodeNumber());
			} else {
				mSeasonEpisodeThreeTv.setText(program.getTitle());
			}
			try {
				mTitleTimeThreeTv.setText(DateUtilities.isoStringToDayOfWeekAndDate(upcomingBroadcasts.get(2).getBeginTime()) + " - "
						+ DateUtilities.isoStringToTimeString(upcomingBroadcasts.get(2).getBeginTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mChannelThreeTv.setText(upcomingBroadcasts.get(2).getChannel().getName());

		}

		if (upcomingBroadcasts.size() > 3) {

			View divider = (View) contentView.findViewById(R.id.upcoming_episodes_divider);
			divider.setVisibility(View.VISIBLE);
			TextView showMoreTxt = (TextView) contentView.findViewById(R.id.upcoming_episodes_more_textview);
			showMoreTxt.setVisibility(View.VISIBLE);
			showMoreTxt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// go to list of all upcoming episodes
				}
			});
		}

		// LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		// layoutParams.setMargins(0, 0, 0, 20);

		containerView.addView(contentView);
	}
}
