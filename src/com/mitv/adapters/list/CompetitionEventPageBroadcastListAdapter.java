package com.mitv.adapters.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Event;
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
	private Event event;
	
	
	
	public CompetitionEventPageBroadcastListAdapter(
			final Activity activity,
			final Event event,
			final List<EventBroadcastDetails> broadcastDetails)
	{
		super();
		
		this.broadcastDetails = broadcastDetails;
		
		this.event = event;
		
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
//			viewHolder.reminderView = (ReminderView) rowView.findViewById(R.id.competition_event_row_reminders_notification_iv);
			viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.competition_event_broadcast_progressbar);
			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.row_competition_broadcast_details_container);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (holder != null)
		{
			final EventBroadcastDetails details = getItem(position);
			
			setChannelLogo(holder, details);
			boolean isOngoing;
			
			/* Event is ongoing */
			if (event.isOngoing() && !event.isPostponed()) {
				isOngoing = true;
				setAiringDate(holder, details, isOngoing);
				setProgressBar(holder, details);
			}
			
			/* Event has not started yet */
			else {
				isOngoing = false;
				setAiringDate(holder, details, isOngoing);
				setReminderIcon(holder, details);
			}
				
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
		
		return rowView;
	}
	
	
	
	/**
	 * Set progress bar
	 * 
	 * @param holder
	 * @param details
	 */
	private void setProgressBar(ViewHolder holder, EventBroadcastDetails details) {
		int totalMinutesInGame = DateUtils.getMinutesInEvent(details.getEventBroadcastBeginTimeLocal());
		int totalMinutesOfEvent = details.getTotalAiringTimeInMinutes();
		
		LanguageUtils.setupOnlyProgressBar(activity, totalMinutesInGame, totalMinutesOfEvent, holder.progressBar);
	}
	
	
	/**
	 * Set airing date
	 * 
	 * @param holder
	 * @param details
	 */
	private void setAiringDate(ViewHolder holder, EventBroadcastDetails details, boolean isOngoing) {	
		StringBuilder sb = new StringBuilder();
		
		String startTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(details.getEventBroadcastBeginTimeLocal());
		String endTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(details.getEventBroadcastEndTimeLocal());
		
		sb.append(details.getEventTimeDayOfTheWeekAsString())
		.append(" ")
		.append(details.getEventTimeDayAndMonthAsString());
		
		holder.beginTime.setText(sb.toString());
		holder.beginTime.setVisibility(View.VISIBLE);
	}
	
	
	
	/**
	 * Set reminder icon
	 * 
	 * @param holder
	 */
	private void setReminderIcon(ViewHolder holder, EventBroadcastDetails details) {
		
		/* TODO
		 * 
		 * WARNING WARNING WARNING
		 * 
		 * The notifications for event is not finished  */
//		holder.reminderView.setCompetitionEventBroadcast(event, details);
		
		/* Used to set a smaller size on the reminder icon */
		boolean iconSizeSmall = true;
		
//		holder.reminderView.setSizeOfIcon(iconSizeSmall);
		
//		holder.reminderView.setVisibility(View.VISIBLE);
		holder.container.setVisibility(View.VISIBLE);
	}
	
	
	
	/**
	 * Set channel logo
	 * 
	 * @param holder
	 * @param details
	 */
	private void setChannelLogo(ViewHolder holder, EventBroadcastDetails details) {
		ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);
		
		TVChannel tvChannel = details.getTVChannelForEventBroadcast();
		
		String logoUrl = tvChannel.getLogo().getSmall();
			
		SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(logoUrl, imageAware);
	}
	
	
	
	private static class ViewHolder 
	{
		private ImageView channelLogo;
		private TextView beginTime;
		private ReminderView reminderView;
		private RelativeLayout container;
		private ProgressBar progressBar;
	}
	
}