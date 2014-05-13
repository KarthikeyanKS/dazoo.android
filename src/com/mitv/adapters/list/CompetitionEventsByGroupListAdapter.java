
package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.competition.EventPageActivity;
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



public class CompetitionEventsByGroupListAdapter 
	extends BaseAdapter
{
	private static final String TAG = CompetitionEventsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private Map<Long, List<Event>> eventsByGroup;
	private List<Event> events;
	private Event event;
	
	
	
	public CompetitionEventsByGroupListAdapter(
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
			rowView = layoutInflater.inflate(R.layout.row_competition_page_list_item, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.row_competition_row_container);

			viewHolder.group = (TextView) rowView.findViewById(R.id.row_competition_header_group_event);
			
			viewHolder.startWeekDayHeader = (TextView) rowView.findViewById(R.id.row_competition_start_day_of_week);
			viewHolder.dividerView = rowView.findViewById(R.id.row_competition_row_divider);
			
			viewHolder.team1name = (TextView) rowView.findViewById(R.id.row_competition_team_one_name);
			viewHolder.team1flag = (ImageView) rowView.findViewById(R.id.row_competition_team_one_flag);
			viewHolder.team2name = (TextView) rowView.findViewById(R.id.row_competition_team_two_name);
			viewHolder.team2flag = (ImageView) rowView.findViewById(R.id.row_competition_team_two_flag);
			
			viewHolder.startTime = (TextView) rowView.findViewById(R.id.row_competition_page_begin_time_broadcast);
			
			// TODO - Fix this
//			viewHolder.score = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
//			viewHolder.timeLeft = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			
			viewHolder.broadcastChannels = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (holder != null)
		{
			holder.startWeekDayHeader.setVisibility(View.GONE);
			holder.dividerView.setVisibility(View.GONE);
			holder.group.setVisibility(View.GONE);
			
			event = getItem(position);
			
			boolean isFirstposition = (position == 0);

			boolean isLastPosition = (position == (getCount() - 1));

			boolean isCurrentEventDayEqualToPreviousEventDay;
			boolean isCurrentEventGroupEqualToPreviousEventGroup;

			if(isFirstposition == false)
			{
				Event prevEvent = getItem(position - 1);

				isCurrentEventDayEqualToPreviousEventDay = event.isTheSameDayAs(prevEvent);
				
				isCurrentEventGroupEqualToPreviousEventGroup = event.isSamePhase(prevEvent);
			}
			else
			{
				isCurrentEventDayEqualToPreviousEventDay = true;
				isCurrentEventGroupEqualToPreviousEventGroup = true;
			}

			boolean isBeginTimeEqualToNextItem;

			if(isLastPosition == false)
			{
				Event nextEvent = getItem(position + 1);

				isBeginTimeEqualToNextItem = event.isTheSameDayAs(nextEvent);
			}
			else
			{
				isBeginTimeEqualToNextItem = false;
			}

			if (isFirstposition || isCurrentEventDayEqualToPreviousEventDay == false) 
			{
				StringBuilder sb = new StringBuilder();

				boolean isBeginTimeTodayOrTomorrow = event.isEventTimeTodayOrTomorrow();

				if(isBeginTimeTodayOrTomorrow)
				{
					sb.append(event.getEventTimeDayOfTheWeekAsString());
				}
				else
				{
					sb.append(event.getEventTimeDayOfTheWeekAsString());
					sb.append(" ");
					sb.append(event.getEventTimeDayAndMonthAsString());
				}
				
				/* Capitalized letters in header */
				String headerText = sb.toString();
				holder.startWeekDayHeader.setText(headerText.toUpperCase());

				holder.startWeekDayHeader.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.dividerView.setVisibility(View.VISIBLE);
			}
			
			if (isFirstposition || isCurrentEventGroupEqualToPreviousEventGroup == false)
			{
				long phaseID = event.getPhaseId();
				
				Phase phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(phaseID);
				
				if(phase != null)
				{
					String headerGroup = phase.getPhase();
					holder.group.setText(headerGroup);
				}
				else
				{
					holder.group.setText("");
				}

				holder.group.setVisibility(View.VISIBLE);
				
				holder.startWeekDayHeader.setVisibility(View.VISIBLE);
			}
			
			if (isLastPosition == false && isBeginTimeEqualToNextItem == false)
			{
				holder.dividerView.setVisibility(View.VISIBLE);
			}			
			
			String homeTeamName = event.getHomeTeam();
			
			String awayTeamName = event.getAwayTeam();
			
			boolean containsTeamInfo = event.containsTeamInfo();
			
			if(containsTeamInfo)
			{
				long team1ID = event.getHomeTeamId();
				
				Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
				
				if(team1 != null)
				{
					ImageAware imageAware = new ImageViewAware(holder.team1flag, false);
						
					String team1FlagUrl = team1.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team1FlagUrl, imageAware);
				}
				else
				{
					Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
				}
					
				
				long team2ID = event.getAwayTeamId();
				
				Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);
				
				if(team2 != null)
				{
					ImageAware imageAware = new ImageViewAware(holder.team2flag, false);
						
					String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team2FlagUrl, imageAware);
				}
				else
				{
					Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
				}
			}
			
			holder.team1name.setText(homeTeamName);
			
			holder.team2name.setText(awayTeamName);
			
			// TODO Set remaining variables: score and timeLeft
			
			/* Start time */
			String start = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
			holder.startTime.setText(start);
			
			StringBuilder channelsSB = new StringBuilder();
			
			boolean containsBroadcastDetails = event.containsBroadcastDetails();
			
			if(containsBroadcastDetails)
			{
				List<EventBroadcastDetailsJSON> eventBroadcastDetailsList = event.getBroadcastDetails();
				
				int totalChannelCount = eventBroadcastDetailsList.size();
				
				List<String> channelNames = new ArrayList<String>(totalChannelCount);
				
				for(EventBroadcastDetailsJSON eventBroadcastDetails : eventBroadcastDetailsList)
				{
					String channelID = eventBroadcastDetails.getChannelId();
					
					TVChannelId tvChannelId = new TVChannelId(channelID);
					
					TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
					
					if(tvChannel != null)
					{
						channelNames.add(tvChannel.getName());
					}
					else
					{
						Log.w(TAG, "No matching TVChannel ID was found for ID: " + channelID);
					}
				}
				
				for(int j=0; j<channelNames.size(); j++)
				{
					if(j >= Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON)
					{
						int remainingChannels = totalChannelCount-Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON;
								
						channelsSB.append("+ ");
						channelsSB.append(remainingChannels);
						channelsSB.append(" ");
						channelsSB.append(activity.getString(R.string.competition_page_more_channels_broadcasting));
						break;
					}
					
					channelsSB.append(channelNames.get(j));
					
					if(j != channelNames.size()-1)
					{
						channelsSB.append(", ");
					}
				}
			}
			
			holder.broadcastChannels.setText(channelsSB.toString());
			
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
		
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private TextView group;
		private TextView startWeekDayHeader;
		private TextView team1name;
		private ImageView team1flag;
		private TextView team2name;
		private ImageView team2flag;
		private TextView startTime;
		private TextView score;
		private TextView timeLeft;
		private TextView broadcastChannels;
		private View dividerView;
		private RelativeLayout container;
	}
	
}
