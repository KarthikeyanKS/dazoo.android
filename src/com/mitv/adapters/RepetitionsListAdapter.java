package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;
import com.mitv.model.OldTVDate;
import com.mitv.notification.NotificationDataSource;
import com.mitv.storage.MiTVStore;

public class RepetitionsListAdapter extends BaseAdapter {
	private static final String		TAG	= "UpcomingEpisodesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<TVBroadcast>	mRepeatingEpisodes;
	private NotificationDataSource	mNotificationDataSource;
	private int						mLastPosition	= -1;
	private int 					mNotificationId = -1;
	private int 					mPosNotificationId[];
	private boolean					mIsSet			= false;
	private boolean 				mPosIsSet[];
	private TVProgram					mProgram;
	private TVBroadcast 				mRunningBroadcast;
	private MiTVStore				mitvStore;
	private ArrayList<OldTVDate>		mTvDates;

	private int reminderPosition;

	public RepetitionsListAdapter(Activity activity, ArrayList<TVBroadcast> repeatingBroadcasts, TVProgram program, TVBroadcast runningBroadcast) {

		/* Remove running broadcast */
		boolean foundRunningBroadcast = false;
		int indexOfRunningBroadcast = 0;
		for(int i = 0; i < repeatingBroadcasts.size(); ++i) {
			TVBroadcast repeatingBroadcast = repeatingBroadcasts.get(i);
			if(repeatingBroadcast.equals(runningBroadcast)) {
				foundRunningBroadcast = true;
				indexOfRunningBroadcast = i;
				break;
			}
		}

		if(foundRunningBroadcast) {
			repeatingBroadcasts.remove(indexOfRunningBroadcast);
		}

		this.mActivity = activity;
		this.mProgram = program;
		this.mRunningBroadcast = runningBroadcast;
		this.mRepeatingEpisodes =repeatingBroadcasts;
		mNotificationDataSource = new NotificationDataSource(mActivity);

		mitvStore = MiTVStore.getInstance();
		mTvDates = mitvStore.getTvDates();

		mPosIsSet = new boolean[getCount()];
		mPosNotificationId = new int[getCount()];
	}

	@Override
	public int getCount() {
		if (mRepeatingEpisodes != null) {
			return mRepeatingEpisodes.size();
		} else return 0;
	}

	@Override
	public TVBroadcast getItem(int position) {
		if (mRepeatingEpisodes != null) {
			return mRepeatingEpisodes.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final TVBroadcast broadcast = getItem(position);
		//TODO why do this? Why should we need to set the program?
//		broadcast.setProgram(mProgram);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// the same layout is used as for the Upcoming Episodes for the
			// series, just without the Season-Episode title which is hidden
			// here
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
			if (position == 0 || broadcast.getBeginTimeStringLocalDayMonth().equals((getItem(position - 1)).getBeginTimeStringLocalDayMonth()) == false) {
				holder.mHeader.setText(broadcast.getDayOfWeekString() + " " + broadcast.getBeginTimeStringLocalDayMonth());
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}
			if (position != (getCount() - 1)
					&& broadcast.getBeginTimeStringLocalDayMonth().equals((getItem(position + 1)).getBeginTimeStringLocalDayMonth()) == false) {
				holder.mDivider.setVisibility(View.GONE);
			}

			holder.mSeasonEpisodeTv.setVisibility(View.GONE);

			holder.mTimeTv.setText(broadcast.getDayOfWeekWithTimeString());
			// Set channel
			String channel = broadcast.getChannel().getName();
			if (channel != null) {
				holder.mChannelTv.setText(channel);
			}

			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getBeginTimeDateRepresentation());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

					mActivity.startActivity(intent);

				}
			});

		} else {
			holder.mSeasonEpisodeTv.setText("");
			holder.mTimeTv.setText("");
			holder.mChannelTv.setText("");
		}

		return rowView;
	}

	static class ViewHolder {
		RelativeLayout	mHeaderContainer;
		TextView		mHeader;
		LinearLayout	mContainer;
		TextView		mSeasonEpisodeTv;
		TextView		mTimeTv;
		TextView		mChannelTv;
		ReminderView		mReminderView;

		View			mDivider;
	}

}
