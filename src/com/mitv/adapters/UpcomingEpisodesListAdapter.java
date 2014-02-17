package com.mitv.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;
import com.mitv.model.Broadcast;
import com.mitv.model.NotificationDbItem;
import com.mitv.model.TvDate;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationDialogHandler;
import com.mitv.notification.NotificationService;
import com.mitv.storage.MiTVStore;
import com.mitv.tvguide.BroadcastPageActivity;
import com.mitv.utilities.AnimationUtilities;
import com.mitv.utilities.DateUtilities;

public class UpcomingEpisodesListAdapter extends BaseAdapter {

	private static final String		TAG	= "UpcomingEpisodesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mUpcomingEpisodes;
	private NotificationDataSource	mNotificationDataSource;
	private int						mLastPosition	= -1;
	private int 					mNotificationId = -1;
	private int 					mPosNotificationId[];
	private boolean					mIsSet = false;
	private boolean 				mPosIsSet[];
	private Broadcast				mRunningBroadcast;

	private MiTVStore				mitvStore;
	private ArrayList<TvDate>		mTvDates;

	private int reminderPosition;

	public UpcomingEpisodesListAdapter(Activity activity, ArrayList<Broadcast> upcomingBroadcasts, Broadcast runningBroadcast) {

		boolean foundRunningBroadcast = false;
		int indexOfRunningBroadcast = 0;
		for (int i = 0; i < upcomingBroadcasts.size(); ++i) {
			Broadcast repeatingBroadcast = upcomingBroadcasts.get(i);
			if (repeatingBroadcast.equals(mRunningBroadcast)) {
				foundRunningBroadcast = true;
				indexOfRunningBroadcast = i;
				break;
			}
		}

		if (foundRunningBroadcast) {
			upcomingBroadcasts.remove(indexOfRunningBroadcast);
		}

		this.mRunningBroadcast = runningBroadcast;
		this.mUpcomingEpisodes = upcomingBroadcasts;
		this.mActivity = activity;
		mNotificationDataSource = new NotificationDataSource(mActivity);

		mitvStore = MiTVStore.getInstance();
		mTvDates = mitvStore.getTvDates();

		mPosIsSet = new boolean[getCount()];
		mPosNotificationId = new int[getCount()];
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Broadcast broadcast = getItem(position);
		// Log.d(TAG, "broadcast: " + broadcast);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = mLayoutInflater.inflate(R.layout.row_upcoming_episodes_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_upcoming_episodes_header_container);
			viewHolder.mHeader = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_header_tv);
			viewHolder.mSeasonEpisodeTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_season_episode);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_title_time);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_channel);

			viewHolder.mReminderView = (ReminderView) rowView.findViewById(R.id.row_upcoming_episodes_reminder_view);

			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.row_upcoming_episodes_listitem_info_container);

			viewHolder.mDivider = (View) rowView.findViewById(R.id.row_upcoming_episodes_listitem_bottom_divider);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			holder.mReminderView.setBroadcast(broadcast);

			holder.mHeaderContainer.setVisibility(View.GONE);
			holder.mDivider.setVisibility(View.VISIBLE);
			if (position == 0 || broadcast.getBeginTimeStringLocalDayMonth().equals(
					(getItem(position - 1)).getBeginTimeStringLocalDayMonth()) == false) {
				holder.mHeader.setText(broadcast.getDayOfWeekString() + " " + broadcast.getBeginTimeStringLocalDayMonth());
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}
			if (position != (getCount() - 1)
					&& broadcast.getBeginTimeStringLocalDayMonth().equals(
							(getItem(position + 1)).getBeginTimeStringLocalDayMonth()) == false) {
				holder.mDivider.setVisibility(View.GONE);
			}

			// Set season and episode
			String season = broadcast.getProgram().getSeason().getNumber();
			int episode = broadcast.getProgram().getEpisodeNumber();
			String seasonEpisode = "";
			if (!season.equals("0")) {
				seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
			}
			if (episode > 0) {
				seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
			}
			holder.mSeasonEpisodeTv.setText(seasonEpisode);

			holder.mTimeTv.setText(broadcast.getDayOfWeekWithTimeString());

			// Set channel
			String channel = broadcast.getChannel().getName();
			if (channel != null) {
				holder.mChannelTv.setText(channel);
			}

			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);

					mActivity.startActivity(intent);

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

	static class ViewHolder {
		RelativeLayout		mHeaderContainer;
		TextView			mHeader;
		LinearLayout		mContainer;
		TextView			mSeasonEpisodeTv;
		TextView			mTimeTv;
		TextView			mChannelTv;
		ReminderView	mReminderView;

		View				mDivider;
	}
}
