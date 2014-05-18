package com.mitv.adapters.list;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcastDetails;
import com.mitv.ui.elements.ReminderView;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class CompetitionEventPageBroadcastListAdapter 
extends BaseAdapter
{
	private static final String TAG = CompetitionEventsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private List<EventBroadcastDetails> broadcastDetails;
	private boolean isAiring;
	private boolean hasEnded;
	
	
	
	public CompetitionEventPageBroadcastListAdapter(
			final Activity activity,
			final List<EventBroadcastDetails> broadcastDetails)
	{
		super();
		
		this.broadcastDetails = broadcastDetails;
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (broadcastDetails != null) 
		{
			count = broadcastDetails.size();
		}
		
		return count;
	}

	
	
	@Override
	public EventBroadcastDetails getItem(int position) 
	{
		EventBroadcastDetails detail = null;
		
		if (broadcastDetails != null)
		{
			detail = broadcastDetails.get(position);
		}
		
		return detail;
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

		if (rowView == null)
		{
			rowView = layoutInflater.inflate(R.layout.row_competition_event_broadcast_list_item, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.channelLogo = (ImageView) rowView.findViewById(R.id.competition_event_channel_logo);
			viewHolder.beginTime = (TextView) rowView.findViewById(R.id.competition_event_full_date);
			viewHolder.reminderView = (TextView) rowView.findViewById(R.id.competition_event_row_reminders_notification_iv);
			viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.competition_event_broadcast_progressbar_2);
			viewHolder.onGoingTimeLeft = (TextView) rowView.findViewById(R.id.competition_event_time_left_ongoing);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (holder != null)
		{
			final EventBroadcastDetails details = getItem(position);
			
			isAiring = details.isAiring();
			hasEnded = details.hasEnded();
			
			/* Set channel logo */
			
			ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);
			
			TVChannel tvChannel = details.getTVChannelForEventBroadcast();
			
			String logoUrl = tvChannel.getLogo().getSmall();
				
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(logoUrl, imageAware);
			
			/* Set airing date and time */
			
			StringBuilder sb = new StringBuilder();
			
			String startTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(details.getEventBroadcastBeginTimeLocal());
			String endTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(details.getEventBroadcastEndTimeLocal());
			
			int totalMinutesInGame = DateUtils.getMinutesInEvent(details.getEventBroadcastBeginTimeLocal());
			int totalMinutesOfGame = details.getTotalAiringTimeInMinutes();
			
			/* Event is ongoing */
			if (isAiring) {
				LanguageUtils.setupOnlyProgressBar(activity, totalMinutesInGame, totalMinutesOfGame, holder.progressBar);
				
				sb.append(details.getEventTimeDayOfTheWeekAsString())
				.append(", ")
				.append(startTimeHourAndMinuteAsString)
				.append(" - ")
				.append(endTimeHourAndMinuteAsString);
				
				/* Set minutes left */
				
				int minutesLeft = totalMinutesOfGame - totalMinutesInGame;
				
				StringBuilder sbMinutesLeft = new StringBuilder();
				sbMinutesLeft.append(minutesLeft)
					.append(" ")
					.append(activity.getResources().getString(R.string.event_page_minutes_left));
				
				holder.onGoingTimeLeft.setText(sbMinutesLeft.toString());
				
				holder.onGoingTimeLeft.setVisibility(View.VISIBLE);
				holder.reminderView.setVisibility(View.GONE);
			}
			
			/* Event has ended */
			else if (hasEnded) {
				sb.append("Game has ended!");
				holder.beginTime.setTextColor(activity.getResources().getColor(R.color.black));
				holder.onGoingTimeLeft.setVisibility(View.GONE);
				holder.reminderView.setVisibility(View.GONE);
			}
			
			/* Event has not started yet */
			else {
				LanguageUtils.setupOnlyProgressBar(activity, totalMinutesInGame, totalMinutesOfGame, holder.progressBar);
				sb.append(details.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(details.getEventTimeDayAndMonthAsString())
				.append(", ")
				.append(startTimeHourAndMinuteAsString)
				.append(" - ")
				.append(endTimeHourAndMinuteAsString);
				
				/* TODO
				 * 
				 * WARNING WARNING WARNING
				 * 
				 * The notifications for event is not finished  */
				
//				holder.reminderView.setCompetitionEventBroadcast(event, details);
				
//				boolean iconSizeSmall = true;
//				holder.reminderView.setSizeOfIcon(iconSizeSmall);
				
				holder.beginTime.setTextColor(activity.getResources().getColor(R.color.black));
				holder.onGoingTimeLeft.setVisibility(View.GONE);
				holder.reminderView.setVisibility(View.VISIBLE);
			}
			
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.channelLogo.setVisibility(View.VISIBLE);
			holder.beginTime.setText(sb.toString());
			holder.beginTime.setVisibility(View.VISIBLE);
			
		}
		else
		{
			Log.w(TAG, "Details is null");
		}
		
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private ImageView channelLogo;
		private TextView beginTime;
		private TextView reminderView;
		private ProgressBar progressBar;
		private TextView onGoingTimeLeft;
	}
	
}