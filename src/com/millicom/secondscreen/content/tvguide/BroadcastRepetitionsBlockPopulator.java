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

public class BroadcastRepetitionsBlockPopulator {
	
	private static final String		TAG				= "BroadcastRepetitionsBlockPopulator";

	private Activity				mActivity;
	private ScrollView				mContainerView;
	private int						mNotificationId	= -1;
	private NotificationDataSource	mNotificationDataSource;
	private ImageView				mReminderOneIv, mReminderTwoIv, mReminderThreeIv;
	private String					mTvDate;

	private boolean					mIsFutureOne	= false, mIsFutureTwo = false, mIsFutureThree = false, mIsSetOne = false, mIsSetTwo = false, mIsSetThree = false, mIsSeries;

	public BroadcastRepetitionsBlockPopulator(Activity activity, ScrollView containerView, String tvDate) {
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mTvDate = tvDate;
		mNotificationDataSource = new NotificationDataSource(mActivity);
	}

	public void createBlock(final ArrayList<Broadcast> repeatingBroadcasts) {
		
		// the same layout as for the Upcoming Episodes for series is used, as the elements are the same, except for the title
		LinearLayout containerView = (LinearLayout) mContainerView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(mActivity).inflate(R.layout.block_broadcastpage_upcoming_layout, null);

		TextView title = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_title_textview);
		title.setText(mActivity.getResources().getString(R.string.upcoming_episodes));

		if (repeatingBroadcasts.size() > 0 && repeatingBroadcasts.get(0) != null) {

			final Broadcast broadcastOne = repeatingBroadcasts.get(0);

			Log.d(TAG, "REPEATING BROADCASTS SIZE: " + repeatingBroadcasts.size());
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

			mSeasonEpisodeOneTv.setVisibility(View.VISIBLE);
			
			try {
				mTitleTimeOneTv.setText(DateUtilities.isoStringToDayOfWeekAndDate(broadcastOne.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastOne.getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mChannelOneTv.setText(broadcastOne.getChannel().getName());

			try {
				mIsFutureOne = DateUtilities.isTimeInFuture(broadcastOne.getBeginTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (!mIsFutureOne) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcastOne.getChannel().getChannelId(), broadcastOne.getBeginTimeMillis());
				if (dbItem.getNotificationId() != 0) {
					mIsSetOne = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSetOne = false;
					mNotificationId = -1;
				}

				if (mIsSetOne) mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
				else mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
			} else {
				mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_dissabled_clock));
			}

			mReminderOneContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFutureOne) {
						if (mIsSetOne == false) {
							if (NotificationService.setAlarm(mActivity, broadcastOne, broadcastOne.getChannel(), mTvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcastOne.getChannel().getChannelId(), broadcastOne.getBeginTimeMillis());

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
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(broadcastOne.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastOne.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastOne.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});
		}

		if (repeatingBroadcasts.size() > 1 && repeatingBroadcasts.get(1) != null) {
			final Broadcast broadcastTwo = repeatingBroadcasts.get(1);

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

			mSeasonEpisodeTwoTv.setVisibility(View.VISIBLE);

			try {
				mTitleTimeTwoTv.setText(DateUtilities.isoStringToDayOfWeekAndDate(repeatingBroadcasts.get(1).getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastTwo.getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mChannelTwoTv.setText(broadcastTwo.getChannel().getName());

			try {
				mIsFutureTwo = DateUtilities.isTimeInFuture(broadcastTwo.getBeginTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (!mIsFutureTwo) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcastTwo.getChannel().getChannelId(), broadcastTwo.getBeginTimeMillis());
				if (dbItem.getNotificationId() != 0) {
					mIsSetTwo = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSetTwo = false;
					mNotificationId = -1;
				}

				if (mIsSetTwo) mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
				else mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
			} else {
				mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_dissabled_clock));
			}

			mReminderTwoContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFutureTwo) {
						if (mIsSetTwo == false) {
							if (NotificationService.setAlarm(mActivity, broadcastTwo, broadcastTwo.getChannel(), mTvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcastTwo.getChannel().getChannelId(), broadcastTwo.getBeginTimeMillis());

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
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(broadcastTwo.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastTwo.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastTwo.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});
		}

		// third program
		if (repeatingBroadcasts.size() > 2 && repeatingBroadcasts.get(2) != null) {

			final Broadcast broadcastThree = repeatingBroadcasts.get(2);

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

			mSeasonEpisodeThreeTv.setVisibility(View.GONE);
			
			try {
				mTitleTimeThreeTv.setText(DateUtilities.isoStringToDayOfWeekAndDate(broadcastThree.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastThree.getBeginTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mChannelThreeTv.setText(broadcastThree.getChannel().getName());

			try {
				mIsFutureThree = DateUtilities.isTimeInFuture(broadcastThree.getBeginTime());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (!mIsFutureThree) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcastThree.getChannel().getChannelId(), broadcastThree.getBeginTimeMillis());
				if (dbItem.getNotificationId() != 0) {
					mIsSetThree = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSetThree = false;
					mNotificationId = -1;
				}

				if (mIsSetThree) mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
				else mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
			} else {
				mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_dissabled_clock));
			}

			mReminderThreeContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFutureThree) {
						if (mIsSetThree == false) {
							if (NotificationService.setAlarm(mActivity, broadcastThree, broadcastThree.getChannel(), mTvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcastThree.getChannel().getChannelId(), broadcastThree.getBeginTimeMillis());

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
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(broadcastThree.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastThree.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastThree.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					mActivity.finish();
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});
		}

		if (repeatingBroadcasts.size() > 3) {

			View divider = (View) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_divider);
			divider.setVisibility(View.VISIBLE);
			TextView showMoreTxt = (TextView) topContentView.findViewById(R.id.block_broadcast_upcoming_episodes_more_textview);
			showMoreTxt.setVisibility(View.VISIBLE);
			showMoreTxt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "SIZE: " + repeatingBroadcasts.size());
					Intent intent = new Intent(mActivity, RepeatitionsPageActivity.class);
					intent.putParcelableArrayListExtra(Consts.INTENT_EXTRA_REPEATING_BROADCASTS, repeatingBroadcasts);
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});
		}

		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(20, 20, 20, 20);
		containerView.addView(topContentView, layoutParams);
		
	}
	
	public Runnable yesNotificationThreeProc() {
		return new Runnable() {
			public void run() {
				mReminderThreeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
				mIsSetThree = false;
			}
		};
	}

	public Runnable yesNotificationTwoProc() {
		return new Runnable() {
			public void run() {
				mReminderTwoIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
				mIsSetTwo = false;
			}
		};
	}

	public Runnable yesNotificationOneProc() {
		return new Runnable() {
			public void run() {
				mReminderOneIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
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
