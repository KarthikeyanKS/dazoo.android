
package com.mitv.activities.competition;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.CompetitionPageTodayListAdapter;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
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
	implements ViewCallbackListener, FetchDataProgressCallbackListener, OnPageChangeListener
{
	private static final String TAG = CompetitionPageActivity.class.getName();
	
	
	private Competition competition;
	private Event event;
	
	private TabPageIndicator pageTabIndicator;
	private CustomViewPager viewPager;
	private CompetitionTabFragmentStatePagerAdapter pagerAdapter;
	private int selectedTabIndex;
	
	private LinearLayout beforeLayout;
	private RelativeLayout countDownLayout;
	private TextView remainingTimeInDays;
	private TextView remainingTimeInDaysTitle;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInHoursTitle;
	private TextView remainingTimeInMinutes;
	private TextView remainingTimeInMinutesTitle;
	private TextView countdownTitle;
	private TextView nextGameText;
	private TextView eventStartTime;
	private TextView tvBroadcastChannels;
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	private RelativeLayout ongoingLayout;
	
	private LinearLayout todaysLiveAndUpcomingList;
	private CompetitionPageTodayListAdapter listAdapter;
	
	private EventCountDownTimer eventCountDownTimer;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_competition_page);
		
		Intent intent = getIntent();
		
		long competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);

		this.selectedTabIndex = intent.getIntExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, 0);
		
		this.competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
		
		ContentManager.sharedInstance().getCacheManager().setSelectedCompetition(competition);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		initLayout();
		
		setAdapter(selectedTabIndex);
		
		int reloadIntervalInSecond = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionPageReloadInterval();
		
		setBackgroundLoadTimerValueInSeconds(reloadIntervalInSecond);
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
			
			setOngoingLayoutForEventsToday();
		}
		
		/* Has ended */
		else if (hasEnded)
		{
			countDownLayout.setVisibility(View.GONE);
			beforeLayout.setVisibility(View.GONE);
			todaysLiveAndUpcomingList.setVisibility(View.GONE);
		}
		
		/* Not yet started */
		else 
		{
			beforeLayout.setVisibility(View.VISIBLE);
			countDownLayout.setVisibility(View.VISIBLE);
			todaysLiveAndUpcomingList.setVisibility(View.GONE);
			
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
					remainingTimeInMinutesTitle,
					countdownTitle,
					countDownLayout);
			
			eventCountDownTimer.start();
			
			countDownLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
	            	TrackingGAManager.sharedInstance().sendUserCompetitionCountdownPressedEvent(competition.getDisplayName());
				}
			});
			
			setBeforeLayout();
		}		
	}
	
	
	
	private void setOngoingLayoutForEventsToday() 
	{
		List<Event> events = ContentManager.sharedInstance().getCacheManager().getAllLiveEventsForSelectedCompetition();
		
		/* ONLY FOR TESTING: If no live events we want to show the next upcoming instead */
//		if (events.isEmpty()) {
//			boolean filterFinishedEvents = true;
//			boolean filterLiveEvents = true;
//			
//			Event event = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition(filterFinishedEvents, filterLiveEvents);
//			
//			events.add(event);
//			events.add(event);
//		}
		
		todaysLiveAndUpcomingList.removeAllViews();

		listAdapter = new CompetitionPageTodayListAdapter(this, events, competition.getDisplayName());
		
		for (int i = 0; i < listAdapter.getCount(); i++) 
		{
            View listItem = listAdapter.getView(i, null, todaysLiveAndUpcomingList);
           
            if (listItem != null) 
            {
            	todaysLiveAndUpcomingList.addView(listItem);
            }
        }
		
		todaysLiveAndUpcomingList.measure(0, 0);

		todaysLiveAndUpcomingList.setVisibility(View.VISIBLE);
		
		ongoingLayout.setBackground(getResources().getDrawable(R.drawable.competition_backdrop_mundial));
	}
	
	
	
	private void setBeforeLayout() {
		String homeTeamName = event.getHomeTeam();
		
		String awayTeamName = event.getAwayTeam();
			
		boolean containsTeamInfo = event.containsTeamInfo();
			
		if(containsTeamInfo)
		{
			long team1ID = event.getHomeTeamId();
			
			Team team1 = ContentManager.sharedInstance().getCacheManager().getTeamById(team1ID);
			
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
			
			Team team2 = ContentManager.sharedInstance().getCacheManager().getTeamById(team2ID);

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
		countdownTitle = (TextView) findViewById(R.id.competition_title);
		
		/* Before */
		beforeLayout = (LinearLayout) findViewById(R.id.competition_page_before_block_container_layout);
		eventStartTime = (TextView) findViewById(R.id.competition_page_begin_time_broadcast);
		tvBroadcastChannels = (TextView) findViewById(R.id.competition_airing_channels_for_broadcast);
		team1Name = (TextView) findViewById(R.id.competition_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_team_two_flag);
		
		/* Ongoing */
		ongoingLayout = (RelativeLayout) findViewById(R.id.competition_scrollable_layout);
		
		/* List of ongoing and upcoming events for today */
		todaysLiveAndUpcomingList = (LinearLayout) findViewById(R.id.todays_ongoing_events_list);
		
		pageTabIndicator = (TabPageIndicator) findViewById(R.id.tab_event_indicator);
		
		viewPager = (CustomViewPager) findViewById(R.id.tab_event_pager);
		
		StickyScrollView scrollView = (StickyScrollView) findViewById(R.id.competition_scrollview);
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
		pageTabIndicator.setOnPageChangeListener(this);
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);

		int reloadIntervalInMinutes = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionPageReloadInterval();
		
		boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadIntervalInMinutes);

		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, forceRefresh, competition.getCompetitionId());
	}
	

	
	@Override
	protected void loadDataInBackground()
	{
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, true, competition.getCompetitionId());
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = false;
		if (competition != null) {
			hasData = ContentManager.sharedInstance().getCacheManager().containsCompetitionData(competition.getCompetitionId());
		}
		return hasData;
	}



	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			boolean filterFinishedEvents = false;
			boolean filterLiveEvents = false;
			event = ContentManager.sharedInstance().getCacheManager().getNextUpcomingEventForSelectedCompetition(filterFinishedEvents, filterLiveEvents);
	
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



	@Override
	public void onPageScrollStateChanged(int arg0) {}



	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}



	@Override
	public void onPageSelected(int pos) {
		selectedTabIndex = pos;
		TrackingGAManager.sharedInstance().sendUserCompetitionTabPressedEvent(competition.getDisplayName(), pagerAdapter.getPageTitle(pos).toString());
	}
}
