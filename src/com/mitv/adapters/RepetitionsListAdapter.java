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
import com.mitv.model.Program;
import com.mitv.model.TvDate;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationDialogHandler;
import com.mitv.notification.NotificationService;
import com.mitv.storage.MiTVStore;
import com.mitv.tvguide.BroadcastPageActivity;
import com.mitv.utilities.AnimationUtilities;
import com.mitv.utilities.DateUtilities;

public class RepetitionsListAdapter extends BaseAdapter {
	private static final String		TAG	= "UpcomingEpisodesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mRepeatingEpisodes;
	private NotificationDataSource	mNotificationDataSource;
	private int						mLastPosition	= -1;
	private int 					mNotificationId = -1;
	private int 					mPosNotificationId[];
	private boolean					mIsSet			= false;
	private boolean 				mPosIsSet[];
	private Program					mProgram;
	private Broadcast 				mRunningBroadcast;
	private MiTVStore				mitvStore;
	private ArrayList<TvDate>		mTvDates;

	private int reminderPosition;

	public RepetitionsListAdapter(Activity activity, ArrayList<Broadcast> repeatingBroadcasts, Program program, Broadcast runningBroadcast) {

		/* Remove running broadcast */
		boolean foundRunningBroadcast = false;
		int indexOfRunningBroadcast = 0;
		for(int i = 0; i < repeatingBroadcasts.size(); ++i) {
			Broadcast repeatingBroadcast = repeatingBroadcasts.get(i);
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
	public Broadcast getItem(int position) {
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

		final Broadcast broadcast = getItem(position);
		broadcast.setProgram(mProgram);

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

			RelativeLayout reminderContainer = (RelativeLayout) rowView.findViewById(R.id.row_upcoming_episodes_reminder_view);
			viewHolder.mReminderView = (ReminderView) reminderContainer.findViewById(R.id.element_reminder_image_View);
			
			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.row_upcoming_episodes_listitem_info_container);

			viewHolder.mDivider = (View) rowView.findViewById(R.id.row_upcoming_episodes_listitem_bottom_divider);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			holder.mReminderView.setBroadcast(broadcast);
			// Get the correct date name index
			int dateIndex = 0;
			boolean dateOutOfWeek = false;
			for (int i = 0; i < mTvDates.size(); i++) {
				if (broadcast.getTvDateString().equals(mTvDates.get(i).getDate())) {
					dateIndex = i;
					break;
				}
				if (i == (mTvDates.size() - 1)) {
					dateOutOfWeek = true;
				}
			}

			holder.mHeaderContainer.setVisibility(View.GONE);
			holder.mDivider.setVisibility(View.VISIBLE);
			if (position == 0 || broadcast.getBeginTimeStringLocalDayMonth().equals((getItem(position - 1)).getBeginTimeStringLocalDayMonth()) == false) {
				if (dateOutOfWeek == false) {
					if (mTvDates != null && mTvDates.isEmpty() != true) {
						holder.mHeader.setText(mTvDates.get(dateIndex).getName() + " " + broadcast.getBeginTimeStringLocalDayMonth());
						holder.mHeaderContainer.setVisibility(View.VISIBLE);
					}
				} else {
					holder.mHeader.setText(broadcast.getDayOfWeekString() + " " + broadcast.getBeginTimeStringLocalDayMonth());
					holder.mHeaderContainer.setVisibility(View.VISIBLE);
				}
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
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
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
