package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.utilities.AnimationUtilities;

public class TrippleBroadcastBlockPopulator {

	private static String mTag;

	private Activity mActivity;
	private ScrollView mContainerView;
	private int mNotificationId = -1;
	private int mPosNotificationId[] = new int[3];
	private NotificationDataSource mNotificationDataSource;
	private ImageView mReminderImageView;
	private ImageView mReminderOneImageView;
	private ImageView mReminderTwoImageView;
	private ImageView mReminderThreeImageView;
	private String mTvDate;
	private Broadcast mRunningBroadcast;
	private ArrayList<Broadcast> mBroadcasts;
	private boolean mIsSet;
	private boolean mPosIsSet[] = new boolean[3];

	private int reminderPosition;

	/* If false, then block populator is used for upcoming episodes */
	private boolean mUsedForRepetitions;

	public TrippleBroadcastBlockPopulator(String tag, boolean usedForRepetitions, Activity activity, ScrollView containerView, String tvDate, Broadcast runningBroadcast) {
		this.mTag = tag;
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mTvDate = tvDate;
		this.mRunningBroadcast = runningBroadcast;
		this.mUsedForRepetitions = usedForRepetitions;
		mNotificationDataSource = new NotificationDataSource(mActivity);
	}

	public void populatePartOfBlock(final int position, ArrayList<Broadcast> broadcastList, final Program program, View topContentView) {
		if (broadcastList.size() > position && broadcastList.get(position) != null) {
			LinearLayout mContainer = null;
			View mDivider = null;
			switch (position) {
			case 0: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_one);
				mDivider = (View) topContentView.findViewById(R.id.block_tripple_broadcast_one_bottom_divider);
				mDivider.setVisibility(View.VISIBLE);
				mReminderOneImageView = (ImageView) mContainer.findViewById(R.id.block_tripple_broadcast_addreminder);
				break;
			}
			case 1: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_two);
				mDivider = (View) topContentView.findViewById(R.id.block_tripple_broadcast_two_bottom_divider);
				mDivider.setVisibility(View.VISIBLE);
				mReminderTwoImageView = (ImageView) mContainer.findViewById(R.id.block_tripple_broadcast_addreminder);
				break;
			}
			case 2: {
				mContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_three);
				mReminderThreeImageView = (ImageView) mContainer.findViewById(R.id.block_tripple_broadcast_addreminder);
			}
			}

			TextView mSeasonEpisodeTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_season_episode);
			TextView mTitleTimeTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_title_time);
			TextView mChannelTv = (TextView) mContainer.findViewById(R.id.block_tripple_broadcast_channel);
			mReminderImageView = (ImageView) mContainer.findViewById(R.id.block_tripple_broadcast_addreminder);
			RelativeLayout mReminderContainer = (RelativeLayout) mContainer.findViewById(R.id.block_broadcast_remind_button_container);

			final Broadcast broadcast = broadcastList.get(position);
			if(mUsedForRepetitions) {
				broadcast.setProgram(program);
			}

			mContainer.setVisibility(View.VISIBLE);
			mReminderContainer.setVisibility(View.VISIBLE);

			mTitleTimeTv.setText(broadcast.getDayOfWeekWithTimeString());
			mChannelTv.setText(broadcast.getChannel().getName());

			if (!mUsedForRepetitions) {
				Program programLocal = broadcast.getProgram();
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programLocal.getProgramType())) {
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

			if (!broadcast.hasStarted()) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
				if (dbItem.getNotificationId() != 0) {
					mIsSet = true;
					mPosIsSet[position] = true;
					mNotificationId = dbItem.getNotificationId();
					mPosNotificationId[position] = dbItem.getNotificationId();
				} else {
					mIsSet = false;
					mPosIsSet[position] = false;
					mNotificationId = -1;
					mPosNotificationId[position] = -1;
				}

				if (mIsSet) {
					mReminderImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
				} else {
					mReminderImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				}
			} else {
				mReminderImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
			}

			mReminderContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!broadcast.hasStarted()) {
						if (position == 0) {
							mReminderImageView = mReminderOneImageView;
						}
						else if (position == 1) {
							mReminderImageView = mReminderTwoImageView;
						}
						else if (position == 2) {
							mReminderImageView = mReminderThreeImageView;
						}
						mIsSet = mPosIsSet[position];
						mNotificationId = mPosNotificationId[position];
						if (mIsSet == false) {
							if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), mTvDate)) {
								BroadcastPageActivity.toast = NotificationService.showSetNotificationToast(mActivity);
								mReminderImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());

								mNotificationId = dbItem.getNotificationId();
								mPosNotificationId[position] = dbItem.getNotificationId();

								AnimationUtilities.animationSet(mReminderImageView);

								mIsSet = true;
								mPosIsSet[position] = true;
							} else {
								// Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
								Log.d(mTag, "!!! Setting notification faced an error !!!");
							}
						} else {
							if (mNotificationId != -1) {
								reminderPosition = position;
								if (BroadcastPageActivity.toast != null) {
									BroadcastPageActivity.toast.cancel();
								}
								NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
								notificationDlg
								.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationProc(), noNotificationProc());
							} else {
								// Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
								Log.d(mTag, "!!! Could not find such reminder in DB !!!");
							}
						}
					}
				}
			});

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

	public void createBlock(final ArrayList<Broadcast> repeatingBroadcasts, final Program program) {
		/* Remove running broadcast */
		boolean foundRunningBroadcast = false;
		int indexOfRunningBroadcast = 0;
		for (int i = 0; i < repeatingBroadcasts.size(); ++i) {
			Broadcast repeatingBroadcast = repeatingBroadcasts.get(i);
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
		if(mUsedForRepetitions) {
			titleString = mActivity.getResources().getString(R.string.repetitions);
		} else {
			titleString = mActivity.getResources().getString(R.string.upcoming_episodes);
		}
		title.setText(titleString);

		populatePartOfBlock(0, repeatingBroadcasts, program, topContentView);
		populatePartOfBlock(1, repeatingBroadcasts, program, topContentView);
		populatePartOfBlock(2, repeatingBroadcasts, program, topContentView);

		if (repeatingBroadcasts.size() > 3) {

			View divider = (View) topContentView.findViewById(R.id.block_tripple_broadcast_three_bottom_divider);
			divider.setVisibility(View.VISIBLE);
			TextView showMoreTxt = (TextView) topContentView.findViewById(R.id.block_tripple_broadcast_more_textview);
			showMoreTxt.setText(mActivity.getResources().getString(R.string.repetitions_more));
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
						Intent intent = new Intent(mActivity, UpcomingeEpisodesPageActivity.class);
						intent.putParcelableArrayListExtra(Consts.INTENT_EXTRA_UPCOMING_BROADCASTS, repeatingBroadcasts);
						intent.putExtra(Consts.INTENT_EXTRA_RUNNING_BROADCAST, mRunningBroadcast);
						mActivity.startActivity(intent);
					}
				}
			});
		}

		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10, 10, 10, 10);
		if (mBroadcasts.size() > 0) {
			containerView.addView(topContentView, layoutParams);
		}

	}

	public Runnable yesNotificationProc() {
		return new Runnable() {
			public void run() {
				mReminderImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSet = false;
				mPosIsSet[reminderPosition] = false;
				mPosNotificationId[reminderPosition] = -1;
				mNotificationId = -1;
			}
		};
	}

	public Runnable noNotificationProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

}
