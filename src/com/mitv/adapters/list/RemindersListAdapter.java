
package com.mitv.adapters.list;



import java.util.List;

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

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.enums.NotificationTypeEnum;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.Notification;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.utilities.DateUtils;



public class RemindersListAdapter 
	extends BaseAdapter
{
	private static final String TAG = RemindersListAdapter.class.getName();


	private LayoutInflater layoutInflater;
	private Activity activity;
	private List<Notification> notifications;



	public RemindersListAdapter(Activity activity, List<Notification> notifications) 
	{
		this.notifications = notifications;

		this.activity = activity;
	}



	@Override
	public int getCount() 
	{
		if (notifications != null) 
		{
			return notifications.size();
		} 
		else 
		{
			return 0;
		}
	}



	@Override
	public Notification getItem(int position) 
	{
		if (notifications != null) 
		{
			return notifications.get(position);
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

		final Notification element = getItem(position);

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

		if (holder != null)
		{
			holder.mHeaderTv.setVisibility(View.GONE);

			holder.mDividerView.setVisibility(View.VISIBLE);
			
			if (shouldShowHeader(position, element)) 
			{
				StringBuilder headerSB = new StringBuilder();

				boolean isBeginTimeTodayOrTomorrow = element.isBeginTimeTodayOrTomorrow();

				if(isBeginTimeTodayOrTomorrow)
				{
					headerSB.append(DateUtils.buildDayOfTheWeekAsString(element.getBeginTimeCalendarLocal(), false));
				}
				else
				{
					headerSB.append(DateUtils.buildDayOfTheWeekAsString(element.getBeginTimeCalendarLocal(), false));
					headerSB.append(" ");
					headerSB.append(element.getBeginTimeDayAndMonthAsString());
				}

				/* Capitalized letters in header */
				String headerText = headerSB.toString();
				holder.mHeaderTv.setText(headerText.toUpperCase());

				holder.mHeaderTv.setVisibility(View.VISIBLE);
			}
			
			if (shouldHideDivider(position, element))
			{
				holder.mDividerView.setVisibility(View.GONE);
			}
			
			StringBuilder titleSB = new StringBuilder();
			StringBuilder broadcastTimeSB = new StringBuilder();
			StringBuilder channelSB = new StringBuilder();
			StringBuilder detailsSB = new StringBuilder();
			
			broadcastTimeSB.append(element.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			
			channelSB.append(element.getBroadcastChannelName());
				
			titleSB.append(element.getBroadcastTitle());

			detailsSB.append(element.getBroadcastProgramDetails());

			holder.mBroadcastTitleTv.setText(titleSB);
			holder.mBroadcastTimeTv.setText(broadcastTimeSB);
			holder.mChannelTv.setText(channelSB);
			holder.mBroadcastDetailsTv.setText(detailsSB);
			holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);
			holder.mReminderIconIv.setTextColor(activity.getResources().getColor(R.color.blue1));

			
			holder.mInformationContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					TrackingGAManager.sharedInstance().sendUserPressedBroadcastInRemindersList(element);
					
					Intent intent;
					
					NotificationTypeEnum notificationType = element.getNotificationType();
					
					switch(notificationType) 
					{
						case TV_BROADCAST:
						{
							intent = new Intent(activity, BroadcastPageActivity.class);
							
							intent.putExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, element.getBeginTimeInMilliseconds());
							intent.putExtra(Constants.INTENT_EXTRA_CHANNEL_ID, element.getChannelId());
							intent.putExtra(Constants.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, true);
							
							break;
						}
						
						case COMPETITION_EVENT_WITH_EMBEDED_CHANNEL:
						case COMPETITION_EVENT_WITH_LOCAL_CHANNEL:
						{
							intent = new Intent(activity, EventPageActivity.class);
							intent.putExtra(Constants.INTENT_COMPETITION_ID, element.getCompetitionId());
							intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, element.getEventId());
							
							break;
						}
						
						default:
						{
							intent = null;
							Log.w(TAG, "Null intent - default notification type");
							break;
						}
					}
					
					activity.startActivity(intent);
				}
			});

			holder.mReminderIconIv.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					DialogHelper.showRemoveNotificationDialog(activity, element, confirmRemoval(), null);
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
				notifyDataSetChanged();
			}
		};
	}
	
	
	
	private boolean shouldShowHeader(int position, Notification notification) 
	{
		boolean isFirstposition = (position == 0);
		
		boolean isCurrentBroadcastDayEqualToPreviousBroadcastDay;
		
		if (isFirstposition == false)
		{
			Notification previousnotificationInList = getItem(position - 1);
			
			isCurrentBroadcastDayEqualToPreviousBroadcastDay = notification.isTheSameDayAs(previousnotificationInList);
		}
		else
		{
			isCurrentBroadcastDayEqualToPreviousBroadcastDay = false;
		}
		
		if (isFirstposition || isCurrentBroadcastDayEqualToPreviousBroadcastDay == false)
		{
			return true;
		}
		
		return false;
	}
	
	
	
	private boolean shouldHideDivider(int position, Notification notification) {
		boolean isLastPosition = (position == (getCount() - 1));

		int nextPos = Math.min(position + 1, (getCount() - 1));
		
		Notification notificationNextPosition = getItem(nextPos);
		
		boolean isBeginTimeEqualToNextItem = notification.isTheSameDayAs(notificationNextPosition);
		
		if (isLastPosition == false && isBeginTimeEqualToNextItem == false) 
		{
			return true;
		}
		
		return false;
	}
	
}
