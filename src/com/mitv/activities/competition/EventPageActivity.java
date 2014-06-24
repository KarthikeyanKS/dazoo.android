
package com.mitv.activities.competition;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
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
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.EventBroadcastByStartTime;
import com.mitv.models.comparators.EventStandingsComparatorByPointsAndGoalDifference;
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

	
	private List<Event> events;
	private long eventID;
	private long competitionID;
	private Phase phase;
	private List<Standings> standings;
	private CompetitionEventPageBroadcastListAdapter listAdapter;

	private int selectedTabIndexForLineupTeams;
	private TabPageIndicator pageTabIndicatorForLineupTeams;
	private CustomViewPager viewPagerForLineupTeams;
	private CompetitionEventLineupTeamsTabFragmentStatePagerAdapter pagerAdapterForLineupTeams;

	private RelativeLayout eventPageContainer;
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	private TextView groupHeader;
	private TextView liveScore;
	private TextView stadiumName;
	private TextView stadiumImageCopyright;
	private TextView description;
	private LinearLayout broadcastListView;
	private LikeView likeView;
	private RelativeLayout shareContainer;
	private TextView beginTime;
	private TextView headerteamvsteam;
	private TextView headerCompetitionName;
	private TextView headerStandings;
	private TextView headerGroups;

	private RelativeLayout highlightsContainerLayout;
	private LinearLayout lineupContainerLayout;

	private LinearLayout listContainerLayoutHighlights;
	private CompetitionEventHighlightsListAdapter listAdapterHighlights;

	private RelativeLayout highlightsReloadRelativeLayout;
	private TextView highlightsReloadText;
	private TextView highlightsReloadIcon;
	private ProgressBar highlightsProgressLoading;
	
	
	private LinearLayout groupListContainer;
	private LinearLayout standingsListContainer;
	private LinearLayout standingsListBlock;
	private CompetitionEventEventsByGroupListAdapter groupListAdapter;
	private CompetitionEventStandingsListAdapter standingsListAdapter;
	private StickyScrollView scrollView;
	private RelativeLayout container;


	/* Timer for re-fetching data in the background while the user is on the same activity */
	private Timer backgroundLoadTimerForHighlights;



	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		boolean isFromNotification = getIntent().getBooleanExtra(Constants.INTENT_NOTIFICATION_EXTRA_IS_FROM_NOTIFICATION, false);
		
		if (isFromNotification == false && isRestartNeeded()) 
		{
			return;
		}

		setContentView(R.layout.layout_competition_event_page);

		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_EVENT_HIGHLIGHTS);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_EVENT_BY_ID);
		
		Intent intent = getIntent();

		competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);

		eventID = intent.getLongExtra(Constants.INTENT_COMPETITION_EVENT_ID, 0);
		
		Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);

		Event event = ContentManager.sharedInstance().getCacheManager().getEventById(competitionID, eventID);

		initLayout();
		
		if (competition == null || event == null) 
		{
			registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		}
		
		if (event == null) 
		{
			registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_STANDINGS_BY_PHASE_ID);
		}
		else
		{
			boolean isEventLive = event.isLive();

			if(isEventLive)
			{
				int reloadIntervalInSeconds = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionEventPageHighlightReloadInterval();

				setBackgroundLoadingTimerForHighlights(reloadIntervalInSeconds);
			}
		}

		int reloadIntervalInMinutes = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionEventPageReloadInterval();

		setBackgroundLoadTimerValueInSeconds(reloadIntervalInMinutes);

		setAllAdapters();
		
		if (isFromNotification) 
		{
			String eventTitle = null;
			if (event != null) 
			{
				eventTitle = event.getTitle();
			}
			else 
			{
				eventTitle = String.valueOf(eventID);
			}
			
			String competitionName = null;
			if (competition != null) 
			{
				competitionName = competition.getDisplayName();
			} 
			else 
			{
				competitionName = String.valueOf(competitionID);
			}

			TrackingGAManager.sharedInstance().sendUserOpenedEventpageFromReminder(competitionName, eventTitle);
		}
	}



	@Override
	protected void onResume() 
	{
		updateStatusOfLikeView();

		super.onResume();
	}



	@Override
	protected void onPause() 
	{
		super.onPause();

		if(backgroundLoadTimerForHighlights != null)
		{
			backgroundLoadTimerForHighlights.cancel();
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
				Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
				
				Event event = ContentManager.sharedInstance().getCacheManager().getEventByIDForSelectedCompetition(eventID);
	
				if(competition != null && event != null)
				{
					setAllAdapters();
		
					boolean containsBroadcastDetails = event.containsBroadcastDetails();
		
					if (containsBroadcastDetails)
					{
						setListView(event);
					}
		
					boolean isEventLive = event.isLive();
		
					if(isEventLive)
					{
						int reloadIntervalInSeconds = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionEventPageHighlightReloadInterval();
		
						setBackgroundLoadingTimerForHighlights(reloadIntervalInSeconds);
					}
		
					setData(competition, event);
		
					if (ContentManager.sharedInstance().getCacheManager().containsHighlightsDataByEventIDForSelectedCompetition(eventID)) 
					{
						setAdapterForHighlights();
					}
				}
	
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}



	private void setAllAdapters() 
	{
		String homeTeam;
		long homeTeamID;
		String awayTeam;
		long awayTeamID;
		String phaseString;

		Event event = ContentManager.sharedInstance().getCacheManager().getEventByIDForSelectedCompetition(eventID);
		
		if (event == null) 
		{
			homeTeam = "";
			homeTeamID = 0l;
			awayTeam = "";
			awayTeamID = 0l;
			phaseString = "";

			standings = new ArrayList<Standings>();

			events = new ArrayList<Event>();
		} 
		else 
		{
			phase = ContentManager.sharedInstance().getCacheManager().getPhaseByIDForSelectedCompetition(event.getPhaseId());

			events = ContentManager.sharedInstance().getCacheManager().getEventsForPhaseInSelectedCompetition(phase.getPhaseId());

			if (ContentManager.sharedInstance().getCacheManager().containsStandingsForPhaseInSelectedCompetition(phase.getPhaseId())) 
			{
				standings = ContentManager.sharedInstance().getCacheManager().getStandingsForPhaseInSelectedCompetition(phase.getPhaseId());
			}

			homeTeam = event.getHomeTeam();
			homeTeamID = event.getHomeTeamId();
			awayTeam = event.getAwayTeam();
			awayTeamID = event.getAwayTeamId();
			phaseString = phase.getPhase();
		}

		setAdapterForLineupTeams(selectedTabIndexForLineupTeams, eventID, homeTeam, homeTeamID, awayTeam, awayTeamID);

		setAdapterForStandingsList(standings, phaseString);

		setAdapterForGroupList(events, phaseString);
	}



	private void setData(
			final Competition competition,
			final Event event)
	{
		actionBar.setDisplayHomeAsUpEnabled(true);

		StringBuilder eventName = new StringBuilder();
		eventName.append(event.getHomeTeam())
		.append(" : ")
		.append(event.getAwayTeam());

		actionBar.setTitle(eventName.toString());

		final String homeTeamName = event.getHomeTeam();

		final String awayTeamName = event.getAwayTeam();

		StringBuilder sbHeader = new StringBuilder();
		sbHeader.append(homeTeamName)
		.append(" ")	
		.append(getString(R.string.event_page_versus))
		.append(" ")
		.append(awayTeamName);

		final String competitionName = competition.getDisplayName();
		
		headerteamvsteam.setText(sbHeader.toString());
		headerCompetitionName.setText(competitionName);

		headerteamvsteam.setVisibility(View.VISIBLE);
		headerCompetitionName.setVisibility(View.VISIBLE);

		StringBuilder sbHeaderGroup = new StringBuilder();
		sbHeaderGroup.append(getResources().getString(R.string.event_page_header_standings))
		.append(" ")
		.append(phase.getPhase());

		headerStandings.setText(sbHeaderGroup.toString());

		boolean containsTeamInfo = event.containsTeamInfo();

		if(containsTeamInfo)
		{
			final long team1ID = event.getHomeTeamId();

			Team team1 = ContentManager.sharedInstance().getCacheManager().getTeamById(team1ID);

			if(team1 != null)
			{
				ImageAware imageAware = new ImageViewAware(team1Flag, false);

				String team1FlagUrl = team1.getFlagImageURL();

				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);

				team1Flag.setOnClickListener(new View.OnClickListener() 
				{
					public void onClick(View v)
					{
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, homeTeamName, "Country");
						Intent intent = new Intent(EventPageActivity.this, TeamPageActivity.class);
						intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
						intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, team1ID);
						intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, event.getPhaseId());

						startActivity(intent);
					}
				});
			}

			final long team2ID = event.getAwayTeamId();

			Team team2 = ContentManager.sharedInstance().getCacheManager().getTeamById(team2ID);

			if(team2 != null)
			{
				ImageAware imageAware = new ImageViewAware(team2Flag, false);
				
				String team2FlagUrl = team2.getFlagImageURL();

				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);

				team2Flag.setOnClickListener(new View.OnClickListener() 
				{
					public void onClick(View v)
					{
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, awayTeamName, "Country");
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

		team2Name.setText(awayTeamName);

		/* Group name */

		String groupHeaderName = phase.getPhase();

		groupHeader.setText(groupHeaderName);

		String stadium = event.getStadium();
		
		if (stadium != null && !stadium.isEmpty()) 
		{
			stadiumName.setText(stadium);
			stadiumName.setVisibility(View.VISIBLE);
		}

		String copyright = event.getStadiumImageCopyright();
		
		if (copyright != null && !copyright.isEmpty()) 
		{
			StringBuilder sb = new StringBuilder();
				
			sb.append(this.getResources().getString(R.string.team_page_team_photo_from_header))
				.append(" ")
				.append(copyright);
				
			stadiumImageCopyright.setText(sb);
			stadiumImageCopyright.setVisibility(View.VISIBLE);
		}

		String descriptionText = event.getDescription();
		
		if (descriptionText != null && !descriptionText.isEmpty()) 
		{
			description.setText(descriptionText);
			description.setVisibility(View.VISIBLE);
		}

		EventMatchStatusEnum matchStatus = event.getMatchStatus();

		/** Setting the visibility of the Highlights **/
		switch(matchStatus)
		{
			case LINE_UP:
			case NOT_STARTED_AND_LINE_UP:
			case DELAYED:
			{
				highlightsContainerLayout.setVisibility(View.GONE);
				hideHighlightsReloadButton();
				break;
			}
	
			case NOT_STARTED:
			case POSTPONED:
			{
				highlightsContainerLayout.setVisibility(View.GONE);
				hideHighlightsReloadButton();
				break;
			}
	
			case UNOFFICIAL_RESULT:
			case NO_LIVE_UPDATES:
			case SUSPENDED:
			case ABANDONED:
			case FINISHED:
			{
				highlightsContainerLayout.setVisibility(View.VISIBLE);
				hideHighlightsReloadButton();
				break;
			}
				
			case INTERVAL:	
			case IN_PROGRESS:
			default:
			{
				highlightsContainerLayout.setVisibility(View.VISIBLE);
				showHighlightsReloadButton();
				break;
			}
		}
		
		/* Setting the visibility of the LineUp */
		if(event.isLineupAvailable())
		{
			lineupContainerLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			lineupContainerLayout.setVisibility(View.GONE);
		}
		
		/** Setting the visibility and values for the date, score and status **/
		switch(matchStatus)
		{
			case LINE_UP:
			case NOT_STARTED_AND_LINE_UP:
			case NOT_STARTED:
			{
				String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());

				liveScore.setText(eventStartTimeHourAndMinuteAsString);
				liveScore.setVisibility(View.VISIBLE);
				
				StringBuilder sb = new StringBuilder();
				sb.append(event.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(event.getEventTimeDayAndMonthAsString());

				beginTime.setText(sb.toString());
				beginTime.setVisibility(View.VISIBLE);
				
				break;
			}
			
			case DELAYED:
			{
				String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
				
				StringBuilder eventStartTimeHourAndMinuteSB = new StringBuilder();
				eventStartTimeHourAndMinuteSB.append(eventStartTimeHourAndMinuteAsString)
				.append(" (")
				.append(getString(R.string.event_page_delayed))
				.append(")");

				liveScore.setText(eventStartTimeHourAndMinuteAsString);
				liveScore.setVisibility(View.VISIBLE);
				
				StringBuilder sb = new StringBuilder();
				sb.append(event.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(event.getEventTimeDayAndMonthAsString());

				beginTime.setText(sb.toString());
				beginTime.setVisibility(View.VISIBLE);
				
				break;
			}
	
			case POSTPONED:
			case IN_PROGRESS:
			{
				String score = event.getScoreAsString();

				liveScore.setText(score);
				liveScore.setVisibility(View.VISIBLE);
				liveScore.setTextColor(getResources().getColor(R.color.red));
				
				String timeAndStatus = event.getGameTimeAndStatusAsString(true);
				
				StringBuilder sb = new StringBuilder();
				sb.append(getResources().getString(R.string.icon_live))
					.append(" ")
					.append(timeAndStatus);
				
				beginTime.setText(sb.toString());
				beginTime.setVisibility(View.VISIBLE);
				beginTime.setTextColor(getResources().getColor(R.color.red));
				
				break;
			}
			
			case INTERVAL:
			case FINISHED:
			case ABANDONED:
			case SUSPENDED:
			case NO_LIVE_UPDATES:
			case UNOFFICIAL_RESULT:
			default:
			{
				String score = event.getScoreAsString();

				liveScore.setText(score);
				liveScore.setVisibility(View.VISIBLE);
				liveScore.setTextColor(getResources().getColor(R.color.grey2));
				
				String timeInGame = event.getGameTimeAndStatusAsString(false);
				
				beginTime.setText(timeInGame);
				beginTime.setVisibility(View.VISIBLE);
				beginTime.setTextColor(getResources().getColor(R.color.grey2));
				
				break;
			}
		}

		likeView.setUserLike(competition);

		/* Share event */
		shareContainer.setTag(event);
		shareContainer.setOnClickListener(this);
		
		int color = getResources().getColor(R.color.transparent_white_background_event_page);
		int height = container.getHeight();
		
		scrollView.setScaledWidth(GenericUtils.getScreenWidth(this), height, color, R.drawable.dropshadow_pop_box);
		
		int paddingLeft = container.getPaddingLeft();
		int paddingTop = container.getPaddingTop();
		int paddingRight = container.getPaddingRight();
		int paddingBottom = container.getPaddingBottom();
		scrollView.setPaddings(paddingLeft, paddingTop, paddingRight, paddingBottom);
		
		/* Set the background image */
//		ImageAware imageAware = new ImageViewAware(team1Flag, false);
//		
//		String team1FlagUrl = url;
//			
//		SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
	}
	
	
	
	private void showHighlightsReloadButton()
	{
		highlightsReloadRelativeLayout.setOnClickListener(this);
		highlightsReloadText.setVisibility(View.VISIBLE);
		highlightsReloadIcon.setVisibility(View.VISIBLE);
		highlightsProgressLoading.setVisibility(View.GONE);
	}
	
	
	private void showHighlightsReloadButtonLoading()
	{
		highlightsReloadText.setVisibility(View.GONE);
		highlightsReloadIcon.setVisibility(View.GONE);
		highlightsProgressLoading.setVisibility(View.VISIBLE);
	}
	
	
	
	private void hideHighlightsReloadButton()
	{
		highlightsReloadText.setVisibility(View.GONE);
		highlightsReloadIcon.setVisibility(View.GONE);
		highlightsProgressLoading.setVisibility(View.GONE);
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
			
			case R.id.competition_event_block_tabs_highlights_reload_container:
			{
				showHighlightsReloadButtonLoading();
				loadHighlightsInBackground();
				
				Event event = ContentManager.sharedInstance().getCacheManager().getEventById(competitionID, eventID);
				
				String eventTitle;
				
				if(event != null)
				{
					eventTitle = event.getTitle();
				}
				else
				{
					eventTitle = Long.valueOf(eventID).toString();
					
					Log.w(TAG, "Event is null. Using eventID as a fallback in analytics reporting.");
				}
				
				Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
				
				String competitionTitle;
				
				if(competition != null)
				{
					competitionTitle = competition.getDisplayName();
				}
				else
				{
					competitionTitle = Long.valueOf(competitionID).toString();
					
					Log.w(TAG, "Competition is null. Using competitionID as a fallback in analytics reporting.");
				}
				
				TrackingGAManager.sharedInstance().sendUserCompetitionReloadPressedEvent(competitionTitle, eventTitle);
				
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
		eventPageContainer = (RelativeLayout) findViewById(R.id.competition_event_scrollable_layout);
		
		team1Name = (TextView) findViewById(R.id.competition_event_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_event_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_event_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_event_team_two_flag);
		groupHeader = (TextView) findViewById(R.id.competition_event_group_header);
		liveScore = (TextView) findViewById(R.id.competition_event_live_score);
		broadcastListView = (LinearLayout) findViewById(R.id.competition_event_broadcasts_listview);
		likeView = (LikeView) findViewById(R.id.competition_element_social_buttons_like_view);
		shareContainer = (RelativeLayout) findViewById(R.id.competition_element_social_buttons_share_button_container);
		beginTime = (TextView) findViewById(R.id.competition_event_starttime_time);
		headerteamvsteam = (TextView) findViewById(R.id.competition_event_title_header);
		headerCompetitionName = (TextView) findViewById(R.id.competition_event_world_cup_header);
		headerStandings = (TextView) findViewById(R.id.competition_standings_header);
		headerGroups = (TextView) findViewById(R.id.competition_group_header_group);

		stadiumName = (TextView) findViewById(R.id.competition_event_stadium_name);
		stadiumImageCopyright = (TextView) findViewById(R.id.competition_event_stadium_photo_credits);
		description = (TextView) findViewById(R.id.competition_event_description);

		highlightsContainerLayout = (RelativeLayout) findViewById(R.id.competition_event_block_tabs_highlights_teams_container);
		listContainerLayoutHighlights = (LinearLayout) findViewById(R.id.competition_event_highlights_table_container);

		highlightsReloadRelativeLayout = (RelativeLayout) findViewById(R.id.competition_event_block_tabs_highlights_reload_container);
		highlightsReloadText = (TextView) findViewById(R.id.competition_event_highlights_reload_text);
		highlightsReloadIcon = (TextView) findViewById(R.id.competition_event_highlights_reload_icon);
		highlightsProgressLoading = (ProgressBar) findViewById(R.id.competition_event_highlights_reload_progressbar);
		
		lineupContainerLayout = (LinearLayout) findViewById(R.id.competition_event_block_tabs_lineup_teams_container);
		pageTabIndicatorForLineupTeams = (TabPageIndicator) findViewById(R.id.tab_event_indicator_for_lineup_teams);
		viewPagerForLineupTeams = (CustomViewPager) findViewById(R.id.tab_event_pager_for_lineup_teams);
		selectedTabIndexForLineupTeams = STARTING_TAB_INDEX;

		groupListContainer = (LinearLayout) findViewById(R.id.competition_event_group_list);
		standingsListContainer = (LinearLayout) findViewById(R.id.competition_event_standings_list);
		standingsListBlock = (LinearLayout) findViewById(R.id.competition_event_block_standings_teams_container);
		scrollView = (StickyScrollView) findViewById(R.id.event_page_scrollview);
		container = (RelativeLayout) findViewById(R.id.competition_next_game_layout);
		
		/* Set background */
		eventPageContainer.setBackgroundResource(R.drawable.event_backdrop_background);
	}



	private void setAdapterForHighlights() 
	{
		if (ContentManager.sharedInstance().getCacheManager().containsHighlightsDataByEventIDForSelectedCompetition(eventID))
		{
			List<EventHighlight> eventHighlights = ContentManager.sharedInstance().getCacheManager().getHighlightsDataByEventIDForSelectedCompetition(eventID, true);

			listContainerLayoutHighlights.removeAllViews();
			
			listAdapterHighlights = new CompetitionEventHighlightsListAdapter(this, eventHighlights);

			for (int i = 0; i < listAdapterHighlights.getCount(); i++)
			{
				View listItem = listAdapterHighlights.getView(i, null, listContainerLayoutHighlights);

				if (listItem != null) 
				{
					listContainerLayoutHighlights.addView(listItem);
				}
			}
		}
		
//		listContainerLayoutHighlights.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) 
//			{
//				Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
//				
//				String competitionTitle;
//				
//				if(competition != null)
//				{
//					competitionTitle = competition.getDisplayName();
//				}
//				else
//				{
//					competitionTitle = Long.valueOf(competitionID).toString();
//					
//					Log.w(TAG, "Competition is null. Using competitionID as a fallback in analytics reporting.");
//				}
//				
//				TrackingGAManager.sharedInstance().senduserCompetitionHightlightsPressedEvent(competitionTitle);
//			}
//		});
	}



	private void setAdapterForLineupTeams(
			int selectedIndex,
			long eventID,
			String homeTeam,
			long homeTeamID,
			String awayTeam,
			long awayTeamID) 
	{
		pagerAdapterForLineupTeams = new CompetitionEventLineupTeamsTabFragmentStatePagerAdapter(
				getSupportFragmentManager(),
				viewPagerForLineupTeams,
				competitionID,
				eventID,
				homeTeam,
				homeTeamID,
				awayTeam,
				awayTeamID);

		viewPagerForLineupTeams.setAdapter(pagerAdapterForLineupTeams);
		viewPagerForLineupTeams.setOffscreenPageLimit(1);
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
		pageTabIndicatorForLineupTeams.setOnPageChangeListener(new OnPageChangeListener()
		{	
			@Override
			public void onPageSelected(int pos) 
			{
				selectedTabIndexForLineupTeams = pos;
				
				Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
				
				String competitionTitle;
				
				if(competition != null)
				{
					competitionTitle = competition.getDisplayName();
				}
				else
				{
					competitionTitle = Long.valueOf(competitionID).toString();
					
					Log.w(TAG, "Competition is null. Using competitionID as a fallback in analytics reporting.");
				}
				
				String pagerPageTitle = pagerAdapterForLineupTeams.getPageTitle(pos).toString();
				
				TrackingGAManager.sharedInstance().sendUserCompetitionTabPressedEvent(competitionTitle, pagerPageTitle);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2){}
			
			@Override
			public void onPageScrollStateChanged(int arg0){}
		});
	}



	/* Schedule for group */
	private void setAdapterForGroupList(List<Event> events, String phase) 
	{
		StringBuilder header = new StringBuilder(); 
		header.append(this.getResources().getString(R.string.team_page_squad_schedule_header))
		.append(" ")
		.append(phase);

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
	}



	/* Standings for group */
	private void setAdapterForStandingsList(List<Standings> standings, String phase) 
	{		
		if (standings != null && standings.isEmpty() == false) {
			StringBuilder header = new StringBuilder(); 

			header.append(this.getResources().getString(R.string.event_page_header_standings))
			.append(" ")
			.append(phase);

			headerStandings.setText(header.toString());

			standingsListContainer.removeAllViews();

			Collections.sort(standings, new EventStandingsComparatorByPointsAndGoalDifference());

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
		}
		else {
			standingsListBlock.setVisibility(View.GONE);
		}
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		String loadingString = getString(R.string.competition_event_loading_text);

		setLoadingLayoutDetailsMessage(loadingString);

		int reloadInterval = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionEventPageReloadInterval();

		boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadInterval);
		
		Event event = ContentManager.sharedInstance().getCacheManager().getEventByIDForSelectedCompetition(eventID);
		
		if (event != null) 
		{
			ContentManager.sharedInstance().getElseFetchFromServiceEventHighlightsData(this, forceRefresh, competitionID, eventID);
		} 
		else
		{
			ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, forceRefresh, competitionID);
		}
	}



	@Override
	protected void loadDataInBackground()
	{
		ContentManager.sharedInstance().getElseFetchFromServiceEventByID(this, true, competitionID, eventID);
		ContentManager.sharedInstance().getElseFetchFromServiceEvents(this, true, competitionID);
	}
	
	
	
	private void loadHighlightsInBackground()
	{
		ContentManager.sharedInstance().getElseFetchFromServiceEventHighlightsData(this, true, competitionID, eventID);
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = ContentManager.sharedInstance().getCacheManager().containsEventData(competitionID, eventID);

		return hasData;
	}



	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{	
		switch (requestIdentifier) 
		{
			case COMPETITION_EVENT_BY_ID:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
				break;
			}

			case COMPETITION_EVENT_HIGHLIGHTS:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					Event event = ContentManager.sharedInstance().getCacheManager().getEventByIDForSelectedCompetition(eventID);
					
					if(event != null)
					{
						EventMatchStatusEnum matchStatus = event.getMatchStatus();
						
						if(matchStatus == EventMatchStatusEnum.INTERVAL || matchStatus == EventMatchStatusEnum.IN_PROGRESS)
						{
							showHighlightsReloadButton();
						}
						else
						{
							hideHighlightsReloadButton();
						}
						
						loadDataInBackground();
						
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					}
					else
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
					}
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
				
				break;
			}
			
			case COMPETITION_INITIAL_DATA:
			{
				Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
				
				ContentManager.sharedInstance().getCacheManager().setSelectedCompetition(competition);
				
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	
				if (phase != null) 
				{
					int reloadInterval = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionEventPageReloadInterval();
	
					boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadInterval);
	
					ContentManager.sharedInstance().getElseFetchFromServiceEventStandingsData(this, forceRefresh, competitionID, phase.getPhaseId());
				}
			}
			
			case COMPETITION_STANDINGS_BY_PHASE_ID: 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	
				break;
			}
	
			case USER_ADD_LIKE:
			{
				updateStatusOfLikeView();
				break;
			}
			
			case COMPETITION_EVENTS:
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unknown request identifier");
				break;
			}
		}
	}



	private void setListView(final Event event) 
	{
		broadcastListView.removeAllViews();

		List<EventBroadcast> broadcasts = event.getEventBroadcasts();

		Collections.sort(broadcasts, new EventBroadcastByStartTime());

		listAdapter = new CompetitionEventPageBroadcastListAdapter(this, competitionID, eventID, broadcasts);

		for (int i = 0; i < listAdapter.getCount(); i++) 
		{
			View listItem = listAdapter.getView(i, null, broadcastListView);

			if (listItem != null) 
			{
				broadcastListView.addView(listItem);
			}
		}
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
				String type = null;
				
				if (tabToNavigateTo == CompetitionTabFragmentStatePagerAdapter.TEAM_STANDINGS_POSITION) 
				{
					type = "Standings";
				}
				
				else if (tabToNavigateTo == CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION) 
				{
					type = "Schedule";
				}
				
				TrackingGAManager.sharedInstance().sendUserCompetitionViewAllLinkPressedEvent(type);
				
				Intent intent = new Intent(EventPageActivity.this, CompetitionPageActivity.class);

				intent.putExtra(Constants.INTENT_COMPETITION_ID, competitionID);
				intent.putExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, tabToNavigateTo);

				startActivity(intent);
			}
		};
	}



	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}




	private void setBackgroundLoadingTimerForHighlights(int valueInSeconds)
	{
		if(valueInSeconds > -1)
		{
			int backgroundTimerValue = (int) (valueInSeconds*DateUtils.TOTAL_MILLISECONDS_IN_ONE_SECOND);

			if(backgroundLoadTimerForHighlights != null)
			{
				backgroundLoadTimerForHighlights.cancel();
			}
			backgroundLoadTimerForHighlights = new Timer();

			backgroundLoadTimerForHighlights.schedule(new java.util.TimerTask()
			{
				@Override
				public void run()
				{
					loadHighlightsInBackground();
				}
			}, backgroundTimerValue, backgroundTimerValue);
		}
	}
}
