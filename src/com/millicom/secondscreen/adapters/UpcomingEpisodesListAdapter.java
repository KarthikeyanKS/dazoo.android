package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.AnimationUtilities;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UpcomingEpisodesListAdapter extends BaseAdapter {

	private static final String		TAG	= "UpcomingEpisodesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mUpcomingEpisodes;
	private ImageLoader				mImageLoader;
	private NotificationDataSource	mNotificationDataSource;
	private int						mLastPosition	= -1, mNotificationId = -1;
	private boolean					mIsSet			= false, mIsFuture = false;

	private DazooStore				dazooStore;
	private ArrayList<TvDate>		mTvDates;

	public UpcomingEpisodesListAdapter(Activity activity, ArrayList<Broadcast> upcomingBroadcasts) {
		this.mUpcomingEpisodes = upcomingBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.color.white);
		mNotificationDataSource = new NotificationDataSource(mActivity);

		dazooStore = DazooStore.getInstance();
		mTvDates = dazooStore.getTvDates();
	}

	@Override
	public int getCount() {
		if (mUpcomingEpisodes != null) {
			return mUpcomingEpisodes.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mUpcomingEpisodes != null) {
			return mUpcomingEpisodes.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Broadcast broadcast = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = mLayoutInflater.inflate(R.layout.row_upcoming_episodes_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_upcoming_episodes_header_container);
			viewHolder.mHeader = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_header_tv);
			viewHolder.mSeasonEpisodeTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_season_episode);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_title_time);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_channel);
			viewHolder.mReminderIv = (ImageView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_remind_iv);
			viewHolder.mReminderContainer = (LinearLayout) rowView.findViewById(R.id.row_upcoming_episodes_listitem_remind_container);
			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.row_upcoming_episodes_listitem_info_container);

			viewHolder.mDivider = (View) rowView.findViewById(R.id.row_upcoming_episodes_listitem_bottom_divider);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			//Get the correct date name index
			int dateIndex = 0;
			boolean dateOutOfWeek = false;
			for (int i = 0; i < mTvDates.size(); i++) {
				//verify works
				if (broadcast.getBeginTimeStringLocal().contains(mTvDates.get(i).getDate())) {
					dateIndex = i;
					break;
				}
				if (i == (mTvDates.size() - 1)) {
					dateOutOfWeek = true;
				}
			}

			try {
				holder.mHeaderContainer.setVisibility(View.GONE);
				holder.mDivider.setVisibility(View.VISIBLE);
				if (position == 0 || DateUtilities.tvDateStringToDatePickerString(broadcast.getBeginTimeStringLocal()).equals(
						DateUtilities.tvDateStringToDatePickerString(getItem(position - 1).getBeginTimeStringLocal())) == false) {
					if (dateOutOfWeek == false) {
						holder.mHeader.setText(mTvDates.get(dateIndex).getName() + " " + 
							DateUtilities.tvDateStringToDatePickerString(mTvDates.get(dateIndex).getDate()));
						holder.mHeaderContainer.setVisibility(View.VISIBLE);
					}
				}
				if (position != (getCount() - 1) && DateUtilities.tvDateStringToDatePickerString(broadcast.getBeginTimeStringLocal()).equals(
						DateUtilities.tvDateStringToDatePickerString(getItem(position + 1).getBeginTimeStringLocal())) == false) {
					holder.mDivider.setVisibility(View.GONE);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// Set season and episode
			String season = broadcast.getProgram().getSeason().getNumber();
			String episode = String.valueOf(broadcast.getProgram().getEpisodeNumber());
			if (season != null && episode != null) {
				holder.mSeasonEpisodeTv.setText(mActivity.getResources().getString(R.string.season) + " " + season + " " + mActivity.getResources().getString(R.string.episode) + " " + episode);
			}

			holder.mTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());

			// Set channel
			String channel = broadcast.getChannel().getName();
			if (channel != null) {
				holder.mChannelTv.setText(channel);
			}

			try {
				mIsFuture = DateUtilities.isTimeInFuture(broadcast.getBeginTimeStringGmt());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (!mIsFuture) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
				if (dbItem.getNotificationId() != 0) {
					mIsSet = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSet = false;
					mNotificationId = -1;
				}

				if (mIsSet) holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
				else holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
			} else {
				holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
			}

			holder.mReminderContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFuture) {
						if (mIsSet == false) {
							if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), broadcast.getTvDateString())) {
								NotificationService.showSetNotificationToast(mActivity);
								holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());

								mNotificationId = dbItem.getNotificationId();

								AnimationUtilities.animationSet(holder.mReminderIv);

								mIsSet = true;
							} else {
								Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
							}
						} else {
							if (mNotificationId != -1) {
								NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
								notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationProc(holder.mReminderIv), noNotificationProc());
							} else {
								Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});

			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});

		} else {
			holder.mSeasonEpisodeTv.setText("");
			holder.mTimeTv.setText("");
			holder.mChannelTv.setText("");
		}

		// animate the item - available for higher api levels only
		// if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		// TranslateAnimation animation = null;
		// if (position > mLastPosition) {
		// animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		// animation.setDuration(1500);
		// rowView.startAnimation(animation);
		// mLastPosition = position;
		// }
		// }

		return rowView;
	}

	public Runnable yesNotificationProc(final ImageView view) {
		return new Runnable() {
			public void run() {
				view.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSet = false;
			}
		};
	}

	public Runnable noNotificationProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

	static class ViewHolder {
		RelativeLayout	mHeaderContainer;
		TextView		mHeader;
		LinearLayout	mContainer;
		TextView		mSeasonEpisodeTv;
		TextView		mTimeTv;
		TextView		mChannelTv;
		ImageView		mReminderIv;
		LinearLayout	mReminderContainer;

		View			mDivider;
	}
}
