
package com.mitv.fragments;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcastDetails;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.utilities.DateUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class TVGuideTabFragmentCompetition
	extends TVGuideTabFragment
	implements OnPageChangeListener
{
	private static final String TAG = TVGuideTabFragmentCompetition.class.getName();
	
	
	private Competition competition;
	private Event event;
	
	private long competitionID;
	
	private EventCountDownTimer eventCountDownTimer;
	
	private RelativeLayout countDownAreaContainer;
	private TextView remainingTimeInDays;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInMinutes;
	private TextView remainingTimeInDaysTitle;
	private TextView remainingTimeInHoursTitle;
	private TextView remainingTimeInMinutesTitle;
	private TextView title;
	private RelativeLayout competitionGoToScheduleLayout;
	
	/* Ongoing */
	private TextView liveHeaderOngoing;
	private TextView nextGameHeaderOngoing;
	private RelativeLayout nextGameLayoutOngoing;
	private TextView nextGameTextOngoing;
	private TextView eventStartTimeOngoing;
	private TextView tvBroadcastChannelsOngoing;
	private TextView team1NameOngoing;
	private ImageView team1FlagOngoing;
	private TextView team2NameOngoing;
	private ImageView team2FlagOngoing;
	
	/* Ongoing - Live */
	private RelativeLayout liveOngoingLayout;
	private TextView liveOngoingStandings;
//	private TextView liteTVBroadcastChannelsOngoing;
	private TextView liveTeam1NameOngoing;
	private ImageView liveTeam1FlagOngoing;
	private TextView liveTeam2NameOngoing;
	private ImageView liveTeam2FlagOngoing;
	private TextView liveTimeLeft;
	private TextView liveGroupHeader;
	private View ongoingContainer;
	
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public TVGuideTabFragmentCompetition()
	{
		super();
	}
	
	
	
	public TVGuideTabFragmentCompetition(long competitionID, String competitionDisplayName)
	{
		super(new Long(competitionID).toString(), competitionDisplayName, TVGuideTabTypeEnum.COMPETITION);

		this.competitionID = competitionID;
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.layout_competition_page_main, null);

		super.initRequestCallbackLayouts(rootView);
		
		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		RelativeLayout learnMoreButton = (RelativeLayout) rootView.findViewById(R.id.competition_go_to_schedule_layout);
		
		initView();
		
        learnMoreButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
            	TrackingManager.sharedInstance().sendUserCompetitionTabCalendarPressed(getCompetition().getDisplayName());
            	
                Intent intent = new Intent(activity, CompetitionPageActivity.class);
                
                intent.putExtra(Constants.INTENT_COMPETITION_ID, getCompetition().getCompetitionId());
                
                activity.startActivity(intent);
            }
        });
        
        RelativeLayout competitionCountDownAreaLayout = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_layout);
        
        competitionCountDownAreaLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
                TrackingManager.sharedInstance().sendUserCompetitionTabCountdownPressed(getCompetition().getDisplayName());
            }
        });
        
        RelativeLayout competitionBannerLayout = (RelativeLayout) rootView.findViewById(R.id.competition_banner_layout);
        
        competitionBannerLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
                TrackingManager.sharedInstance().sendUserCompetitionTabCountdownPressed(getCompetition().getDisplayName());
            }
        });
        
        if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
        	competitionID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_ID, 0);
        }
		
		return rootView;
	}
	
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        
        outState.putLong(Constants.INTENT_COMPETITION_ID, competitionID);
    }
	
	
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if(eventCountDownTimer != null)
		{
			eventCountDownTimer.cancel();
		}
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
//		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionsData(this, false);
		
		/* Always re-fetch the data from the service */
		boolean forceRefreshOfCompetitionInitialData = true;
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, forceRefreshOfCompetitionInitialData, getCompetition().getCompetitionId());
		
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasCompetitionData(competitionID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
		} 
		else 
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setData();
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	@Override
	public void onTimeChange(int hour){}
	
	
	
	/* Tab elements */
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}
	

	
	@Override
	public void onPageSelected(int pos) {}

	
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	
	
	@Override
	public void onPageScrollStateChanged(int arg0) {}
	
	
	
	private void initView()
	{
		countDownAreaContainer = (RelativeLayout) rootView.findViewById(R.id.time_left_section);
		
		remainingTimeInDays = (TextView) rootView.findViewById(R.id.competition_days_number);
		remainingTimeInHours = (TextView) rootView.findViewById(R.id.competition_hours_number);
		remainingTimeInMinutes = (TextView) rootView.findViewById(R.id.competition_minutes_number);
		
		remainingTimeInDaysTitle = (TextView) rootView.findViewById(R.id.competition_days_title);
		remainingTimeInHoursTitle = (TextView) rootView.findViewById(R.id.competition_hours_title);
		remainingTimeInMinutesTitle = (TextView) rootView.findViewById(R.id.competition_minutes_title);
		
		title = (TextView) rootView.findViewById(R.id.competition_title);
		
		competitionGoToScheduleLayout = (RelativeLayout) rootView.findViewById(R.id.competition_go_to_schedule_layout);
		
		/* Ongoing - LIVE */
		liveOngoingLayout = (RelativeLayout) rootView.findViewById(R.id.competition_ongoing_live_game_layout);
		liveGroupHeader = (TextView) rootView.findViewById(R.id.competition_ongoing_group_header);
		liveOngoingStandings = (TextView) rootView.findViewById(R.id.competition_ongoing_live_standing);
//		liteTVBroadcastChannelsOngoing = rootView.findViewById(R.id.);
		liveTeam1NameOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_team_one_name);
		liveTeam1FlagOngoing = (ImageView) rootView.findViewById(R.id.competition_ongoing_team_one_flag);
		liveTeam2NameOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_team_two_name);
		liveTeam2FlagOngoing = (ImageView) rootView.findViewById(R.id.competition_ongoing_team_two_flag);
		liveTimeLeft = (TextView) rootView.findViewById(R.id.competition_ongoing_live_time);
		
		/* Ongoing - NEXT GAME */
		ongoingContainer = rootView.findViewById(R.id.competition_tag_ongoing_live_and_next_event_container);
		liveHeaderOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_header_live);
		nextGameHeaderOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_header_next_game);
		nextGameLayoutOngoing = (RelativeLayout) rootView.findViewById(R.id.competition_ongoing_next_game_layout);
		eventStartTimeOngoing = (TextView) rootView.findViewById(R.id.competition_page_ongoing_begin_time_broadcast);
		tvBroadcastChannelsOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_airing_channels_for_broadcast);
		team1NameOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_next_team_one_name);
		team1FlagOngoing = (ImageView) rootView.findViewById(R.id.competition_ongoing_next_team_one_flag);
		team2NameOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_next_team_two_name);
		team2FlagOngoing = (ImageView) rootView.findViewById(R.id.competition_ongoing_next_team_two_flag);
		nextGameTextOngoing = (TextView) rootView.findViewById(R.id.competition_ongoing_next_game_text);
	}
	
	
	
	private Competition getCompetition()
	{
		if(this.competition == null)
		{
			Log.d(TAG, "Competition ID is: " + competitionID);
			
			this.competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);
		}
		
		return competition;
	}
	
	
	
	private void setData()
	{
		Competition currentCompetition = getCompetition();
		
		boolean hasBegun = currentCompetition.hasBegun();
		boolean hasEnded = currentCompetition.hasEnded();
		boolean isVisible = currentCompetition.isVisible();
		boolean isOngoing = true;
//		boolean isOngoing = hasBegun && !hasEnded && isVisible;
		
		if (isOngoing)
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
		
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
			
			countDownAreaContainer.setVisibility(View.GONE);
			
			ongoingContainer.setVisibility(View.VISIBLE);
			
			setOngoingLayoutForLiveEvent();
			setOngoingLayoutForNextEvent();
		}
		
		else if (hasEnded) {
			
		}
		
		else
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
			
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
			
			countDownAreaContainer.setVisibility(View.VISIBLE);
			
			ongoingContainer.setVisibility(View.GONE);
		
			String competitionName = getCompetition().getDisplayName();
			
			long eventStartTimeInMiliseconds = getCompetition().getBeginTimeCalendarGMT().getTimeInMillis();
				
			long millisecondsUntilEventStart = (eventStartTimeInMiliseconds - DateUtils.getNow().getTimeInMillis());

			eventCountDownTimer = new EventCountDownTimer(
					competitionName, 
					millisecondsUntilEventStart, 
					remainingTimeInDays, 
					remainingTimeInHours, 
					remainingTimeInMinutes,
					remainingTimeInDaysTitle,
					remainingTimeInHoursTitle,
					remainingTimeInMinutesTitle);
				
			eventCountDownTimer.start();
		}
	}
	
	
	
	private void setOngoingLayoutForLiveEvent() {
//		liteTVBroadcastChannelsOngoing;
		
		/* LIVE GAME */
		final Event liveEvent = ContentManager.sharedInstance().getFromCacheLiveEventForSelectedCompetition();
		
		if (liveEvent != null) 
		{
			liveOngoingLayout.setVisibility(View.VISIBLE);
			
			String header = getResources().getString(R.string.competition_page_ongoing_live_header);
			liveHeaderOngoing.setText(header);
			liveHeaderOngoing.setVisibility(View.VISIBLE);
			
			String homeTeamName = liveEvent.getHomeTeam();
			
			String awayTeamName = liveEvent.getAwayTeam();
			
			boolean containsTeamInfo = liveEvent.containsTeamInfo();
			
			if(containsTeamInfo)
			{
				long team1ID = liveEvent.getHomeTeamId();
				
				Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
				
				if(team1 != null)
				{						
					ImageAware imageAware = new ImageViewAware(liveTeam1FlagOngoing, false);
						
					String team1FlagUrl = team1.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team1FlagUrl, imageAware);
				}
				else
				{
					Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
				}
				
				long team2ID = liveEvent.getAwayTeamId();
				
				Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);

				if(team2 != null)
				{
					ImageAware imageAware = new ImageViewAware(liveTeam2FlagOngoing, false);
						
					String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team2FlagUrl, imageAware);
				}
				else
				{
					Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
				}
			}
			
			liveTeam1NameOngoing.setText(homeTeamName);
			
			liveTeam2NameOngoing.setText(awayTeamName);
			
			/* Group name */
			
			long phaseID = liveEvent.getPhaseId();
			
			Phase phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(phaseID);
			
			String groupHeaderName = phase.getPhase();
			
			liveGroupHeader.setText(groupHeaderName);
				
			String score = liveEvent.getScoreAsString();
			
			liveOngoingStandings.setText(score);
			
			String timeInGame = liveEvent.getGameTimeAndStatusAsString(true);
			
			liveTimeLeft.setText(timeInGame);
			
			liveOngoingLayout.setOnClickListener(new View.OnClickListener() 
	        {
	            public void onClick(View v)
	            {
	                Intent intent = new Intent(activity, EventPageActivity.class);
	                
	                intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, liveEvent.getEventId());
	                intent.putExtra(Constants.INTENT_COMPETITION_NAME, competition.getDisplayName());
	                
	                startActivity(intent);
	            }
	        });
		}
		else 
		{
			/* Hide container */
			liveOngoingLayout.setVisibility(View.GONE);
		}
	}
	
	
	
	private void setOngoingLayoutForNextEvent() {
		/* NEXT GAME */
		boolean filterFinishedEvents = true;
		boolean filterLiveEvents = true;
		final Event nextEvent = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition(filterFinishedEvents, filterLiveEvents);
		
		if (nextEvent != null) {
			StringBuilder sbNext = new StringBuilder();
			sbNext.append(getResources().getString(R.string.competition_page_ongoing_next_game_header))
				.append(" - ")
				.append(nextEvent.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(nextEvent.getEventTimeDayAndMonthAsString());
			
			nextGameHeaderOngoing.setText(sbNext.toString());
			
			String homeTeamName = nextEvent.getHomeTeam();
			
			String awayTeamName = nextEvent.getAwayTeam();
				
			boolean containsTeamInfo = nextEvent.containsTeamInfo();
				
			if(containsTeamInfo)
			{
				long team1ID = nextEvent.getHomeTeamId();
				
				Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
				
				if(team1 != null)
				{
					Log.w(TAG, "Local flag for team: " + team1.getNationCode() + " not found in cache");
						
					ImageAware imageAware = new ImageViewAware(team1FlagOngoing, false);
						
					String team1FlagUrl = team1.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team1FlagUrl, imageAware);
				}
				else
				{
					Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
				}
				
				long team2ID = nextEvent.getAwayTeamId();
				
				Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);
	
				if(team2 != null)
				{
					ImageAware imageAware = new ImageViewAware(team2FlagOngoing, false);
						
					String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team2FlagUrl, imageAware);
				}
				else
				{
					Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
				}
			}
			
			team1NameOngoing.setText(homeTeamName);
			
			team2NameOngoing.setText(awayTeamName);
			
			long phaseID = nextEvent.getPhaseId();
			Phase phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(phaseID);
			
			nextGameTextOngoing.setText(phase.getPhase());
			
			String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(nextEvent.getEventDateCalendarLocal());
			
			eventStartTimeOngoing.setText(eventStartTimeHourAndMinuteAsString);
			
			StringBuilder channelsSB = new StringBuilder();
			
			boolean containsBroadcastDetails = nextEvent.containsBroadcastDetails();
			
			if(containsBroadcastDetails)
			{
				List<EventBroadcastDetails> eventBroadcastDetailsList = nextEvent.getEventBroadcastDetails();
				
				int totalChannelCount = eventBroadcastDetailsList.size();
				
				List<String> channelNames = new ArrayList<String>(totalChannelCount);
				
				for(EventBroadcastDetails eventBroadcastDetails : eventBroadcastDetailsList)
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
				
				for(int i=0; i<channelNames.size(); i++)
				{
					if(i >= Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON)
					{
						int remainingChannels = totalChannelCount - Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON;
								 
						channelsSB.append(" + ");
						channelsSB.append(remainingChannels);
						channelsSB.append(" ");
						channelsSB.append(getResources().getString(R.string.competition_page_more_channels_broadcasting));
						break;
					}
					
					channelsSB.append(channelNames.get(i));
				}
			}
			
			String channels = channelsSB.toString();
			
			if (channels != null && !channels.isEmpty() && channels != "")
			{
				tvBroadcastChannelsOngoing.setText(channels);
				tvBroadcastChannelsOngoing.setVisibility(View.VISIBLE);
			}
			else
			{
				tvBroadcastChannelsOngoing.setVisibility(View.GONE);
			}
			
			nextGameLayoutOngoing.setOnClickListener(new View.OnClickListener() 
			{
			    public void onClick(View v)
			    {
			        Intent intent = new Intent(activity, EventPageActivity.class);
			        
			        intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, nextEvent.getEventId());
			        intent.putExtra(Constants.INTENT_COMPETITION_NAME, competition.getDisplayName());
			        
			        startActivity(intent);
			    }
			});
		}
	}
	
}
