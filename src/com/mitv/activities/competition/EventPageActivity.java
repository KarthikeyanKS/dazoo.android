
package com.mitv.activities.competition;



import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.CompetitionEventPageBroadcastListAdapter;
import com.mitv.adapters.pager.CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter;
import com.mitv.adapters.pager.CompetitionEventHighlightsAndLineupTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcastDetails;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.CustomViewPager;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.viewpagerindicator.TabPageIndicator;



public class EventPageActivity 
	extends BaseContentActivity
	implements ViewCallbackListener, FetchDataProgressCallbackListener 
{
	private static final String TAG = EventPageActivity.class.getName();
	
	
	private static final int STARTING_TAB_INDEX = 0;
	

	private Event event;
	private Phase phase;
	private CompetitionEventPageBroadcastListAdapter listAdapter;
	
	private int selectedTabIndexForHighlightsAndLineup;
	private TabPageIndicator pageTabIndicatorForHighlightsAndLineup;
	public static CustomViewPager viewPagerForHighlightsAndLineup;
	private CompetitionEventHighlightsAndLineupTabFragmentStatePagerAdapter pagerAdapterForHighlightsAndLineup;
	
	private int selectedTabIndexForGroupAndStandings;
	private TabPageIndicator pageTabIndicatorForGroupAndStandings;
	public static CustomViewPager viewPagerForGroupAndStandings;
	private CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter pagerAdapterForGroupAndStandings;

	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	private TextView groupHeader;
	private TextView liveStandings;
	private TextView liveTimeInGame;
	private LinearLayout broadcastListView;
	private TextView likeIcon;
	private TextView shareIcon;
	private TextView beginTime;
	private TextView beginTimeDate;
	private TextView headerteamvsteam;
	private TextView headerCompetitionName;
	private String competitionName;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded())
		{
			return;
		}
		
		setContentView(R.layout.layout_competition_event_page);
		
		Intent intent = getIntent();
		
		long eventID = intent.getLongExtra(Constants.INTENT_COMPETITION_EVENT_ID, 0);
		
		competitionName = intent.getStringExtra(Constants.INTENT_COMPETITION_NAME);
		
		event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
				
		long phaseID = event.getPhaseId();
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(phaseID);
		
		initLayout();
		
		setAdapterForHighlightsAndLineup(selectedTabIndexForHighlightsAndLineup);
		
		setAdapterForGroupAndStandings(selectedTabIndexForGroupAndStandings);
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
				boolean containsBroadcastDetails = event.containsBroadcastDetails();
				
				if (containsBroadcastDetails) {
					setListView();
				}
				
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
		String homeTeamName = event.getHomeTeam();
			
		String awayTeamName = event.getAwayTeam();
		
		/* Headr img and text: Team1 va Team2 */
		StringBuilder sbHeader = new StringBuilder();
		sbHeader.append(homeTeamName)
			.append(" vs ")
			.append(awayTeamName);
		
		headerteamvsteam.setText(sbHeader.toString());
		headerCompetitionName.setText(competitionName);
		
		headerteamvsteam.setVisibility(View.VISIBLE);
		headerCompetitionName.setVisibility(View.VISIBLE);
		
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
		
		/* Group name */
		
		String groupHeaderName = phase.getPhase();
		
		groupHeader.setText(groupHeaderName);
		
		/* The event is ongoing */
		if (event.isOngoing() && !event.isPostponed()) {
			StringBuilder sb = new StringBuilder();
			sb.append(event.getAwayGoals())
				.append(" - ")
				.append(event.getHomeGoals());
			
			liveStandings.setText(sb.toString());
			
			String timeInGame = DateUtils.getMinutesInGameString(event.getEventDateCalendarLocal());
			liveTimeInGame.setText(timeInGame);
			
			liveStandings.setVisibility(View.VISIBLE);
			liveTimeInGame.setVisibility(View.VISIBLE);
			beginTime.setVisibility(View.GONE);
			beginTimeDate.setVisibility(View.GONE);
		}
		
		else if (event.isFinished()) {
			
		}
		
		/* The event has not started yet */
		else {
			StringBuilder sb = new StringBuilder();
			String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
			
			beginTime.setText(eventStartTimeHourAndMinuteAsString);
			
			sb.append(event.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(event.getEventTimeDayAndMonthAsString());
			
			beginTimeDate.setText(sb.toString());
			
			liveStandings.setVisibility(View.GONE);
			liveTimeInGame.setVisibility(View.GONE);
			beginTime.setVisibility(View.VISIBLE);
			beginTimeDate.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	private void initLayout()
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		StringBuilder eventName = new StringBuilder();
		eventName.append(event.getHomeTeam())
		.append(" : ")
		.append(event.getAwayTeam());
		
		actionBar.setTitle(eventName.toString());
		
		team1Name = (TextView) findViewById(R.id.competition_event_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_event_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_event_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_event_team_two_flag);
		groupHeader = (TextView) findViewById(R.id.competition_event_group_header);
		liveStandings = (TextView) findViewById(R.id.competition_event_live_standing);
		liveTimeInGame = (TextView) findViewById(R.id.competition_event_live_time);
		broadcastListView = (LinearLayout) findViewById(R.id.competition_event_broadcasts_listview);
//		likeIcon = (TextView) findViewById(R.id.competition_element_social_buttons_like_view);
//		shareIcon = (TextView) findViewById(R.id.competition_element_social_buttons_share_button_iv);
		beginTime = (TextView) findViewById(R.id.competition_event_starttime_time);
		beginTimeDate = (TextView) findViewById(R.id.competition_event_starttime_date);
		headerteamvsteam = (TextView) findViewById(R.id.competition_event_title_header);
		headerCompetitionName = (TextView) findViewById(R.id.competition_event_world_cup_header);
		
		pageTabIndicatorForHighlightsAndLineup = (TabPageIndicator) findViewById(R.id.tab_event_indicator_for_highlights_and_lineup);
		viewPagerForHighlightsAndLineup = (CustomViewPager) findViewById(R.id.tab_event_pager_for_highlights_and_lineup);
		selectedTabIndexForHighlightsAndLineup = STARTING_TAB_INDEX;
		
		pageTabIndicatorForGroupAndStandings = (TabPageIndicator) findViewById(R.id.tab_event_indicator_for_group_and_standings);
		viewPagerForGroupAndStandings = (CustomViewPager) findViewById(R.id.tab_event_pager_for_group_and_standings);
		selectedTabIndexForGroupAndStandings = STARTING_TAB_INDEX;
	}
	

	
	private void setAdapterForHighlightsAndLineup(int selectedIndex) 
	{
		pagerAdapterForHighlightsAndLineup = new CompetitionEventHighlightsAndLineupTabFragmentStatePagerAdapter(getSupportFragmentManager(), event.getEventId());
	
		viewPagerForHighlightsAndLineup.setAdapter(pagerAdapterForHighlightsAndLineup);
		viewPagerForHighlightsAndLineup.setOffscreenPageLimit(1);
		viewPagerForHighlightsAndLineup.setBoundaryCaching(true);
		viewPagerForHighlightsAndLineup.setCurrentItem(selectedIndex);
		viewPagerForHighlightsAndLineup.setVisibility(View.VISIBLE);
		viewPagerForHighlightsAndLineup.setEnabled(false);

		pagerAdapterForHighlightsAndLineup.notifyDataSetChanged();
		
		pageTabIndicatorForHighlightsAndLineup.setVisibility(View.VISIBLE);
		pageTabIndicatorForHighlightsAndLineup.setViewPager(viewPagerForHighlightsAndLineup);
		viewPagerForHighlightsAndLineup.setScreenHeight(GenericUtils.getScreenHeight(this));
		
		pagerAdapterForHighlightsAndLineup.notifyDataSetChanged();
		pageTabIndicatorForHighlightsAndLineup.setCurrentItem(selectedIndex);
		
		pageTabIndicatorForHighlightsAndLineup.setInitialStyleOnAllTabs();
		pageTabIndicatorForHighlightsAndLineup.setStyleOnTabViewAtIndex(selectedIndex);
	}
	
	
	
	private void setAdapterForGroupAndStandings(int selectedIndex) 
	{
		StringBuilder groupName = new StringBuilder();
		groupName.append(phase.getPhase());
		
		pagerAdapterForGroupAndStandings = new CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter(getSupportFragmentManager(), groupName.toString(), event.getEventId());
	
		viewPagerForGroupAndStandings.setAdapter(pagerAdapterForGroupAndStandings);
		viewPagerForGroupAndStandings.setOffscreenPageLimit(1);
		viewPagerForGroupAndStandings.setBoundaryCaching(true);
		viewPagerForGroupAndStandings.setCurrentItem(selectedIndex);
		viewPagerForGroupAndStandings.setVisibility(View.VISIBLE);
		viewPagerForGroupAndStandings.setEnabled(false);

		pagerAdapterForGroupAndStandings.notifyDataSetChanged();
		
		pageTabIndicatorForGroupAndStandings.setVisibility(View.VISIBLE);
		pageTabIndicatorForGroupAndStandings.setViewPager(viewPagerForGroupAndStandings);
		viewPagerForGroupAndStandings.setScreenHeight(GenericUtils.getScreenHeight(this));
		
		pagerAdapterForGroupAndStandings.notifyDataSetChanged();
		pageTabIndicatorForGroupAndStandings.setCurrentItem(selectedIndex);
		
		pageTabIndicatorForGroupAndStandings.setInitialStyleOnAllTabs();
		pageTabIndicatorForGroupAndStandings.setStyleOnTabViewAtIndex(selectedIndex);
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_event_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, false, event.getCompetitionId());
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = ContentManager.sharedInstance().getFromCacheHasEventData(event.getCompetitionId(), event.getEventId());
		
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
	
	
	
	private void setListView() 
	{
		broadcastListView.removeAllViews();
		
		List<EventBroadcastDetails> broadcastDetails = event.getEventBroadcastDetails();
		
		listAdapter = new CompetitionEventPageBroadcastListAdapter(this, broadcastDetails);
		
		for (int i = 0; i < listAdapter.getCount(); i++) 
		{
            View listItem = listAdapter.getView(i, null, broadcastListView);
           
            if (listItem != null) 
            {
            	broadcastListView.addView(listItem);
            }
        }
		
		broadcastListView.measure(0, 0);
	}



	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}
}
