package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.utilities.AnimationUtilities;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

//import com.millicom.secondscreen.Consts;
//import com.millicom.secondscreen.R;
//import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
//import com.millicom.secondscreen.content.model.Broadcast;
//import com.millicom.secondscreen.content.model.Guide;
//import com.millicom.secondscreen.utilities.DateUtilities;
//import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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

	public UpcomingEpisodesListAdapter(Activity activity, ArrayList<Broadcast> upcomingBroadcasts) {
		this.mUpcomingEpisodes = upcomingBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		mNotificationDataSource = new NotificationDataSource(mActivity);
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
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			// TODO: SORTING IN THE HEADER, NOW JUST THE NAME OF THE DAY
			holder.mHeaderContainer.setVisibility(View.VISIBLE);
			try {
				holder.mHeader.setText(DateUtilities.isoStringToDayOfWeek(broadcast.getBeginTime()));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			// Set season and episode
			String season = broadcast.getProgram().getSeason().getNumber();
			String episode = String.valueOf(broadcast.getProgram().getEpisodeNumber());
			if (season != null && episode != null) {
				holder.mSeasonEpisodeTv.setText(mActivity.getResources().getString(R.string.season) + " " + season + " " + mActivity.getResources().getString(R.string.episode) + " " + episode);
			}

			try {
				holder.mTimeTv.setText(DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
				holder.mTimeTv.setText("");
			}

			// Set channel
			String channel = broadcast.getChannel().getName();
			if (channel != null) {
				holder.mChannelTv.setText(channel);
			}

			try {
				mIsFuture = DateUtilities.isTimeInFuture(broadcast.getBeginTime());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (!mIsFuture) {
				NotificationDbItem dbItem = new NotificationDbItem();
				dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillis());
				if (dbItem.getNotificationId() != 0) {
					mIsSet = true;
					mNotificationId = dbItem.getNotificationId();
				} else {
					mIsSet = false;
					mNotificationId = -1;
				}

				if (mIsSet) holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
				else holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
			} else {
				holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_dissabled_clock));
			}

			holder.mReminderContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mIsFuture) {
						if (mIsSet == false) {
							String tvDate = "";
							try {
								tvDate = DateUtilities.isoDateStringToTvDateString(broadcast.getBeginTime());
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), tvDate)) {
								NotificationService.showSetNotificationToast(mActivity);
								holder.mReminderIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

								NotificationDbItem dbItem = new NotificationDbItem();
								dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillis());

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
					} else {
						Toast.makeText(mActivity, "The broadcast was already shown! You cannot set a reminder on that", Toast.LENGTH_SHORT).show();
					}

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
				view.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
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
		TextView		mSeasonEpisodeTv;
		TextView		mTimeTv;
		TextView		mChannelTv;
		ImageView		mReminderIv;
		LinearLayout	mReminderContainer;
	}
}
