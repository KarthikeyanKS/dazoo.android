package com.mitv.adapters.list;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.internal.ev;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.managers.ContentManager;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Team;
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
	
	private Map<Long, List<Event>> eventsByGroup;
	private List<Event> events;
	private Event event;
	private List<EventBroadcastDetailsJSON> broadcastDetails;
	
	
	
	public CompetitionEventPageBroadcastListAdapter(
			final Activity activity,
			final Map<Long, List<Event>> eventsByGroup)
	{
		super();
		
		this.eventsByGroup = eventsByGroup;
		
		this.events = new ArrayList<Event>();
		
		Collection<List<Event>> values = eventsByGroup.values();
		
		for(List<Event> value : values)
		{
			events.addAll(value);
		}
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (events != null) 
		{
			count = events.size();
		}
		
		return count;
	}

	
	
	@Override
	public Event getItem(int position) 
	{
		Event event = null;
		
		if (events != null)
		{
			event = events.get(position);
		}
		
		return event;
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
			viewHolder.reminderIcon = (TextView) rowView.findViewById(R.id.competition_event_row_reminders_notification_iv);
			viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.competition_event_broadcast_progressbar);
			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.row_competition_broadcast_details_container);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (holder != null)
		{
			boolean containsBroadcastDetails = event.containsBroadcastDetails();
			
			if(containsBroadcastDetails) {
				
				broadcastDetails = event.getBroadcastDetails();
				EventBroadcastDetailsJSON details = broadcastDetails.get(position);
				
				setChannelLogo(holder, details);
				
				/* Event is ongoing */
				if (event.isOngoing() && !event.isPostponed()) {
					setProgressBar(holder);
				}
				
				/* Event has not started yet */
				else {
					setAiringDate(holder, details);
				}
			}
				
			else {
				holder.beginTime.setVisibility(View.GONE);
				holder.reminderIcon.setVisibility(View.GONE);
				holder.progressBar.setVisibility(View.GONE);
				holder.container.setVisibility(View.GONE);
			}
				
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
		
		return rowView;
	}
	
	
	
	private void setProgressBar(ViewHolder holder) {
		/* Progress bar */
		Calendar beginTimeCal = event.getEventDateCalendarLocal();
		Calendar endTimeCal = event.getEventDateCalendarLocal();
		
		int totalMinutesInGame = event.countMinutesInGame(beginTimeCal);
		int totalMinutesOfEvent = event.totalMinutesOfEvent(beginTimeCal, endTimeCal);
		
		LanguageUtils.setupOnlyProgressBar(activity, totalMinutesInGame, totalMinutesOfEvent, holder.progressBar);
	}
	
	
	
	private void setAiringDate(ViewHolder holder, EventBroadcastDetailsJSON details) {
		/* Date airing */
		String date = details.getDate();
		holder.beginTime.setText(date);
		
		holder.beginTime.setVisibility(View.VISIBLE);
		
		/* Reminder icon */
		holder.reminderIcon.setVisibility(View.VISIBLE);
		
		holder.container.setVisibility(View.VISIBLE);
	}
	
	
	
	private void setChannelLogo(ViewHolder holder, EventBroadcastDetailsJSON details) {
		/* Channel logo */
		ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);
		
		TVChannelId tvChannelId = new TVChannelId(details.getChannelId());
		
		TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
		
		String logoUrl = tvChannel.getLogo().getSmall();
			
		SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(logoUrl, imageAware);
	}
	
	
	
	private static class ViewHolder 
	{
		private ImageView channelLogo;
		private TextView beginTime;
		private TextView reminderIcon;
		private RelativeLayout container;
		private ProgressBar progressBar;
	}
	
}