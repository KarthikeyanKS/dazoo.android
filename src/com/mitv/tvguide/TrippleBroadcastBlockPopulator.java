package com.mitv.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.activities.RepetitionsPageActivity;
import com.millicom.mitv.activities.UpcomingEpisodesPageActivity;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldProgram;

public class TrippleBroadcastBlockPopulator {

	private static String TAG;

	private Activity mActivity;
	private ScrollView mContainerView;
	private OldBroadcast mRunningBroadcast;
	private ArrayList<OldBroadcast> mBroadcasts;
	private ReminderView reminderViewOne, reminderViewTwo, reminderViewThree;
	private View dividerView;


	/* If false, then block populator is used for upcoming episodes */
	private boolean mUsedForRepetitions;

	public TrippleBroadcastBlockPopulator(String tag, boolean usedForRepetitions, Activity activity, ScrollView containerView, OldBroadcast runningBroadcast) {
		this.TAG = tag;
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mRunningBroadcast = runningBroadcast;
		this.mUsedForRepetitions = usedForRepetitions;
	}

	public void populatePartOfBlock(final int position, ArrayList<OldBroadcast> broadcastList, final OldProgram program, View topContentView) {
		if (broadcastList.size() > position && broadcastList.get(position) != null) {
			final OldBroadcast broadcast = broadcastList.get(position);
			LinearLayout mContainer = null;

			switch (position) {
			case 0: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_one);

				// RelativeLayout reminderContainer = (RelativeLayout)
				// topContentView.findViewById(R.id.block_tripple_broadcast_reminder_view);
				// reminderViewOne = (ReminderImageView)
				// reminderContainer.findViewById(R.id.element_reminder_image_View);
				reminderViewOne = (ReminderView) mContainer.findViewById(R.id.block_tripple_broadcast_reminder_view);
				reminderViewOne.setBroadcast(broadcast);
				
				break;
			}
			case 1: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_two);

				// RelativeLayout reminderContainer = (RelativeLayout)
				// topContentView.findViewById(R.id.block_tripple_broadcast_reminder_view);
				// reminderViewTwo = (ReminderImageView)
				// reminderContainer.findViewById(R.id.element_reminder_image_View);
				reminderViewTwo = (ReminderView) mContainer.findViewById(R.id.block_tripple_broadcast_reminder_view);
				reminderViewTwo.setBroadcast(broadcast);
			
				dividerView = topContentView.findViewById(R.id.block_tripple_broadcast_one_bottom_divider);
				dividerView.setVisibility(View.VISIBLE);
				
				break;
			}
			case 2: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_three);

				// RelativeLayout reminderContainer = (RelativeLayout)
				// topContentView.findViewById(R.id.block_tripple_broadcast_reminder_view);
				// reminderViewThree = (ReminderImageView)
				// reminderContainer.findViewById(R.id.element_reminder_image_View);
				reminderViewThree = (ReminderView) mContainer.findViewById(R.id.block_tripple_broadcast_reminder_view);
				reminderViewThree.setBroadcast(broadcast);
				
				dividerView = topContentView.findViewById(R.id.block_tripple_broadcast_two_bottom_divider);
				dividerView.setVisibility(View.VISIBLE);
			}
			}

			
			TextView mSeasonEpisodeTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_season_episode);
			TextView mTitleTimeTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_title_time);
			TextView mChannelTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_channel);
			
			if(mUsedForRepetitions) {
				broadcast.setProgram(program);
			}

			mContainer.setVisibility(View.VISIBLE);

			mTitleTimeTv.setText(broadcast.getDayOfWeekWithTimeString());
			mChannelTv.setText(broadcast.getChannel().getName());

			if (!mUsedForRepetitions) {
				OldProgram programLocal = broadcast.getProgram();
				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programLocal.getProgramType())) {
					String season = programLocal.getSeason().getNumber();
					int episode = programLocal.getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode > 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
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
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
				}
			});
		}
	}

	public void createBlock(final ArrayList<OldBroadcast> repeatingBroadcasts, final OldProgram program) {
		/* Remove running broadcast */
		boolean foundRunningBroadcast = false;
		int indexOfRunningBroadcast = 0;
		for (int i = 0; i < repeatingBroadcasts.size(); ++i) {
			OldBroadcast repeatingBroadcast = repeatingBroadcasts.get(i);
			if (repeatingBroadcast.equals(mRunningBroadcast)) {
				foundRunningBroadcast = true;
				indexOfRunningBroadcast = i;
				break;
			}
		}

		if (foundRunningBroadcast) {
			repeatingBroadcasts.remove(indexOfRunningBroadcast);
		}

		mBroadcasts = repeatingBroadcasts;

		// the same layout as for the Upcoming Episodes for series is used, as the elements are the same, except for the title
		LinearLayout containerView = (LinearLayout) mContainerView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(mActivity).inflate(R.layout.block_broadcastpage_upcoming_or_repetition_layout, null);

		TextView title = (TextView) topContentView.findViewById(R.id.block_tripple_broadcast_title_textview);

		String titleString = null;
		String showMoreString = null;
		if(mUsedForRepetitions) {
			titleString = mActivity.getResources().getString(R.string.repetitions);
			showMoreString = mActivity.getResources().getString(R.string.repetitions_more);
		} else {
			titleString = mActivity.getResources().getString(R.string.upcoming_episodes);
			showMoreString = mActivity.getResources().getString(R.string.upcoming_episodes_more);
		}
		title.setText(titleString);

		populatePartOfBlock(0, repeatingBroadcasts, program, topContentView);
		populatePartOfBlock(1, repeatingBroadcasts, program, topContentView);
		populatePartOfBlock(2, repeatingBroadcasts, program, topContentView);

		if (repeatingBroadcasts.size() > 3) {

			View divider = (View) topContentView.findViewById(R.id.block_tripple_broadcast_three_bottom_divider);
			divider.setVisibility(View.VISIBLE);
			TextView showMoreTxt = (TextView) topContentView.findViewById(R.id.block_tripple_broadcast_more_textview);
			showMoreTxt.setText(showMoreString);
			showMoreTxt.setVisibility(View.VISIBLE);
			showMoreTxt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mUsedForRepetitions) {
						Intent intent = new Intent(mActivity, RepetitionsPageActivity.class);
						intent.putParcelableArrayListExtra(Consts.INTENT_EXTRA_REPEATING_BROADCASTS, repeatingBroadcasts);
						intent.putExtra(Consts.INTENT_EXTRA_REPEATING_PROGRAM, program);
						intent.putExtra(Consts.INTENT_EXTRA_RUNNING_BROADCAST, mRunningBroadcast);
						mActivity.startActivity(intent);
					}
					else {
						Intent intent = new Intent(mActivity, UpcomingEpisodesPageActivity.class);
						intent.putParcelableArrayListExtra(Consts.INTENT_EXTRA_UPCOMING_BROADCASTS, repeatingBroadcasts);
						intent.putExtra(Consts.INTENT_EXTRA_RUNNING_BROADCAST, mRunningBroadcast);
						mActivity.startActivity(intent);
					}
				}
			});
		}

		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(20, 10, 20, 10);
		if (mBroadcasts.size() > 0) {
			containerView.addView(topContentView, layoutParams);
		}

	}
}
