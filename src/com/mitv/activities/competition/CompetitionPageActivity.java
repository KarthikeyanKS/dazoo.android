
package com.mitv.activities.competition;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseContentCompetitionActivity;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.CustomViewPager;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.viewpagerindicator.TabPageIndicator;



public class CompetitionPageActivity 
	extends BaseContentCompetitionActivity
	implements ViewCallbackListener, FetchDataProgressCallbackListener 
{
	private static final String TAG = CompetitionPageActivity.class.getName();
	
	
	private Competition competition;
	private Event event;
	
	private TabPageIndicator pageTabIndicator;
	private CustomViewPager viewPager;
	private CompetitionTabFragmentStatePagerAdapter pagerAdapter;
	private int selectedTabIndex;
	
	/* Before */
	private RelativeLayout beforeLayout;
	private RelativeLayout countDownLayout;
	private TextView remainingTimeInDays;
	private TextView remainingTimeInDaysTitle;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInHoursTitle;
	private TextView remainingTimeInMinutes;
	private TextView remainingTimeInMinutesTitle;
	private RelativeLayout nextGameLayout;
	private TextView nextGameText;
	private TextView eventStartTime;
	private TextView tvBroadcastChannels;
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	
	/* Ongoing */
	private RelativeLayout ongoingLayout;
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
	private TextView liteTVBroadcastChannelsOngoing;
	private TextView liveTeam1NameOngoing;
	private ImageView liveTeam1FlagOngoing;
	private TextView liveTeam2NameOngoing;
	private ImageView liveTeam2FlagOngoing;
	private TextView liveTimeLeft;
	private TextView liveGroupHeader;
	
	
	
	private EventCountDownTimer eventCountDownTimer;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded())
		{
			return;
		}
		
		setContentView(R.layout.layout_competition_page);
		
		Intent intent = getIntent();
		
		long competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);

		this.selectedTabIndex = intent.getIntExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, 0);
		
		this.competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);
		
		ContentManager.sharedInstance().setSelectedCompetition(competition);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		initLayout();
		
		setAdapter(selectedTabIndex);
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
	
	
	
	private void setData()
	{
		String competitionName = competition.getDisplayName();
		
		boolean hasBegun = competition.hasBegun();
		boolean hasEnded = competition.hasEnded();
		boolean isOngoing = hasBegun && !hasEnded;
		
		/* Ongoing */
		if (isOngoing) 
		{
			beforeLayout.setVisibility(View.GONE);
			countDownLayout.setVisibility(View.GONE);
			ongoingLayout.setVisibility(View.VISIBLE);
			nextGameLayout.setVisibility(View.GONE);
			nextGameLayoutOngoing.setVisibility(View.VISIBLE);
			liveOngoingLayout.setVisibility(View.VISIBLE);
			
			setOngoingLayoutForLiveEvent();
			setOngoingLayoutForNextEvent();
		}
		
		/* Has ended */
		else if (hasEnded)
		{
			countDownLayout.setVisibility(View.GONE);
			beforeLayout.setVisibility(View.GONE);
			ongoingLayout.setVisibility(View.GONE);
			nextGameLayout.setVisibility(View.GONE);
			nextGameLayoutOngoing.setVisibility(View.GONE);
			liveOngoingLayout.setVisibility(View.GONE);
			/* TODO What to do here????????????? */
		}
		
		/* Before */
		else 
		{
			beforeLayout.setVisibility(View.VISIBLE);
			nextGameLayoutOngoing.setVisibility(View.GONE);
			liveHeaderOngoing.setVisibility(View.GONE);
			countDownLayout.setVisibility(View.VISIBLE);
			nextGameLayout.setVisibility(View.VISIBLE);
			liveOngoingLayout.setVisibility(View.GONE);
			
			long competitionStartTimeInMiliseconds = competition.getBeginTimeCalendarGMT().getTimeInMillis();
			
			long millisecondsUntilEventStart = (competitionStartTimeInMiliseconds - DateUtils.getNowWithGMTTimeZone().getTimeInMillis());
			
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
			
			setBeforeLayout();
		}		
	}
	
	
	
	private void setOngoingLayoutForLiveEvent() {		
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
						
					String team1FlagUrl = team1.getFlagImageURL();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
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
						
					String team2FlagUrl = team2.getFlagImageURL();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);
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
	                Intent intent = new Intent(CompetitionPageActivity.this, EventPageActivity.class);
	                
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
					ImageAware imageAware = new ImageViewAware(team1FlagOngoing, false);
						
					String team1FlagUrl = team1.getFlagImageURL();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
				}
				
				long team2ID = nextEvent.getAwayTeamId();
				
				Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);
	
				if(team2 != null)
				{
					ImageAware imageAware = new ImageViewAware(team2FlagOngoing, false);
						
					String team2FlagUrl = team2.getFlagImageURL();
						
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);
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
				List<EventBroadcast> eventBroadcastDetailsList = nextEvent.getEventBroadcasts();
				
				int totalChannelCount = eventBroadcastDetailsList.size();
				
				List<String> channelNames = new ArrayList<String>(totalChannelCount);
				
				for(EventBroadcast eventBroadcastDetails : eventBroadcastDetailsList)
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
			        Intent intent = new Intent(CompetitionPageActivity.this, EventPageActivity.class);
			        
			        intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, nextEvent.getEventId());
			        intent.putExtra(Constants.INTENT_COMPETITION_NAME, competition.getDisplayName());
			        
			        startActivity(intent);
			    }
			});
		}
	}
	
	
	
	private void setBeforeLayout() {
		String homeTeamName = event.getHomeTeam();
		
		String awayTeamName = event.getAwayTeam();
			
		boolean containsTeamInfo = event.containsTeamInfo();
			
		if(containsTeamInfo)
		{
			long team1ID = event.getHomeTeamId();
			
			Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
			
			if(team1 != null)
			{
				ImageAware imageAware = new ImageViewAware(team1Flag, false);
					
				String team1FlagUrl = team1.getFlagImageURL();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
			}
			else
			{
				Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
			}
			
			long team2ID = event.getAwayTeamId();
			
			Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);

			if(team2 != null)
			{
				ImageAware imageAware = new ImageViewAware(team2Flag, false);
					
				String team2FlagUrl = team2.getFlagImageURL();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);
			}
			else
			{
				Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
			}
		}
		
		team1Name.setText(homeTeamName);
		
		team2Name.setText(awayTeamName);
		
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.competition_page_first_game))
			.append(" - ")
			.append(event.getEventTimeDayOfTheWeekAsString())
			.append(" ")
			.append(event.getEventTimeDayAndMonthAsString());
		
		nextGameText.setText(sb.toString());
		
		String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
		
		eventStartTime.setText(eventStartTimeHourAndMinuteAsString);
		
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
							 
					channelsSB.append("+ ");
					channelsSB.append(remainingChannels);
					channelsSB.append(" ");
					channelsSB.append(getResources().getString(R.string.competition_page_more_channels_broadcasting));
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
			tvBroadcastChannels.setText(channels);
			tvBroadcastChannels.setVisibility(View.VISIBLE);
		}
		else
		{
			tvBroadcastChannels.setVisibility(View.GONE);
		}
		
		nextGameLayout.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(CompetitionPageActivity.this, EventPageActivity.class);
                
                intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, event.getEventId());
                intent.putExtra(Constants.INTENT_COMPETITION_NAME, competition.getDisplayName());
                
                startActivity(intent);
            }
        });
	}
	
	
	
	private void initLayout()
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		String competitionName = competition.getDisplayName();
		
		actionBar.setTitle(competitionName);
		
		/* Before */
		countDownLayout = (RelativeLayout) findViewById(R.id.competition_count_down_area);
		remainingTimeInDays = (TextView) findViewById(R.id.competition_days_number);
		remainingTimeInDaysTitle = (TextView) findViewById(R.id.competition_days_title);
		remainingTimeInHours = (TextView) findViewById(R.id.competition_hours_number);
		remainingTimeInHoursTitle = (TextView) findViewById(R.id.competition_hours_title);
		remainingTimeInMinutes = (TextView) findViewById(R.id.competition_minutes_number);
		remainingTimeInMinutesTitle = (TextView) findViewById(R.id.competition_minutes_title);
		
		/* Before */
		beforeLayout = (RelativeLayout) findViewById(R.id.competition_page_before_layout);
		nextGameLayout = (RelativeLayout) findViewById(R.id.competition_next_game_layout);
		eventStartTime = (TextView) findViewById(R.id.competition_page_begin_time_broadcast);
		tvBroadcastChannels = (TextView) findViewById(R.id.competition_airing_channels_for_broadcast);
		team1Name = (TextView) findViewById(R.id.competition_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_team_two_flag);
		nextGameText = (TextView) findViewById(R.id.competition_next_game_text);
		
		/* Ongoing - LIVE */
		liveOngoingLayout = (RelativeLayout) findViewById(R.id.competition_ongoing_live_game_layout);
		liveGroupHeader = (TextView) findViewById(R.id.competition_ongoing_group_header);
		liveOngoingStandings = (TextView) findViewById(R.id.competition_ongoing_live_standing);
		liveTeam1NameOngoing = (TextView) findViewById(R.id.competition_ongoing_team_one_name);
		liveTeam1FlagOngoing = (ImageView) findViewById(R.id.competition_ongoing_team_one_flag);
		liveTeam2NameOngoing = (TextView) findViewById(R.id.competition_ongoing_team_two_name);
		liveTeam2FlagOngoing = (ImageView) findViewById(R.id.competition_ongoing_team_two_flag);
		liveTimeLeft = (TextView) findViewById(R.id.competition_ongoing_live_time);
		
		/* Ongoing - NEXT GAME */
		ongoingLayout = (RelativeLayout) findViewById(R.id.competition_page_ongoing_layout);
		liveHeaderOngoing = (TextView) findViewById(R.id.competition_ongoing_header_live);
		nextGameHeaderOngoing = (TextView) findViewById(R.id.competition_ongoing_header_next_game);
		nextGameLayoutOngoing = (RelativeLayout) findViewById(R.id.competition_ongoing_next_game_layout);
		eventStartTimeOngoing = (TextView) findViewById(R.id.competition_page_ongoing_begin_time_broadcast);
		tvBroadcastChannelsOngoing = (TextView) findViewById(R.id.competition_ongoing_airing_channels_for_broadcast);
		team1NameOngoing = (TextView) findViewById(R.id.competition_ongoing_next_team_one_name);
		team1FlagOngoing = (ImageView) findViewById(R.id.competition_ongoing_next_team_one_flag);
		team2NameOngoing = (TextView) findViewById(R.id.competition_ongoing_next_team_two_name);
		team2FlagOngoing = (ImageView) findViewById(R.id.competition_ongoing_next_team_two_flag);
		nextGameTextOngoing = (TextView) findViewById(R.id.competition_ongoing_next_game_text);
		
		pageTabIndicator = (TabPageIndicator) findViewById(R.id.tab_event_indicator);
		
		viewPager = (CustomViewPager) findViewById(R.id.tab_event_pager);
	}
	
	
	
	private void setAdapter(int selectedIndex) 
	{
		pagerAdapter = new CompetitionTabFragmentStatePagerAdapter(getSupportFragmentManager(), viewPager, competition.getCompetitionId());
	
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
//		viewPager.setBoundaryCaching(true);
		viewPager.setCurrentItem(selectedIndex);
		viewPager.setVisibility(View.VISIBLE);
		viewPager.setEnabled(false);

		pagerAdapter.notifyDataSetChanged();
		
		pageTabIndicator.setVisibility(View.VISIBLE);
		pageTabIndicator.setViewPager(viewPager);
		viewPager.setScreenHeight(GenericUtils.getScreenHeight(this));
		
		pagerAdapter.notifyDataSetChanged();
		pageTabIndicator.setCurrentItem(selectedIndex);
		
		pageTabIndicator.setInitialStyleOnAllTabs();
		pageTabIndicator.setStyleOnTabViewAtIndex(selectedIndex);
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		/* Always re-fetch the data from the service */
//		boolean forceRefreshOfCompetitionInitialData = true;
		boolean forceRefreshOfCompetitionInitialData = false;
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, forceRefreshOfCompetitionInitialData, competition.getCompetitionId());
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = ContentManager.sharedInstance().getFromCacheHasCompetitionData(competition.getCompetitionId());
		
		return hasData;
	}



	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			boolean filterFinishedEvents = false;
			boolean filterLiveEvents = false;
			event = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition(filterFinishedEvents, filterLiveEvents);
	
			if(event == null)
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
			} 
			else
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			}
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}	
	}



	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}
}
