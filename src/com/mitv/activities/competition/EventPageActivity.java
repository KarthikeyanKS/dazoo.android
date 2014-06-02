
package com.mitv.activities.competition;



import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.CompetitionEventEventsByGroupListAdapter;
import com.mitv.adapters.list.CompetitionEventHighlightsListAdapter;
import com.mitv.adapters.list.CompetitionEventPageBroadcastListAdapter;
import com.mitv.adapters.list.CompetitionEventStandingsListAdapter;
import com.mitv.adapters.pager.CompetitionEventLineupTeamsTabFragmentStatePagerAdapter;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.EventMatchStatusEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.comparators.EventBroadcastByStartTime;
import com.mitv.models.comparators.EventHighlightComparatorByTime;
import com.mitv.models.comparators.EventStandingsComparatorByPoints;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.CustomViewPager;
import com.mitv.ui.elements.LikeView;
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
	private List<Event> events;
	private Phase phase;
	private List<Standings> standings;
	private CompetitionEventPageBroadcastListAdapter listAdapter;
	
	private int selectedTabIndexForLineupTeams;
	private TabPageIndicator pageTabIndicatorForLineupTeams;
	private CustomViewPager viewPagerForLineupTeams;
	private CompetitionEventLineupTeamsTabFragmentStatePagerAdapter pagerAdapterForLineupTeams;

	private ImageView stadiumImage;
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	private TextView groupHeader;
	private TextView liveScore;
	private TextView liveStatus;
	private TextView stadiumName;
	private TextView stadiumImageCopyright;
	private TextView description;
	private LinearLayout broadcastListView;
	private LikeView likeView;
	private RelativeLayout shareContainer;
	private TextView beginTime;
	private TextView beginTimeDate;
	private TextView headerteamvsteam;
	private TextView headerCompetitionName;
	private String competitionName;
	private TextView headerStandings;
	private TextView headerGroups;
	
	private RelativeLayout highlightsContainerLayout;
	private RelativeLayout lineupContainerLayout;
	
	private RelativeLayout highlightsFlagAndNameContainerOne;
	private RelativeLayout highlightsFlagAndNameContainerTwo;
	
	private LinearLayout listContainerLayoutHighlights;
	private CompetitionEventHighlightsListAdapter listAdapterHighlights;
	private TextView team1NameHighlights;
	private ImageView team1FlagHighlights;
	private TextView team2NameHighlights;
	private ImageView team2FlagHighlights;
	
	private LinearLayout groupListContainer;
	private LinearLayout standingsListContainer;
	private CompetitionEventEventsByGroupListAdapter groupListAdapter;
	private CompetitionEventStandingsListAdapter standingsListAdapter;
	
	
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
		
		phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(phaseID);
		
		events = ContentManager.sharedInstance().getFromCacheEventsForPhaseInSelectedCompetition(phase.getPhaseId());
		
		standings = ContentManager.sharedInstance().getFromCacheStandingsForPhaseInSelectedCompetition(phase.getPhaseId());
		
		initLayout();
		
		setAdapterForLineupTeams(selectedTabIndexForLineupTeams);
		
		setAdapterForStandingsList();
		
		setAdapterForGroupList();
	}
		
	
	
	@Override
	protected void onResume() 
	{
		updateStatusOfLikeView();
		
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
				
				if (containsBroadcastDetails)
				{
					setListView();
				}
				
				setData();
				
				setAdapterForHighlights();
				
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
		
		StringBuilder sbHeader = new StringBuilder();
		sbHeader.append(homeTeamName)
			.append(" ")	
			.append(getString(R.string.event_page_versus))
			.append(" ")
			.append(awayTeamName);
		
		headerteamvsteam.setText(sbHeader.toString());
		headerCompetitionName.setText(competitionName);
		
		headerteamvsteam.setVisibility(View.VISIBLE);
		headerCompetitionName.setVisibility(View.VISIBLE);
		
		StringBuilder sbHeaderGroup = new StringBuilder();
		sbHeaderGroup.append(getResources().getString(R.string.event_page_header_standings))
			.append(" ")
			.append(phase.getPhase());
		
		headerStandings.setText(sbHeaderGroup.toString());
				
		ImageAware imageAwareStadium = new ImageViewAware(stadiumImage, false);
		
		String stadiumImageURL = event.getStadiumImageURL();
		
		SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionEventStadiumOptions(stadiumImageURL, imageAwareStadium);
		
		boolean containsTeamInfo = event.containsTeamInfo();
			
		if(containsTeamInfo)
		{
			final long team1ID = event.getHomeTeamId();
			
			Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
			
			if(team1 != null)
			{
				Log.w(TAG, "Local flag for team: " + team1.getNationCode() + " not found in cache");
					
				ImageAware imageAware = new ImageViewAware(team1Flag, false);
				ImageAware imageAwareHighlights = new ImageViewAware(team1FlagHighlights, false);
					
				String team1FlagUrl = team1.getFlagImageURL();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAwareHighlights);
				
				team1Flag.setOnClickListener(new View.OnClickListener() 
		        {
		            public void onClick(View v)
		            {
		                Intent intent = new Intent(EventPageActivity.this, TeamPageActivity.class);
		                intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
		                intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, team1ID);
		                intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, event.getPhaseId());
		                
		                startActivity(intent);
		            }
		        });

				highlightsFlagAndNameContainerOne.setOnClickListener(new View.OnClickListener() 
		        {
		            public void onClick(View v)
		            {
		                Intent intent = new Intent(EventPageActivity.this, TeamPageActivity.class);
		                intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
		                intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, team1ID);
		                intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, event.getPhaseId());
		                
		                startActivity(intent);
		            }
		        });
			}
			
			final long team2ID = event.getAwayTeamId();
			
			Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);

			if(team2 != null)
			{
				ImageAware imageAware = new ImageViewAware(team2Flag, false);
				ImageAware imageAwareHighlights = new ImageViewAware(team2FlagHighlights, false);
					
				String team2FlagUrl = team2.getFlagImageURL();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAwareHighlights);
				
				team2Flag.setOnClickListener(new View.OnClickListener() 
		        {
		            public void onClick(View v)
		            {
		                Intent intent = new Intent(EventPageActivity.this, TeamPageActivity.class);
		                intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
		                intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, team2ID);
		                intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, event.getPhaseId());
		                
		                startActivity(intent);
		            }
		        });
				
				highlightsFlagAndNameContainerTwo.setOnClickListener(new View.OnClickListener() 
		        {
		            public void onClick(View v)
		            {
		                Intent intent = new Intent(EventPageActivity.this, TeamPageActivity.class);
		                
		                intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
		                intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, team2ID);
		                intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, event.getPhaseId());
		                
		                startActivity(intent);
		            }
		        });
			}
		}
		
		team1Name.setText(homeTeamName);
		team1NameHighlights.setText(homeTeamName);
		
		team2Name.setText(awayTeamName);
		team2NameHighlights.setText(awayTeamName);
		
		/* Group name */
		
		String groupHeaderName = phase.getPhase();
		
		groupHeader.setText(groupHeaderName);
		
		EventMatchStatusEnum matchStatus = event.getMatchStatus();
		
		switch(matchStatus)
		{
			case LINE_UP:
			case NOT_STARTED_AND_LINE_UP:
			case DELAYED:
			{
				lineupContainerLayout.setVisibility(View.VISIBLE);
				highlightsContainerLayout.setVisibility(View.GONE);
				break;
			}
			
			case NOT_STARTED:
			case POSTPONED:
			{
				lineupContainerLayout.setVisibility(View.GONE);
				highlightsContainerLayout.setVisibility(View.GONE);
				break;
			}
			
			default:
			{
				lineupContainerLayout.setVisibility(View.VISIBLE);
				highlightsContainerLayout.setVisibility(View.VISIBLE);
				break;
			}
		}
		
		String stadium = event.getStadium();
		if (stadium != null && !stadium.isEmpty()) {
			stadiumName.setText(stadium);
			stadiumName.setVisibility(View.VISIBLE);
		}
		
		String copyright = event.getStadiumImageCopyright();
		if (copyright != null && !copyright.isEmpty()) {
			stadiumImageCopyright.setText(copyright);
			stadiumImageCopyright.setVisibility(View.VISIBLE);
		}
		
		String descriptionText = event.getDescription();
		if (descriptionText != null && !descriptionText.isEmpty()) {
			description.setText(descriptionText);
			description.setVisibility(View.VISIBLE);
		}
		
		boolean isOngoing = event.hasStarted();
		boolean isFinished = event.isFinished();

		/* The event is ongoing or finished */
		if (isOngoing || isFinished)
		{
			String score = event.getScoreAsString();
			
			liveScore.setText(score);
			
			String timeInGame = event.getGameTimeAndStatusAsString(true);
			
			liveStatus.setText(timeInGame);
			
			liveScore.setVisibility(View.VISIBLE);
			liveStatus.setVisibility(View.VISIBLE);
			beginTime.setVisibility(View.GONE);
			beginTimeDate.setVisibility(View.GONE);
		}
		
		/* The event has not started yet */
		else 
		{
			StringBuilder sb = new StringBuilder();
			String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
			
			beginTime.setText(eventStartTimeHourAndMinuteAsString);
			
			sb.append(event.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(event.getEventTimeDayAndMonthAsString());
			
			beginTimeDate.setText(sb.toString());
			
			liveScore.setVisibility(View.GONE);
			liveStatus.setVisibility(View.GONE);
			beginTime.setVisibility(View.VISIBLE);
			beginTimeDate.setVisibility(View.VISIBLE);
		}
		
		long competitionID = event.getCompetitionId();
		
		Competition competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);
		
		if(competition != null)
		{
			likeView.setUserLike(competition);
		}
		else
		{
			Log.w(TAG, "Competition was not found. User like will not be set");
		}
		
		/* Share event */
		shareContainer.setTag(event);
		shareContainer.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) 
	{
		/* Important to call super, else tabs wont work */
		super.onClick(v);

		int viewId = v.getId();

		Event shareEvent = (Event) v.getTag();

		switch (viewId) 
		{
			case R.id.competition_element_social_buttons_share_button_container: 
			{
				GenericUtils.startShareActivity(this, shareEvent);
				break;
			}
			default: 
			{
				Log.w(TAG, "Unhandled onClick action");
				break;
			}
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
		
		stadiumImage = (ImageView) findViewById(R.id.competition_event_stadium_image);
		team1Name = (TextView) findViewById(R.id.competition_event_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_event_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_event_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_event_team_two_flag);
		groupHeader = (TextView) findViewById(R.id.competition_event_group_header);
		liveScore = (TextView) findViewById(R.id.competition_event_live_score);
		liveStatus = (TextView) findViewById(R.id.competition_event_live_status);
		broadcastListView = (LinearLayout) findViewById(R.id.competition_event_broadcasts_listview);
		likeView = (LikeView) findViewById(R.id.competition_element_social_buttons_like_view);
		shareContainer = (RelativeLayout) findViewById(R.id.competition_element_social_buttons_share_button_container);
		beginTime = (TextView) findViewById(R.id.competition_event_starttime_time);
		beginTimeDate = (TextView) findViewById(R.id.competition_event_starttime_date);
		headerteamvsteam = (TextView) findViewById(R.id.competition_event_title_header);
		headerCompetitionName = (TextView) findViewById(R.id.competition_event_world_cup_header);
		headerStandings = (TextView) findViewById(R.id.competition_standings_header);
		headerGroups = (TextView) findViewById(R.id.competition_group_header_group);
		
		stadiumName = (TextView) findViewById(R.id.competition_event_stadium_name);
		stadiumImageCopyright = (TextView) findViewById(R.id.competition_event_stadium_photo_credits);
		description = (TextView) findViewById(R.id.competition_event_description);
		
		highlightsContainerLayout = (RelativeLayout) findViewById(R.id.competition_event_block_tabs_highlights_teams_container);
		team1NameHighlights = (TextView) findViewById(R.id.competition_event_highlights_team_one_name);
		team1FlagHighlights = (ImageView) findViewById(R.id.competition_event_highlights_team_one_flag);
		team2NameHighlights = (TextView) findViewById(R.id.competition_event_highlights_team_two_name);
		team2FlagHighlights = (ImageView) findViewById(R.id.competition_event_highlights_team_two_flag);
		listContainerLayoutHighlights = (LinearLayout) findViewById(R.id.competition_event_highlights_table_container);
		
		lineupContainerLayout = (RelativeLayout) findViewById(R.id.competition_event_block_tabs_lineup_teams_container);
		pageTabIndicatorForLineupTeams = (TabPageIndicator) findViewById(R.id.tab_event_indicator_for_lineup_teams);
		viewPagerForLineupTeams = (CustomViewPager) findViewById(R.id.tab_event_pager_for_lineup_teams);
		selectedTabIndexForLineupTeams = STARTING_TAB_INDEX;
		
		groupListContainer = (LinearLayout) findViewById(R.id.competition_event_group_list);
		standingsListContainer = (LinearLayout) findViewById(R.id.competition_event_standings_list);
		
		highlightsFlagAndNameContainerOne = (RelativeLayout) findViewById(R.id.competition_event_highlights_team_one_flag_container);
		highlightsFlagAndNameContainerTwo = (RelativeLayout) findViewById(R.id.competition_event_highlights_team_two_flag_container);
	}
	

	
	private void setAdapterForHighlights() 
	{
		long eventID = event.getEventId();
		
		if (ContentManager.sharedInstance().getFromCacheHasHighlightsDataByEventIDForSelectedCompetition(eventID))
		{
			List<EventHighlight> eventHighlights = ContentManager.sharedInstance().getFromCacheHighlightsDataByEventIDForSelectedCompetition(eventID, Constants.EVENT_HIGHLIGHT_ACTIONS_TO_EXCLUDE);
			
			listContainerLayoutHighlights.removeAllViews();

			Collections.sort(eventHighlights, new EventHighlightComparatorByTime());
			Collections.reverse(eventHighlights);
			
			listAdapterHighlights = new CompetitionEventHighlightsListAdapter(this, eventHighlights);
			
			for (int i = 0; i < listAdapterHighlights.getCount(); i++)
			{
	            View listItem = listAdapterHighlights.getView(i, null, listContainerLayoutHighlights);
	           
	            if (listItem != null) 
	            {
	            	listContainerLayoutHighlights.addView(listItem);
	            }
	        }
			
			listContainerLayoutHighlights.measure(0, 0);
		}
	}
	
	
	
	private void setAdapterForLineupTeams(int selectedIndex) 
	{
		pagerAdapterForLineupTeams = new CompetitionEventLineupTeamsTabFragmentStatePagerAdapter(
				getSupportFragmentManager(),
				viewPagerForLineupTeams,
				event.getEventId(),
				event.getHomeTeam(),
				event.getHomeTeamId(),
				event.getAwayTeam(),
				event.getAwayTeamId());
	
		viewPagerForLineupTeams.setAdapter(pagerAdapterForLineupTeams);
		viewPagerForLineupTeams.setOffscreenPageLimit(1);
//		viewPagerForLineupTeams.setBoundaryCaching(true);
		viewPagerForLineupTeams.setCurrentItem(selectedIndex);
		viewPagerForLineupTeams.setVisibility(View.VISIBLE);
		viewPagerForLineupTeams.setEnabled(false);

		pagerAdapterForLineupTeams.notifyDataSetChanged();
		
		pageTabIndicatorForLineupTeams.setVisibility(View.VISIBLE);
		pageTabIndicatorForLineupTeams.setViewPager(viewPagerForLineupTeams);
		viewPagerForLineupTeams.setScreenHeight(GenericUtils.getScreenHeight(this));
		
		pagerAdapterForLineupTeams.notifyDataSetChanged();
		pageTabIndicatorForLineupTeams.setCurrentItem(selectedIndex);
		
		pageTabIndicatorForLineupTeams.setInitialStyleOnAllTabs();
		pageTabIndicatorForLineupTeams.setStyleOnTabViewAtIndex(selectedIndex);
	}
	
	
	
	/* Schedule for group */
	private void setAdapterForGroupList() 
	{
		StringBuilder header = new StringBuilder(); 
		header.append(this.getResources().getString(R.string.team_page_squad_schedule_header))
			.append(" ")
			.append(phase.getPhase());
		
		headerGroups.setText(header.toString());
		
		groupListContainer.removeAllViews();

		String viewBottomMessage = getString(R.string.event_page_groups_list_show_more);
		
		Runnable procedure = getNavigateToCompetitionPageProcedure(CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION);
		
		groupListAdapter = new CompetitionEventEventsByGroupListAdapter(this, events, true, viewBottomMessage, procedure);
		
		for (int i = 0; i < groupListAdapter.getCount(); i++) 
		{
            View listItem = groupListAdapter.getView(i, null, groupListContainer);
           
            if (listItem != null) 
            {
            	groupListContainer.addView(listItem);
            }
        }
		
		groupListContainer.measure(0, 0);
	}
	
	
	
	/* Standings for group */
	private void setAdapterForStandingsList() 
	{
		StringBuilder header = new StringBuilder(); 
		
		header.append(this.getResources().getString(R.string.event_page_header_standings))
			.append(" ")
			.append(phase.getPhase());
		
		headerStandings.setText(header.toString());
		
		standingsListContainer.removeAllViews();
		
		Collections.sort(standings, new EventStandingsComparatorByPoints());
		
		Collections.reverse(standings);
		
		String viewBottomMessage = getString(R.string.event_page_standings_list_show_more);
		
		Runnable procedure = getNavigateToCompetitionPageProcedure(CompetitionTabFragmentStatePagerAdapter.TEAM_STANDINGS_POSITION);
		
		standingsListAdapter = new CompetitionEventStandingsListAdapter(this, standings, true, viewBottomMessage, procedure);
		
		for (int i = 0; i < standingsListAdapter.getCount(); i++) 
		{
            View listItem = standingsListAdapter.getView(i, null, standingsListContainer);
           
            if (listItem != null) 
            {
            	standingsListContainer.addView(listItem);
            }
        }
		
		standingsListContainer.measure(0, 0);
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_event_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		/* Always re-fetch the data from the service */
		boolean forceRefreshOfHighlights = false;
		
		if (Constants.USE_COMPETITION_FORCE_DOWNLOAD_ALL_TIMES) {
			forceRefreshOfHighlights = true;
		}
		
		ContentManager.sharedInstance().getElseFetchFromServiceEventHighlighstData(this, forceRefreshOfHighlights, event.getCompetitionId(), event.getEventId());
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
		switch (requestIdentifier) 
		{
			case COMPETITION_EVENT_HIGHLIGHTS:
			{
				if(fetchRequestResult.wasSuccessful())
				{
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
				break;
			}
	
			case USER_ADD_LIKE:
			{
				updateStatusOfLikeView();
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unknown request identifier");
				break;
			}
		}
	}
	
	
	
	private void setListView() 
	{
		broadcastListView.removeAllViews();
		
		List<EventBroadcast> broadcasts = event.getEventBroadcasts();
		
		Collections.sort(broadcasts, new EventBroadcastByStartTime());
		
		listAdapter = new CompetitionEventPageBroadcastListAdapter(this, broadcasts);
		
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
	
	
	
	private void updateStatusOfLikeView() 
	{
		if (likeView != null) 
		{
			likeView.updateImage();
		}
	}
	
	
	
	private Runnable getNavigateToCompetitionPageProcedure(final int tabToNavigateTo)
	{
		return new Runnable() 
		{
			public void run() 
			{
				Intent intent = new Intent(EventPageActivity.this, CompetitionPageActivity.class);
				
				intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
                intent.putExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, tabToNavigateTo);
                
				startActivity(intent);
			}
		};
	}



	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}
}
