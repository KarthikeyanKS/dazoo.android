package com.mitv.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.activities.RepetitionsPageActivity;
import com.millicom.mitv.activities.UpcomingEpisodesPageActivity;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;

public class TrippleBroadcastBlockPopulator {
	@SuppressWarnings("unused")
	private static String TAG = TrippleBroadcastBlockPopulator.class.getName();

	private Activity activity;
	private ScrollView containerView;
	private TVBroadcastWithChannelInfo runningBroadcast;
	private ArrayList<TVBroadcastWithChannelInfo> broadcastsWithChannelInfo;
	private ReminderView reminderViewOne;
	private ReminderView reminderViewTwo;
	private ReminderView reminderViewThree;
	private View dividerView;

	/* If false, then block populator is used for upcoming episodes */
	private boolean usedForRepetitions;

	public TrippleBroadcastBlockPopulator(String tag, boolean usedForRepetitions, Activity activity, ScrollView containerView,
			TVBroadcastWithChannelInfo runningBroadcast) {
		this.activity = activity;
		this.containerView = containerView;
		this.runningBroadcast = runningBroadcast;
		this.usedForRepetitions = usedForRepetitions;
	}

	public void populatePartOfBlock(final int position, ArrayList<TVBroadcastWithChannelInfo> broadcastList, final TVProgram program, View topContentView) {
		if (broadcastList.size() > position && broadcastList.get(position) != null) {
			final TVBroadcastWithChannelInfo broadcastWithChannelInfo = broadcastList.get(position);

			LinearLayout mContainer = null;

			switch (position) {
			case 0: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_one);

				reminderViewOne = (ReminderView) mContainer.findViewById(R.id.block_tripple_broadcast_reminder_view);
				reminderViewOne.setBroadcast(broadcastWithChannelInfo);

				break;
			}

			case 1: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_two);

				reminderViewTwo = (ReminderView) mContainer.findViewById(R.id.block_tripple_broadcast_reminder_view);
				reminderViewTwo.setBroadcast(broadcastWithChannelInfo);

				dividerView = topContentView.findViewById(R.id.block_tripple_broadcast_one_bottom_divider);
				dividerView.setVisibility(View.VISIBLE);

				break;
			}

			case 2: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_three);
				
				reminderViewThree = (ReminderView) mContainer.findViewById(R.id.block_tripple_broadcast_reminder_view);
				reminderViewThree.setBroadcast(broadcastWithChannelInfo);

				dividerView = topContentView.findViewById(R.id.block_tripple_broadcast_two_bottom_divider);
				dividerView.setVisibility(View.VISIBLE);
			}
			}

			TextView mSeasonEpisodeTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_season_episode);
			TextView mTitleTimeTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_title_time);
			TextView mChannelTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_channel);

			if (usedForRepetitions) {
				// TODO should we really set program here, why?
				// broadcast.setProgram(program);
			}

			mContainer.setVisibility(View.VISIBLE);

			mTitleTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			if (!usedForRepetitions) {
				TVProgram programLocal = broadcastWithChannelInfo.getProgram();

				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programLocal.getProgramType())) {
					String season = programLocal.getSeason().getNumber().toString();

					int episode = programLocal.getEpisodeNumber();

					String seasonEpisode = "";

					if (!season.equals("0")) {
						seasonEpisode += activity.getResources().getString(R.string.season) + " " + season + " ";
					}

					if (episode > 0) {
						seasonEpisode += activity.getResources().getString(R.string.episode) + " " + episode;
					}

					mSeasonEpisodeTv.setText(seasonEpisode);
				} else {
					mSeasonEpisodeTv.setText(programLocal.getTitle());
				}

				mSeasonEpisodeTv.setVisibility(View.VISIBLE);
			}

			mContainer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
	}

	public void createBlock(final ArrayList<TVBroadcastWithChannelInfo> repeatingOrUpcomingBroadcasts, final TVProgram program) {
		/* Remove running broadcast */

		boolean foundRunningBroadcast = false;

		int indexOfRunningBroadcast = 0;

		for (int i = 0; i < repeatingOrUpcomingBroadcasts.size(); ++i) {
			TVBroadcastWithChannelInfo repeatingBroadcast = repeatingOrUpcomingBroadcasts.get(i);

			if (repeatingBroadcast.equals(runningBroadcast)) {
				foundRunningBroadcast = true;
				indexOfRunningBroadcast = i;
				break;
			}
		}

		if (foundRunningBroadcast) {
			repeatingOrUpcomingBroadcasts.remove(indexOfRunningBroadcast);
		}

		broadcastsWithChannelInfo = repeatingOrUpcomingBroadcasts;

		// the same layout as for the Upcoming Episodes for series is used, as the elements are the same, except for the
		// title
		LinearLayout layoutContainerView = (LinearLayout) containerView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(activity).inflate(R.layout.block_broadcastpage_upcoming_or_repetition_layout, null);

		TextView title = (TextView) topContentView.findViewById(R.id.block_tripple_broadcast_title_textview);

		String titleString = null;

		String showMoreString = null;

		if (usedForRepetitions) {
			titleString = activity.getResources().getString(R.string.repetitions);

			showMoreString = activity.getResources().getString(R.string.repetitions_more);
		} else {
			titleString = activity.getResources().getString(R.string.upcoming_episodes);

			showMoreString = activity.getResources().getString(R.string.upcoming_episodes_more);
		}

		title.setText(titleString);

		populatePartOfBlock(0, repeatingOrUpcomingBroadcasts, program, topContentView);
		populatePartOfBlock(1, repeatingOrUpcomingBroadcasts, program, topContentView);
		populatePartOfBlock(2, repeatingOrUpcomingBroadcasts, program, topContentView);

		if (repeatingOrUpcomingBroadcasts.size() > 3) {
			View divider = (View) topContentView.findViewById(R.id.block_tripple_broadcast_three_bottom_divider);

			divider.setVisibility(View.VISIBLE);

			TextView showMoreTxt = (TextView) topContentView.findViewById(R.id.block_tripple_broadcast_more_textview);

			showMoreTxt.setText(showMoreString);
			showMoreTxt.setVisibility(View.VISIBLE);

			showMoreTxt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(runningBroadcast);

					if (usedForRepetitions) {
						Intent intent = new Intent(activity, RepetitionsPageActivity.class);

						ContentManager.sharedInstance().setRepeatingBroadcasts(runningBroadcast, repeatingOrUpcomingBroadcasts);

						activity.startActivity(intent);
					} else {
						Intent intent = new Intent(activity, UpcomingEpisodesPageActivity.class);

						ContentManager.sharedInstance().setUpcomingBroadcasts(runningBroadcast, repeatingOrUpcomingBroadcasts);

						activity.startActivity(intent);
					}

				}
			});
		}

		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(20, 10, 20, 10);

		if (broadcastsWithChannelInfo.size() > 0) {
			layoutContainerView.addView(topContentView, layoutParams);
		}

	}
}
