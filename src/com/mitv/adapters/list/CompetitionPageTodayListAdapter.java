
package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.List;

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
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.utilities.DateUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionPageTodayListAdapter 
	extends BaseAdapter
{
	private static final String TAG = CompetitionPageTodayListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private List<Event> events;
	private Activity activity;
	private String competitionDisplayName;
	
	
	
	public CompetitionPageTodayListAdapter(
			final Activity activity,
			final List<Event> events,
			final String competitionDisplayName)
	{
		super();

		this.events = events;
		
		this.activity = activity;
		
		this.competitionDisplayName = competitionDisplayName;
		
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
			rowView = layoutInflater.inflate(R.layout.row_today_ongoing_competition_page_live_and_upcoming, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.liveOngoingLayout = (RelativeLayout) rowView.findViewById(R.id.competition_ongoing_live_game_layout);
			viewHolder.liveOngoingStandings = (TextView) rowView.findViewById(R.id.competition_ongoing_live_standing);
			viewHolder.liveTeam1NameOngoing = (TextView) rowView.findViewById(R.id.competition_ongoing_team_one_name);
			viewHolder.liveTeam1FlagOngoing = (ImageView) rowView.findViewById(R.id.competition_ongoing_team_one_flag);
			viewHolder.liveTeam2NameOngoing = (TextView) rowView.findViewById(R.id.competition_ongoing_team_two_name);
			viewHolder.liveTeam2FlagOngoing = (ImageView) rowView.findViewById(R.id.competition_ongoing_team_two_flag);
			viewHolder.liveTimeLeft = (TextView) rowView.findViewById(R.id.competition_ongoing_live_time);
			viewHolder.liveChannels = (TextView) rowView.findViewById(R.id.competition_ongoing_channels_for_broadcast_live);
			viewHolder.header = (TextView) rowView.findViewById(R.id.competition_ongoing_group_header);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (holder != null)
		{
			final Event event = getItem(position);
			
			if (event != null) 
			{
				holder.header.setVisibility(View.GONE);
				
				boolean isFirstposition = (position == 0);
				
				boolean shouldBeNewHeader;

				if(isFirstposition == false)
				{
					Event prevEvent = getItem(position - 1);
					
					if (prevEvent.isLive() && event.isLive()) {
						shouldBeNewHeader = false;
						
					} else if (prevEvent.isLive() && !event.isLive()) {
						/* Set to true if header will be used */
//						shouldBeNewHeader = true;
						shouldBeNewHeader = false;
						
					} else if (!prevEvent.isLive() && !event.isLive()) {
						shouldBeNewHeader = false;
						
					} else {
						shouldBeNewHeader = false;
					}
				}
				else
				{					
					/* Set to true if header will be used */
//					shouldBeNewHeader = true;
					shouldBeNewHeader = false;
					isFirstposition = false;
				}

				if (isFirstposition || shouldBeNewHeader)
				{
					String headerText;
					
					if (event.isLive()) {
						headerText = "Playing Now";
						
					} else {
						headerText = "Upcoming Games";
					}
					
					holder.header.setText(headerText.toUpperCase());

					holder.header.setVisibility(View.VISIBLE);
				}
				
				
				String homeTeamName = event.getHomeTeam();
				
				String awayTeamName = event.getAwayTeam();
				
				boolean containsTeamInfo = event.containsTeamInfo();
				
				if(containsTeamInfo)
				{
					long team1ID = event.getHomeTeamId();
					
					Team team1 = ContentManager.sharedInstance().getCacheManager().getTeamById(team1ID);
					
					if(team1 != null)
					{
						ImageAware imageAware = new ImageViewAware(holder.liveTeam1FlagOngoing, false);
							
						String team1FlagUrl = team1.getFlagImageURL();
							
						SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
					}
					else
					{
						Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
					}
					
					long team2ID = event.getAwayTeamId();
					
					Team team2 = ContentManager.sharedInstance().getCacheManager().getTeamById(team2ID);

					if(team2 != null)
					{
						ImageAware imageAware = new ImageViewAware(holder.liveTeam2FlagOngoing, false);
							
						String team2FlagUrl = team2.getFlagImageURL();
							
						SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);
					}
					else
					{
						Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
					}
				}
				
				holder.liveTeam1NameOngoing.setText(homeTeamName);
				
				holder.liveTeam2NameOngoing.setText(awayTeamName);
				
				boolean isLive = event.isLive();
				
				if (isLive) {
					String score = event.getScoreAsString();
					
					holder.liveOngoingStandings.setText(score);
					
					String timeInGame = event.getGameTimeAndStatusAsString(true);
					
					StringBuilder sb = new StringBuilder();
					sb.append(activity.getResources().getString(R.string.icon_live))
						.append(" ")
						.append(timeInGame);
					
					holder.liveTimeLeft.setText(sb.toString());
					
					holder.liveOngoingStandings.setTextColor(activity.getResources().getColor(R.color.red));
					holder.liveTimeLeft.setTextColor(activity.getResources().getColor(R.color.red));
					
				} else {
					String startTime = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
					
					holder.liveOngoingStandings.setText(startTime);
					
					holder.liveTimeLeft.setVisibility(View.GONE);
				}
				
				/* BROADCASTS */
				
				StringBuilder channelsSB = new StringBuilder();
				
				boolean containsBroadcastDetails = event.containsBroadcastDetails();
				
				if(containsBroadcastDetails)
				{
					List<EventBroadcast> eventBroadcastDetailsList = event.getEventBroadcasts();
					
					int totalChannelCount = eventBroadcastDetailsList.size();
					
					List<String> channelNames = new ArrayList<String>(totalChannelCount);
					
					for(EventBroadcast eventBroadcastDetails : eventBroadcastDetailsList)
					{
						String channelID = eventBroadcastDetails.getChannelId();
						
						TVChannelId tvChannelId = new TVChannelId(channelID);
						
						TVChannel tvChannel = ContentManager.sharedInstance().getCacheManager().getTVChannelById(tvChannelId);
						
						if(tvChannel != null)
						{
							channelNames.add(tvChannel.getName());
						}
						else
						{
							Log.w(TAG, "No matching TVChannel ID was found for ID: " + channelID);
						}
					}
					
					for(int i=0; i<channelNames.size(); i++)
					{
						if(i >= Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON)
						{
							int remainingChannels = totalChannelCount - Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON;
									 
							channelsSB.append("+ ");
							channelsSB.append(remainingChannels);
							channelsSB.append(" ");
							channelsSB.append(activity.getResources().getString(R.string.competition_page_more_channels_broadcasting));
							break;
						}
						
						channelsSB.append(channelNames.get(i));
						
						if(i != channelNames.size()-1)
						{
							channelsSB.append(", ");
						}
					}
				}
				
				String channels = channelsSB.toString();
				
				if (channels != null && !channels.isEmpty() && channels != "")
				{
					holder.liveChannels.setText(channels);
					holder.liveChannels.setVisibility(View.VISIBLE);
				}
				else
				{
					holder.liveChannels.setVisibility(View.INVISIBLE);
				}
				
				holder.liveOngoingLayout.setOnClickListener(new View.OnClickListener() 
		        {
		            public void onClick(View v)
		            {
		            	if (event != null) 
		            	{
		            		TrackingGAManager.sharedInstance().sendUserCompetitionEventPressedEvent(competitionDisplayName, event.getTitle(), event.getEventId(), "Live Game");
		            	}
		            	
		                Intent intent = new Intent(activity, EventPageActivity.class);

		                intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
		                intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, event.getEventId());
		                
		                activity.startActivity(intent);
		            }
		        });
			}
			else 
			{
				/* Hide container */
				holder.liveOngoingLayout.setVisibility(View.GONE);
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
		private RelativeLayout liveOngoingLayout;
		private TextView liveOngoingStandings;
		private TextView liveTeam1NameOngoing;
		private ImageView liveTeam1FlagOngoing;
		private TextView liveTeam2NameOngoing;
		private ImageView liveTeam2FlagOngoing;
		private TextView liveTimeLeft;
		private TextView liveChannels;
		private TextView header;
	}
	
}
