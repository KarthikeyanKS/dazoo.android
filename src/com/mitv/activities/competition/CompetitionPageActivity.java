
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
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.CustomViewPager;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.viewpagerindicator.TabPageIndicator;



public class CompetitionPageActivity 
	extends BaseContentActivity
	implements ViewCallbackListener, FetchDataProgressCallbackListener 
{
	private static final String TAG = CompetitionPageActivity.class.getName();
	
	
	private static final int STARTING_TAB_INDEX = 0;
	
	private Competition competition;
	private Event event;
	
	private TabPageIndicator pageTabIndicator;
	private CustomViewPager viewPager;
	private CompetitionTabFragmentStatePagerAdapter pagerAdapter;
	private int selectedTabIndex;
	
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
	protected void onResume() 
	{
		super.onResume();
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
		
		if (competition.hasBegun())
		{
			countDownLayout.setVisibility(View.GONE);
		}
		else
		{
			countDownLayout.setVisibility(View.VISIBLE);
			
			long competitionStartTimeInMiliseconds = competition.getBeginTimeCalendarGMT().getTimeInMillis();
			
			long millisecondsUntilEventStart = (competitionStartTimeInMiliseconds - DateUtils.getNow().getTimeInMillis());
			
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
		
		String homeTeamName = event.getHomeTeam();
			
		String awayTeamName = event.getAwayTeam();
			
		boolean containsTeamInfo = event.containsTeamInfo();
			
		if(containsTeamInfo)
		{	
			long team1ID = event.getHomeTeamId();
			
			Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
			
			if(team1 != null)
			{
				Log.w(TAG, "Local flag for team: " + team1.getNationCode() + " not found in cache");
					
				ImageAware imageAware = new ImageViewAware(team1Flag, false);
					
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
				ImageAware imageAware = new ImageViewAware(team2Flag, false);
					
				String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team2FlagUrl, imageAware);
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
                
                startActivity(intent);
            }
        });
	}
	
	
	
	private void initLayout()
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		String competitionName = competition.getDisplayName();
		
		actionBar.setTitle(competitionName);
		
		countDownLayout = (RelativeLayout) findViewById(R.id.competition_count_down_area);
		remainingTimeInDays = (TextView) findViewById(R.id.competition_days_number);
		remainingTimeInDaysTitle = (TextView) findViewById(R.id.competition_days_title);
		remainingTimeInHours = (TextView) findViewById(R.id.competition_hours_number);
		remainingTimeInHoursTitle = (TextView) findViewById(R.id.competition_hours_title);
		remainingTimeInMinutes = (TextView) findViewById(R.id.competition_minutes_number);
		remainingTimeInMinutesTitle = (TextView) findViewById(R.id.competition_minutes_title);
		
		nextGameLayout = (RelativeLayout) findViewById(R.id.competition_next_game_layout);
		eventStartTime = (TextView) findViewById(R.id.competition_page_begin_time_broadcast);
		tvBroadcastChannels = (TextView) findViewById(R.id.competition_airing_channels_for_broadcast);
		team1Name = (TextView) findViewById(R.id.competition_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_team_two_flag);
		nextGameText = (TextView) findViewById(R.id.competition_next_game_text);
		
		pageTabIndicator = (TabPageIndicator) findViewById(R.id.tab_event_indicator);
		
		viewPager = (CustomViewPager) findViewById(R.id.tab_event_pager);
		
		selectedTabIndex = STARTING_TAB_INDEX;
	}
	
	
	
	private void setAdapter(int selectedIndex) 
	{
		pagerAdapter = new CompetitionTabFragmentStatePagerAdapter(getSupportFragmentManager(), viewPager, competition.getCompetitionId());
	
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setBoundaryCaching(true);
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
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, false, competition.getCompetitionId());
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
			event = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition();
	
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
