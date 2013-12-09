package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

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
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class BroadcastUpcomingBlockPopulator {

	private static final String		TAG				= "BroadcastUpcomingBlockPopulator";

	private Activity				mActivity;
	private ImageLoader				mImageLoader;
	private ScrollView				mContainerView;
	private int						mNotificationId	= -1;
	private NotificationDataSource	mNotificationDataSource;
	private ImageView				mReminderOneIv, mReminderTwoIv, mReminderThreeIv;
	private String					mTvDate;

	private boolean					mIsFutureOne	= false, mIsFutureTwo = false, mIsFutureThree = false, mIsSetOne = false, mIsSetTwo = false, mIsSetThree = false, mIsSeries;

	public BroadcastUpcomingBlockPopulator(Activity activity, ScrollView containerView, String tvDate, boolean isSeries) {
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mTvDate = tvDate;
		this.mIsSeries = isSeries;
		mNotificationDataSource = new NotificationDataSource(mActivity);
	}

	public void createBlock(final ArrayList<Broadcast> upcomingBroadcasts) {
		LinearLayout containerView = (LinearLayout) mContainerView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(mActivity).inflate(R.layout.block_broadcastpage_upcoming_layout, null);

		TextView title = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_title_textview);
		title.setText(mActivity.getResources().getString(R.string.upcoming_episodes));

		if (upcomingBroadcasts.size() > 0 && upcomingBroadcasts.get(0) != null) {
			
			final Broadcast broadcastOne = upcomingBroadcasts.get(0);

			Log.d(TAG, "UPCOMING BROADCASTS SIZE: " + upcomingBroadcasts.size());
			// first program
			LinearLayout mFirstContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_one_container);
			mFirstContainer.setVisibility(View.VISIBLE);
			TextView mSeasonEpisodeOneTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_one_season_episode);
			TextView mTitleTimeOneTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_one_title_time);
			TextView mChannelOneTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_one_channel);
			mReminderOneIv = (ImageView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_one_addreminder);
			LinearLayout mReminderOneContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_remind_button_one_container);
			mReminderOneContainer.setVisibility(View.VISIBLE);
			LinearLayout mDividerOneContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_divider_one_container);
			mDividerOneContainer.setVisibility(View.VISIBLE);

			if (mIsSeries) {
				Program program = broadcastOne.getProgram();
				
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
					String season = program.getSeason().getNumber();
					int episode = program.getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
					}
					mSeasonEpisodeOneTv.setText(seasonEpisode);
				} else {
					mSeasonEpisodeOneTv.setText(program.getTitle());
				}
				mSeasonEpisodeOneTv.setVisibility(View.VISIBLE);
			}


			mTitleTimeOneTv.setText(broadcastOne.getDayOfWeekWithTimeString());
			mChannelOneTv.setText(broadcastOne.getChannel().getName());

			try {
				mIsFutureOne = DateUtilities.isTimeInFuture(broadcastOne.getBeginTimeMillisLocal());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (!mIsFutureOne) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcastOne.getChannel().getChannelId(), broadcastOne.getBeginTimeMillisLocal());
				if (dbItem.getNotificationId() != 0) {
					mIsSetOne = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSetOne = false;
					mNotificationId = -1;
				}

				if (mIsSetOne) mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
				else mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
			} else {
				mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
			}

			mReminderOneContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFutureOne) {
						if (mIsSetOne == false) {
							if (NotificationService.setAlarm(mActivity, broadcastOne, broadcastOne.getChannel(), mTvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcastOne.getChannel().getChannelId(), broadcastOne.getBeginTimeMillisLocal());

								mNotificationId = dbItem.getNotificationId();

								AnimationUtilities.animationSet(mReminderOneIv);

								mIsSetOne = true;
							} else {
								Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
							}
						} else {
							if (mNotificationId != -1) {
								NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
								notificationDlg.showRemoveNotificationDialog(mActivity, broadcastOne, mNotificationId, yesNotificationOneProc(), noNotificationProc());
							} else {
								Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});

			mFirstContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastOne.getBeginTimeMillisLocal());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastOne.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcastOne.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});
		}

		if (upcomingBroadcasts.size() > 1 && upcomingBroadcasts.get(1) != null) {
			View divider = (View) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_one_bottom_divider);
			divider.setVisibility(View.VISIBLE);
			
			final Broadcast broadcastTwo = upcomingBroadcasts.get(1);

			// second program
			LinearLayout mSecondContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_two_container);
			mSecondContainer.setVisibility(View.VISIBLE);
			TextView mSeasonEpisodeTwoTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_two_season_episode);
			TextView mTitleTimeTwoTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_two_title_time);
			TextView mChannelTwoTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_two_channel);
			mReminderTwoIv = (ImageView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_two_addreminder);

			LinearLayout mReminderTwoContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_remind_button_two_container);
			mReminderTwoContainer.setVisibility(View.VISIBLE);
			LinearLayout mDividerTwoContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_divider_two_container);
			mDividerTwoContainer.setVisibility(View.VISIBLE);

			if (mIsSeries) {
				Program program = broadcastTwo.getProgram();
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
					String season = program.getSeason().getNumber();
					int episode = program.getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
					}
					mSeasonEpisodeTwoTv.setText(seasonEpisode);
				} else {
					mSeasonEpisodeTwoTv.setText(program.getTitle());
				}
				mSeasonEpisodeTwoTv.setVisibility(View.VISIBLE);
			}

			mTitleTimeTwoTv.setText(broadcastTwo.getDayOfWeekWithTimeString());
			mChannelTwoTv.setText(broadcastTwo.getChannel().getName());

			try {
				mIsFutureTwo = DateUtilities.isTimeInFuture(broadcastTwo.getBeginTimeMillisLocal());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (!mIsFutureTwo) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcastTwo.getChannel().getChannelId(), broadcastTwo.getBeginTimeMillisLocal());
				if (dbItem.getNotificationId() != 0) {
					mIsSetTwo = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSetTwo = false;
					mNotificationId = -1;
				}

				if (mIsSetTwo) mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
				else mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
			} else {
				mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			}

			mReminderTwoContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFutureTwo) {
						if (mIsSetTwo == false) {
							if (NotificationService.setAlarm(mActivity, broadcastTwo, broadcastTwo.getChannel(), mTvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcastTwo.getChannel().getChannelId(), broadcastTwo.getBeginTimeMillisLocal());

								mNotificationId = dbItem.getNotificationId();

								AnimationUtilities.animationSet(mReminderTwoIv);

								mIsSetTwo = true;
							} else {
								Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
							}
						} else {
							if (mNotificationId != -1) {
								NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
								notificationDlg.showRemoveNotificationDialog(mActivity, broadcastTwo, mNotificationId, yesNotificationTwoProc(), noNotificationProc());
							} else {
								Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});

			mSecondContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastTwo.getBeginTimeMillisLocal());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastTwo.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcastTwo.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});
		}

		// third program
		if (upcomingBroadcasts.size() > 2 && upcomingBroadcasts.get(2) != null) {
			View divider = (View) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_two_bottom_divider);
			divider.setVisibility(View.VISIBLE);

			final Broadcast broadcastThree = upcomingBroadcasts.get(2);

			LinearLayout mThirdContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_upcoming_three_container);
			mThirdContainer.setVisibility(View.VISIBLE);
			TextView mSeasonEpisodeThreeTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_three_season_episode);
			TextView mTitleTimeThreeTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_three_title_time);
			TextView mChannelThreeTv = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_three_channel);
			mReminderThreeIv = (ImageView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_three_addreminder);

			LinearLayout mReminderThreeContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_remind_button_three_container);
			mReminderThreeContainer.setVisibility(View.VISIBLE);
			LinearLayout mDividerThreeContainer = (LinearLayout) topContentView.findViewById(R.id.block_broadcast_divider_three_container);
			mDividerThreeContainer.setVisibility(View.VISIBLE);

			if (mIsSeries) {
				Program program = broadcastThree.getProgram();

				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
					String season = program.getSeason().getNumber();
					int episode = program.getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
					}
					mSeasonEpisodeThreeTv.setText(seasonEpisode);
				} else {
					mSeasonEpisodeThreeTv.setText(program.getTitle());
				}
				mSeasonEpisodeThreeTv.setVisibility(View.VISIBLE);
			}

			mTitleTimeThreeTv.setText(broadcastThree.getDayOfWeekWithTimeString());
			mChannelThreeTv.setText(broadcastThree.getChannel().getName());

			try {
				mIsFutureThree = DateUtilities.isTimeInFuture(broadcastThree.getBeginTimeMillisLocal());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (!mIsFutureThree) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcastThree.getChannel().getChannelId(), broadcastThree.getBeginTimeMillisLocal());
				if (dbItem.getNotificationId() != 0) {
					mIsSetThree = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSetThree = false;
					mNotificationId = -1;
				}

				if (mIsSetThree) mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
				else mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
			} else {
				mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
			}

			mReminderThreeContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFutureThree) {
						if (mIsSetThree == false) {
							if (NotificationService.setAlarm(mActivity, broadcastThree, broadcastThree.getChannel(), mTvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcastThree.getChannel().getChannelId(), broadcastThree.getBeginTimeMillisLocal());

								mNotificationId = dbItem.getNotificationId();

								AnimationUtilities.animationSet(mReminderThreeIv);

								mIsSetThree = true;
							} else {
								Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
							}
						} else {
							if (mNotificationId != -1) {
								NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
								notificationDlg.showRemoveNotificationDialog(mActivity, broadcastThree, mNotificationId, yesNotificationThreeProc(), noNotificationProc());
							} else {
								Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});

			mThirdContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastThree.getBeginTimeMillisLocal());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastThree.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcastThree.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});
		}
		if (upcomingBroadcasts.size() > 3) {

			View divider = (View) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_three_bottom_divider);
			divider.setVisibility(View.VISIBLE);
			TextView showMoreTxt = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_more_textview);
			showMoreTxt.setVisibility(View.VISIBLE);
			showMoreTxt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "SIZE: " + upcomingBroadcasts.size());
					Intent intent = new Intent(mActivity, UpcomingeEpisodesPageActivity.class);
					intent.putParcelableArrayListExtra(Consts.INTENT_EXTRA_UPCOMING_BROADCASTS, upcomingBroadcasts);
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});
		}

		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10, 10, 10, 10);
		containerView.addView(topContentView, layoutParams);
	}

	public Runnable yesNotificationThreeProc() {
		return new Runnable() {
			public void run() {
				mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSetThree = false;
			}
		};
	}

	public Runnable yesNotificationTwoProc() {
		return new Runnable() {
			public void run() {
				mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSetTwo = false;
			}
		};
	}

	public Runnable yesNotificationOneProc() {
		return new Runnable() {
			public void run() {
				mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSetOne = false;
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