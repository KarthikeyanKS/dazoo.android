
package com.mitv.activities.competition;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.EventBroadcastByStartTime;
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
	private long eventID;
	private Competition competition;
	private long competitionID;
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

	private RelativeLayout highlightsReloadRelativeLayout;
	private TextView highlightsReloadText;
	private TextView highlightsReloadIcon;
	private ProgressBar highlightsProgressLoading;
	
	
	private LinearLayout groupListContainer;
	private LinearLayout standingsListContainer;
	private RelativeLayout standingsListBlock;
	private CompetitionEventEventsByGroupListAdapter groupListAdapter;
	private CompetitionEventStandingsListAdapter standingsListAdapter;


	/* Timer for re-fetching data in the background while the user is on the same activity */
	private Timer backgroundLoadTimerForHighlights;



	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}

		setContentView(R.layout.layout_competition_event_page);

		Intent intent = getIntent();

		competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);

		eventID = intent.getLongExtra(Constants.INTENT_COMPETITION_EVENT_ID, 0);

		competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);

		competitionName = intent.getStringExtra(Constants.INTENT_COMPETITION_NAME);

		event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);

		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_EVENT_HIGHLIGHTS);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_EVENT_BY_ID);

		initLayout();

		if (event == null) 
		{
			ContentManager.sharedInstance().setSelectedCompetition(competition);

			registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

			registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_STANDINGS_BY_PHASE_ID);
		}
		else
		{
			boolean isEventLive = event.isLive();

			if(isEventLive)
			{
				int reloadIntervalInSeconds = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageHighlightReloadInterval();

				setBackgroundLoadingTimerForHighlights(reloadIntervalInSeconds);
			}
		}

		int reloadIntervalInMinutes = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageReloadInterval();

		setBackgroundLoadTimerValueInSeconds(reloadIntervalInMinutes);

		setAllAdapters();
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
			event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);

			setAllAdapters();

			boolean containsBroadcastDetails = event.containsBroadcastDetails();

			if (containsBroadcastDetails)
			{
				setListView();
			}

			boolean isEventLive = event.isLive();

			if(isEventLive)
			{
				int reloadIntervalInSeconds = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageHighlightReloadInterval();

				setBackgroundLoadingTimerForHighlights(reloadIntervalInSeconds);
			}

			setData();

			if (ContentManager.sharedInstance().getFromCacheHasHighlightsDataByEventIDForSelectedCompetition(eventID)) {
				setAdapterForHighlights();
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

		if (event == null) 
		{
			homeTeam = "";
			homeTeamID = 0l;
			awayTeam = "";
			awayTeamID = 0l;
			phaseString = "";

			standings = new ArrayList<Standings>();

			events = new ArrayList<Event>();

		} else {
			phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(event.getPhaseId());

			events = ContentManager.sharedInstance().getFromCacheEventsForPhaseInSelectedCompetition(phase.getPhaseId());

			if (ContentManager.sharedInstance().getFromCacheHasStandingsForPhaseInSelectedCompetition(phase.getPhaseId())) {
				standings = ContentManager.sharedInstance().getFromCacheStandingsForPhaseInSelectedCompetition(phase.getPhaseId());
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



	private void setData()
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
				ImageAware imageAware = new ImageViewAware(team1Flag, false);
				ImageAware imageAwareHighlights = new ImageViewAware(team1FlagHighlights, false);

				String team1FlagUrl = team1.getFlagImageURL();

				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAwareHighlights);

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

				highlightsFlagAndNameContainerOne.setOnClickListener(new View.OnClickListener() 
				{
					public void onClick(View v)
					{
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, homeTeamName, "Highlights");
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
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, awayTeamName, "Country");
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
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, awayTeamName, "Highlights");
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
		
		/** Setting the visibility of the LineUp **/
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
				liveScore.setVisibility(View.GONE);
				
				liveStatus.setVisibility(View.GONE);
				
				String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());

				beginTime.setText(eventStartTimeHourAndMinuteAsString);
				beginTime.setVisibility(View.VISIBLE);
				
				StringBuilder sb = new StringBuilder();
				sb.append(event.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(event.getEventTimeDayAndMonthAsString());

				beginTimeDate.setText(sb.toString());
				beginTimeDate.setVisibility(View.VISIBLE);
				break;
			}
			
			case DELAYED:
			{
				liveScore.setVisibility(View.GONE);
				
				liveStatus.setVisibility(View.GONE);
				
				String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
				
				StringBuilder eventStartTimeHourAndMinuteSB = new StringBuilder();
				eventStartTimeHourAndMinuteSB.append(eventStartTimeHourAndMinuteAsString)
				.append(" (")
				.append(getString(R.string.event_page_delayed))
				.append(")");

				beginTime.setText(eventStartTimeHourAndMinuteAsString);
				beginTime.setVisibility(View.VISIBLE);
				
				StringBuilder sb = new StringBuilder();
				sb.append(event.getEventTimeDayOfTheWeekAsString())
				.append(" ")
				.append(event.getEventTimeDayAndMonthAsString());

				beginTimeDate.setText(sb.toString());
				beginTimeDate.setVisibility(View.VISIBLE);
				break;
			}
	
			case POSTPONED:
			{
				String score = event.getScoreAsString();

				liveScore.setText(score);
				liveScore.setVisibility(View.GONE);
				liveScore.setTextColor(getResources().getColor(R.color.red));
				
				String timeAndStatus = event.getGameTimeAndStatusAsString(false);
				
				liveStatus.setText(timeAndStatus);
				liveStatus.setVisibility(View.VISIBLE);
				liveStatus.setTextColor(getResources().getColor(R.color.red));
				
				beginTime.setVisibility(View.GONE);
				beginTimeDate.setVisibility(View.GONE);
				break;
			}
			
			case IN_PROGRESS:
			{
				String score = event.getScoreAsString();

				liveScore.setText(score);
				liveScore.setVisibility(View.VISIBLE);
				liveScore.setTextColor(getResources().getColor(R.color.red));
				
				String timeAndStatus = event.getGameTimeAndStatusAsString(true);
				
				liveStatus.setText(timeAndStatus);
				liveStatus.setVisibility(View.VISIBLE);
				liveStatus.setTextColor(getResources().getColor(R.color.red));
				
				beginTime.setVisibility(View.GONE);
				beginTimeDate.setVisibility(View.GONE);
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
				
				liveStatus.setText(timeInGame);
				liveStatus.setVisibility(View.VISIBLE);
				liveStatus.setTextColor(getResources().getColor(R.color.grey2));
				
				beginTime.setVisibility(View.GONE);
				beginTimeDate.setVisibility(View.GONE);
				break;
			}
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

		highlightsReloadRelativeLayout = (RelativeLayout) findViewById(R.id.competition_event_block_tabs_highlights_reload_container);
		highlightsReloadText = (TextView) findViewById(R.id.competition_event_highlights_reload_text);
		highlightsReloadIcon = (TextView) findViewById(R.id.competition_event_highlights_reload_icon);
		highlightsProgressLoading = (ProgressBar) findViewById(R.id.competition_event_highlights_reload_progressbar);
		
		lineupContainerLayout = (RelativeLayout) findViewById(R.id.competition_event_block_tabs_lineup_teams_container);
		pageTabIndicatorForLineupTeams = (TabPageIndicator) findViewById(R.id.tab_event_indicator_for_lineup_teams);
		viewPagerForLineupTeams = (CustomViewPager) findViewById(R.id.tab_event_pager_for_lineup_teams);
		selectedTabIndexForLineupTeams = STARTING_TAB_INDEX;

		groupListContainer = (LinearLayout) findViewById(R.id.competition_event_group_list);
		standingsListContainer = (LinearLayout) findViewById(R.id.competition_event_standings_list);
		standingsListBlock = (RelativeLayout) findViewById(R.id.competition_event_block_standings_teams_container);

		highlightsFlagAndNameContainerOne = (RelativeLayout) findViewById(R.id.competition_event_highlights_team_one_flag_container);
		highlightsFlagAndNameContainerTwo = (RelativeLayout) findViewById(R.id.competition_event_highlights_team_two_flag_container);
	}



	private void setAdapterForHighlights() 
	{
		long eventID = event.getEventId();

		if (ContentManager.sharedInstance().getFromCacheHasHighlightsDataByEventIDForSelectedCompetition(eventID))
		{
			List<EventHighlight> eventHighlights = ContentManager.sharedInstance().getFromCacheHighlightsDataByEventIDForSelectedCompetition(eventID, true);

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
		listContainerLayoutHighlights.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				TrackingGAManager.sharedInstance().senduserCompetitionHightlightsPressedEvent(competitionName);
			}
		});
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
		pageTabIndicatorForLineupTeams.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				selectedTabIndexForLineupTeams = pos;
				TrackingGAManager.sharedInstance().sendUserCompetitionTabPressedEvent(competitionName, pagerAdapterForLineupTeams.getPageTitle(pos).toString());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
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

		int reloadInterval = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageReloadInterval();

		boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadInterval);

		if (event != null) 
		{
			ContentManager.sharedInstance().getElseFetchFromServiceEventHighlighstData(this, forceRefresh, event.getCompetitionId(), event.getEventId());
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
	}
	
	
	
	private void loadHighlightsInBackground()
	{
		ContentManager.sharedInstance().getElseFetchFromServiceEventHighlighstData(this, true, event.getCompetitionId(), event.getEventId());
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = false;
		if (event != null) 
		{
			hasData = ContentManager.sharedInstance().getFromCacheHasEventData(event.getCompetitionId(), event.getEventId());
		}

		return hasData;
	}



	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{	
		switch (requestIdentifier) 
		{
			case COMPETITION_STANDINGS_BY_PHASE_ID: 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	
				break;
			}
			
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
	
			case COMPETITION_INITIAL_DATA:
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	
				if (phase != null) 
				{
					int reloadInterval = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageReloadInterval();
	
					boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadInterval);
	
					ContentManager.sharedInstance().getElseFetchFromServiceEventStandingsData(this, forceRefresh, competitionID, phase.getPhaseId());
				}
	
				break;
			}
	
			case COMPETITION_EVENT_HIGHLIGHTS:
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
				
				if(fetchRequestResult.wasSuccessful())
				{
					if(event == null)
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
					} 
					else
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
						
						loadDataInBackground();
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

		long competitionId = event.getCompetitionId();
		long eventId = event.getEventId();

		listAdapter = new CompetitionEventPageBroadcastListAdapter(this, competitionId, eventId, broadcasts);

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
				if (tabToNavigateTo == CompetitionTabFragmentStatePagerAdapter.TEAM_STANDINGS_POSITION) {
					type = "Standings";
				}
				else if (tabToNavigateTo == CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION) {
					type = "Schedule";
				}
				
				TrackingGAManager.sharedInstance().sendUserCompetitionViewAllLinkPressedEvent(type);
				
				Intent intent = new Intent(EventPageActivity.this, CompetitionPageActivity.class);

				intent.putExtra(Constants.INTENT_COMPETITION_ID, event.getCompetitionId());
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
