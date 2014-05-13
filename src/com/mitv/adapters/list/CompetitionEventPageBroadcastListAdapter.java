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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.multiplayer.realtime.Room;
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
				
				/* Channel logo */
				ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);
				
				TVChannelId tvChannelId = new TVChannelId(details.getChannelId());
				
				TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
				
				String logoUrl = tvChannel.getLogo().getSmall();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(logoUrl, imageAware);
				
				/* Date airing */
				String date = details.getDate();
				holder.beginTime.setText(date);
				
				/* Reminder icon */
				holder.reminderIcon.setVisibility(View.VISIBLE);
				holder.container.setVisibility(View.VISIBLE);
			}
			
			else {
				holder.beginTime.setVisibility(View.GONE);
				holder.reminderIcon.setVisibility(View.GONE);
				holder.container.setVisibility(View.GONE);
			}
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
		
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private ImageView channelLogo;
		private TextView beginTime;
		private TextView reminderIcon;
		private RelativeLayout container;
	}
	
}