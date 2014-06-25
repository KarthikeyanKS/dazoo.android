
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
import com.mitv.activities.base.BaseCommentsActivity;
import com.mitv.adapters.list.CompetitionEventStandingsListAdapter;
import com.mitv.adapters.list.CompetitionTagEventsListAdapter;
import com.mitv.adapters.list.CompetitionTeamSquadsTeamsListAdapter;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.EventStandingsComparatorByPointsAndGoalDifference;
import com.mitv.models.comparators.TeamSquadComparatorByPositionANdShirtNumber;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;
import com.mitv.ui.elements.LikeView;
import com.mitv.utilities.GenericUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class TeamPageActivity 
	extends BaseCommentsActivity 
	implements ViewCallbackListener, FetchDataProgressCallbackListener
{	
	private static final String TAG = TeamPageActivity.class.getName();
	
	
	private Team team;
	private long teamID;
	private long competitionID;
	private Phase phase;
	private long phaseID;
	private List<Event> events;
	
	/* Main content */
	private ImageView teamFlagImage;
	private TextView teamName;
	private TextView teamFootballNational;
	private ImageView teamImage;
	private TextView about;
	private TextView photoFrom;
	
	/* Like and Reminder */
	private LikeView likeView;
	private RelativeLayout shareContainer;
	
	/* Squad */
	private List<TeamSquad> teamSquads;
	private LinearLayout squadListContainer;
	private CompetitionTeamSquadsTeamsListAdapter squadListAdapter;
	
	/* Standings */
	private LinearLayout standingsListContainer;
	private CompetitionEventStandingsListAdapter standingsListAdapter;
	private TextView standingsHeader;
	private LinearLayout standingsContainer;
	
	/* Schedule */
	private TextView scheduleHeader;
	private LinearLayout scheduleListContainer;
	private CompetitionTagEventsListAdapter scheduleListAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_competition_team_page_main);
		
		Intent intent = getIntent();
		
		competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);
		
		teamID = intent.getLongExtra(Constants.INTENT_COMPETITION_TEAM_ID, 0);
		
		phaseID = intent.getLongExtra(Constants.INTENT_COMPETITION_PHASE_ID, 0);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_TEAM_BY_ID);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_TEAM_SQUAD);
		
		initLayout();
		
		int reloadIntervalInMinutes = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionTeamPageReloadInterval();
		
		setBackgroundLoadTimerValueInSeconds(reloadIntervalInMinutes);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		updateStatusOfLikeView();
				
		super.onResume();
	}
	
	
	
	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}

	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setMainLayoutLayout();
				setStandingsLayout();
				setScheduleLayout();
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
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_team_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		int reloadInterval = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionTeamPageReloadInterval();
		
		boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadInterval);
		
		ContentManager.sharedInstance().getElseFetchFromServiceTeamByID(this, forceRefresh, competitionID, teamID);
		
		ContentManager.sharedInstance().getElseFetchFromServiceSquadByTeamID(this, forceRefresh, competitionID, teamID);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		ContentManager.sharedInstance().getElseFetchFromServiceTeamByID(this, true, competitionID, teamID);
		
		ContentManager.sharedInstance().getElseFetchFromServiceSquadByTeamID(this, true, competitionID, teamID);
	}

	
	
	@Override
	protected boolean hasEnoughDataToShowContent() 
	{
		boolean hasData = ContentManager.sharedInstance().getCacheManager().containsTeamData(competitionID) && 
				          ContentManager.sharedInstance().getCacheManager().containsSquadForTeamID(teamID);
		
		return hasData;
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch(requestIdentifier)
		{			
			case COMPETITION_TEAM_BY_ID:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					loadDisqusForTeam(teamID);
					
					updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);		
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
			}
	
			case COMPETITION_TEAM_SQUAD:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					setSquadLayout();
				}
				break;
			}
	
			case USER_ADD_LIKE: 
			{
				updateStatusOfLikeView();
				break;
			}
			
			case DISQUS_THREAD_DETAILS:
			{
				if (fetchRequestResult.wasSuccessful())
				{
					int totalDisqusPosts = ContentManager.sharedInstance().getCacheManager().getDisqusTotalPostsForLatestBroadcast();
					
					showAndReloadDisqusCommentsWebview(totalDisqusPosts);
				}
				else 
				{
					showAndReloadDisqusCommentsWebview(0);
				}
				
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unknown request identifier");
				break;
			}
		}
	}
	
	
	
	private void initLayout() 
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		StringBuilder eventName = new StringBuilder();
		eventName.append(getTeam().getDisplayName());
		
		actionBar.setTitle(eventName.toString());
		
		/* Main content */
		teamFlagImage = (ImageView) findViewById(R.id.competition_team_page_header_flag_img);
		teamName = (TextView) findViewById(R.id.competition_team_page_team_name);
		teamFootballNational = (TextView) findViewById(R.id.competition_team_page_info_national);
		teamImage = (ImageView) findViewById(R.id.competition_team_page_team_img);
		about = (TextView) findViewById(R.id.competition_team_page_about);
		photoFrom = (TextView) findViewById(R.id.competition_team_page_photo_from);
		shareContainer = (RelativeLayout) findViewById(R.id.competition_element_social_buttons_share_button_container);
		
		/* Squad */
		squadListContainer = (LinearLayout) findViewById(R.id.competition_team_page_squad_list);
		
		/* Standings */
		standingsListContainer = (LinearLayout) findViewById(R.id.competition_team_page_standings_list);
		standingsHeader = (TextView) findViewById(R.id.competition_team_page_standings_header);
		standingsContainer = (LinearLayout) findViewById(R.id.competition_team_page_standings_container);
		
		/* Schedule */
		scheduleHeader = (TextView) findViewById(R.id.competition_team_page_schedule_header);
		scheduleListContainer = (LinearLayout) findViewById(R.id.competition_team_page_schedule_list);
		
		initDisqus();
	}
	
	
	
	private void setMainLayoutLayout() 
	{
		boolean filterFinishedEvents = false;
		boolean filterLiveEvents = false;
		events = ContentManager.sharedInstance().getCacheManager().getEventsByTeamIDForSelectedCompetition(filterFinishedEvents, filterLiveEvents, teamID);
		
		phase = ContentManager.sharedInstance().getCacheManager().getPhaseByIDForSelectedCompetition(phaseID);
		
		if (team != null) 
		{
			/* Team flag */
			ImageAware imageAwareForTeamFlag = new ImageViewAware(teamFlagImage, false);
			
			String teamFlagUrl = team.getFlagImageURL();
			
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(teamFlagUrl, imageAwareForTeamFlag);

			String name = team.getDisplayName();
			teamName.setText(name);
			
			teamFootballNational.setText(this.getResources().getString(R.string.team_page_team_info_header));
			
			/* Team image */
			ImageAware imageAwareForTeamBanner = new ImageViewAware(teamImage, false);

			String teamBannerUrl = team.getTeamImageURL();
				
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionTeamBannerOptions(teamBannerUrl, imageAwareForTeamBanner);
			
			/* Description */
			String textAbout = team.getDescription();
			if (textAbout != null && !textAbout.isEmpty()) 
			{
				about.setText(textAbout);
				about.setVisibility(View.VISIBLE);
			}

			String credit = team.getTeamImageCopyright();
			
			if (credit != null && !credit.isEmpty()) 
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append(this.getResources().getString(R.string.team_page_team_photo_from_header))
					.append(" ")
					.append(team.getTeamImageCopyright());
				
				photoFrom.setText(sb.toString());
				photoFrom.setVisibility(View.VISIBLE);
			}
			
			likeView = (LikeView) findViewById(R.id.competition_element_social_buttons_like_view);
			
			likeView.setUserLike(team);
			
			/* Share team */
			shareContainer.setTag(team);
			shareContainer.setOnClickListener(this);
		}
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		/* Important to call super, else tabs wont work */
		super.onClick(v);

		int viewId = v.getId();

		Team shareTeam = (Team) v.getTag();

		switch (viewId) 
		{
			case R.id.competition_element_social_buttons_share_button_container:
			{
				GenericUtils.startShareActivity(this, shareTeam);
				break;
			}
			default: 
			{
				Log.w(TAG, "Unhandled onClick action");
				break;
			}
		}
	}
	
	
	
	private void setSquadLayout() 
	{
		squadListContainer.removeAllViews();
		
		teamSquads = ContentManager.sharedInstance().getCacheManager().getSquadByTeamId(teamID, false);
		
		Collections.sort(teamSquads, new TeamSquadComparatorByPositionANdShirtNumber());
		
		squadListAdapter = new CompetitionTeamSquadsTeamsListAdapter(this, teamSquads);
		
		for (int i = 0; i < squadListAdapter.getCount(); i++) 
		{
            View listItem = squadListAdapter.getView(i, null, squadListContainer);
           
            if (listItem != null) 
            {
            	squadListContainer.addView(listItem);
            }
        }
		squadListContainer.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{				
				Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
				
				String competitionName;
				
				if(competition != null)
				{
					competitionName = competition.getDisplayName();
				}
				else
				{
					competitionName = Long.valueOf(competitionID).toString();
					
					Log.w(TAG, "Competition is null. Using competitionID as a fallback in analytics reporting.");
				}
				
				String teamName;
				
				if(team != null)
				{
					teamName = team.getDisplayName();
				}
				else
				{
					teamName = Long.valueOf(teamID).toString();
					
					Log.w(TAG, "Team is null. Using teamID as a fallback in analytics reporting.");
				}
	
				TrackingGAManager.sharedInstance().sendUserCompetitionSquadPressedEvent(competitionName, teamName);			
			}
		});
	}
	
	
	
	private void setStandingsLayout() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getResources().getString(R.string.event_page_header_standings))
			.append(" ")
			.append(phase.getPhase());
		
		standingsHeader.setText(sb.toString());
		
		standingsListContainer.removeAllViews();
		
		List<Standings> standings = ContentManager.sharedInstance().getCacheManager().getStandingsForPhaseInSelectedCompetition(phase.getPhaseId());
		
		if (standings != null && !standings.isEmpty()) {
			Collections.sort(standings, new EventStandingsComparatorByPointsAndGoalDifference());
			
			Collections.reverse(standings);
			
			String viewBottomMessage = getString(R.string.event_page_standings_list_show_more);
			
			Runnable procedure = getNavigateToCompetitionPageProcedure(CompetitionTabFragmentStatePagerAdapter.TEAM_STANDINGS_POSITION);
			
			/* Using the same list adapter as the evens */
			standingsListAdapter = new CompetitionEventStandingsListAdapter(this, standings, true, viewBottomMessage, procedure);
			
			for (int i = 0; i < standingsListAdapter.getCount(); i++) 
			{
	            View listItem = standingsListAdapter.getView(i, null, standingsListContainer);
	           
	            if (listItem != null) 
	            {
	            	standingsListContainer.addView(listItem);
	            }
	        }
			
			standingsContainer.setVisibility(View.VISIBLE);
			
		} else {
			standingsContainer.setVisibility(View.GONE);
		}
	}
	
	
	
	private void setScheduleLayout() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getResources().getString(R.string.team_page_schedule_header))
			.append(" ")
			.append(team.getDisplayName());
		
		scheduleHeader.setText(sb.toString());
		
		scheduleListContainer.removeAllViews();
		
		String viewBottomMessage = getString(R.string.event_page_groups_list_show_more);
		
		Runnable procedure = getNavigateToCompetitionPageProcedure(CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION);

		scheduleListAdapter = new CompetitionTagEventsListAdapter(this, events, true, viewBottomMessage, procedure);
		
		for (int i = 0; i < scheduleListAdapter.getCount(); i++) 
		{
            View listItem = scheduleListAdapter.getView(i, null, scheduleListContainer);
           
            if (listItem != null) 
            {
            	scheduleListContainer.addView(listItem);
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
	
	
	
	private Team getTeam()
	{
		if(this.team == null)
		{
			Log.d(TAG, "Team ID is: " + teamID);
			
			this.team = ContentManager.sharedInstance().getCacheManager().getTeamById(teamID);
		}
		
		return team;
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
				
				Intent intent = new Intent(TeamPageActivity.this, CompetitionPageActivity.class);		
				
				intent.putExtra(Constants.INTENT_COMPETITION_ID, competitionID);
                intent.putExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, tabToNavigateTo);
                
				startActivity(intent);
			}
		};
	}

}
