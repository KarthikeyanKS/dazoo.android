
package com.mitv.adapters.list;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.models.sql.NotificationDataSource;
import com.mitv.models.sql.NotificationSQLElement;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;



public class RemindersListAdapter 
extends BaseAdapter
{
	private static final String TAG = RemindersListAdapter.class.getName();


	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	private int	currentPosition;



	public RemindersListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> mBroadcasts) 
	{
		this.broadcasts = mBroadcasts;

		this.activity = activity;

		this.currentPosition = -1;
	}



	@Override
	public int getCount() 
	{
		if (broadcasts != null) 
		{
			return broadcasts.size();
		} 
		else 
		{
			return 0;
		}
	}



	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		if (broadcasts != null) 
		{
			return broadcasts.get(position);
		} 
		else 
		{
			return null;	
		}
	}



	@Override
	public long getItemId(int arg0) 
	{
		return -1;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = layoutInflater.inflate(R.layout.row_reminders, null);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.mInformationContainer = (RelativeLayout) rowView.findViewById(R.id.row_reminders_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.row_reminders_header_textview);
			viewHolder.mBroadcastTitleTv = (TextView) rowView.findViewById(R.id.row_reminders_text_title_tv);
			viewHolder.mBroadcastDetailsTv = (TextView) rowView.findViewById(R.id.row_reminders_text_details_tv);
			viewHolder.mBroadcastTimeTv = (TextView) rowView.findViewById(R.id.row_reminders_text_time_tv);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.row_reminders_text_channel_tv);
			viewHolder.mReminderIconIv = (FontTextView) rowView.findViewById(R.id.row_reminders_notification_iv);
			viewHolder.mReminderIconIv.setTag(Integer.valueOf(position));

			viewHolder.mDividerView = (View) rowView.findViewById(R.id.row_reminders_header_divider);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null)
		{
			holder.mHeaderTv.setVisibility(View.GONE);

			holder.mDividerView.setVisibility(View.VISIBLE);

			boolean isFirstposition = (position == 0);

			boolean isLastPosition = (position == (getCount() - 1));

			boolean isCurrentBroadcastDayEqualToPreviousBroadcastDay;

			if(isFirstposition == false)
			{
				TVBroadcastWithChannelInfo previousBroadcastInList = getItem(position - 1);

				isCurrentBroadcastDayEqualToPreviousBroadcastDay = broadcastWithChannelInfo.isTheSameDayAs(previousBroadcastInList);
			}
			else
			{
				isCurrentBroadcastDayEqualToPreviousBroadcastDay = true;
			}

			boolean isBeginTimeEqualToNextItem;

			if(isLastPosition == false)
			{
				TVBroadcastWithChannelInfo nextBroadcastInList = getItem(position + 1);

				isBeginTimeEqualToNextItem = broadcastWithChannelInfo.isTheSameDayAs(nextBroadcastInList);
			}
			else
			{
				isBeginTimeEqualToNextItem = false;
			}

			if (isFirstposition || isCurrentBroadcastDayEqualToPreviousBroadcastDay == false) 
			{
				StringBuilder headerSB = new StringBuilder();

				boolean isBeginTimeTodayOrTomorrow = broadcastWithChannelInfo.isBeginTimeTodayOrTomorrow();

				if(isBeginTimeTodayOrTomorrow)
				{
					headerSB.append(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekAsString());
				}
				else
				{
					headerSB.append(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekAsString());
					headerSB.append(" ");
					headerSB.append(broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				}

				/* Capitalized letters in header */
				String headerText = headerSB.toString();
				holder.mHeaderTv.setText(headerText.toUpperCase());

				holder.mHeaderTv.setVisibility(View.VISIBLE);
			}

			if (isLastPosition == false && isBeginTimeEqualToNextItem == false)
			{
				holder.mDividerView.setVisibility(View.GONE);
			}

			TVProgram tvProgram = broadcastWithChannelInfo.getProgram();

			if (tvProgram != null)
			{
				holder.mBroadcastTitleTv.setText(broadcastWithChannelInfo.getTitle());

				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				ProgramTypeEnum programType = tvProgram.getProgramType();

				switch(programType)
				{
				case TV_EPISODE:
				{
					String seasonAndEpisodeString = broadcastWithChannelInfo.buildSeasonAndEpisodeString();

					holder.mBroadcastDetailsTv.setText(seasonAndEpisodeString);

					break;
				}

				case MOVIE:
				{
					holder.mBroadcastDetailsTv.setText(tvProgram.getGenre() + " " + tvProgram.getYear());

					break;
				}

				case SPORT:
				{
					if (tvProgram.getTournament() != null) 
					{
						holder.mBroadcastDetailsTv.setText(tvProgram.getTournament());
					}
					else 
					{
						holder.mBroadcastDetailsTv.setText(tvProgram.getSportType().getName());
					}
					break;
				}

				case OTHER:
				{
					String category = tvProgram.getCategory();

					holder.mBroadcastDetailsTv.setText(category);

					break;
				}

				case UNKNOWN:
				default:
				{
					Log.w(TAG, "Unhandled program type.");
					break;
				}
				}
			}

			final TVChannel tvChannel = broadcastWithChannelInfo.getChannel();

			holder.mChannelTv.setText(tvChannel.getName());

			holder.mBroadcastTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			holder.mReminderIconIv.setTextColor(activity.getResources().getColor(R.color.blue1));

			holder.mInformationContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					ContentManager.sharedInstance().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);

					Intent intent = new Intent(activity, BroadcastPageActivity.class);

					activity.startActivity(intent);
				}
			});

			holder.mReminderIconIv.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					currentPosition = (Integer) v.getTag();

					NotificationDataSource notificationDataSource = new NotificationDataSource(activity);

					NotificationSQLElement notificationDbItem = notificationDataSource.getNotification(tvChannel.getChannelId(), broadcastWithChannelInfo.getBeginTime());

					if (notificationDbItem != null) 
					{
						int notificationId = notificationDbItem.getNotificationId();

						DialogHelper.showRemoveNotificationDialog(activity, broadcastWithChannelInfo, notificationId, confirmRemoval(), null);
					}
				}
			});
		}

		return rowView;
	}



	private static class ViewHolder 
	{
		private RelativeLayout mInformationContainer;
		private TextView mHeaderTv;
		private TextView mBroadcastTitleTv;
		private TextView mBroadcastDetailsTv;
		private TextView mBroadcastTimeTv;
		private TextView mChannelTv;
		private FontTextView mReminderIconIv;
		private View mDividerView;
	}



	private Runnable confirmRemoval() 
	{
		return new Runnable() 
		{
			public void run() 
			{
				if(currentPosition >= 0 && 
						currentPosition < broadcasts.size()) 
				{
					TVBroadcastWithChannelInfo broadcastForReminderToDelete = broadcasts.get(currentPosition);
					broadcasts.remove(currentPosition);

					TrackingGAManager.sharedInstance().sendUserReminderEvent(broadcastForReminderToDelete, true);

					notifyDataSetChanged();
				}
				else
				{
					Log.e(TAG, "Current position is out of bounds.");
				}
			}
		};
	}
}
