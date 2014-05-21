package com.mitv.activities.competition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TeamPageActivity extends BaseContentActivity implements ViewCallbackListener, FetchDataProgressCallbackListener {
	
	private static final String TAG = TeamPageActivity.class.getName();
	
	private long teamID;
	private long competitionID;
	private Team team;
	
	/* Main content */
	private Event event;
	private ImageView teamFlagImage;
	private TextView teamName;
	private TextView teamFootballNational;
	private ImageView teamImage;
	private TextView about;
	private TextView founded;
	private TextView coach;
	private TextView location;
	private TextView arenas;
	private TextView photoFrom;
	
	/* Squad */
	/* Standings */
	/* Schedule */
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded())
		{
			return;
		}
		
		setContentView(R.layout.layout_competition_team_page_main);
		
		Intent intent = getIntent();
		
		teamID = intent.getLongExtra(Constants.INTENT_COMPETITION_TEAM_ID, 0);
		
		competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);
		
		long eventID = intent.getLongExtra(Constants.INTENT_COMPETITION_EVENT_ID, 0);
		
		event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_TEAMS);
		
		initLayout();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	
	
	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}

	
	
	@Override
	protected void updateUI(UIStatusEnum status) {
		super.updateUIBaseElements(status);
		
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setTeamInfoLayout();
				setSquadLayout();
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
	protected void loadData() {
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_event_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		/* Always re-fetch the data from the service */
		boolean forceRefreshOfHighlights = true;
		
		ContentManager.sharedInstance().getElseFetchFromServiceTeamData(this, forceRefreshOfHighlights, competitionID);
	}

	
	
	@Override
	protected boolean hasEnoughDataToShowContent() {
		boolean hasData = ContentManager.sharedInstance().getFromCacheHasTeamData(competitionID);
		
		return hasData;
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		if(fetchRequestResult.wasSuccessful())
		{
			
			if(event == null)
			{
				team = ContentManager.sharedInstance().getFromCacheTeamByID(teamID);
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
	
	
	
	private void initLayout() {
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		StringBuilder eventName = new StringBuilder();
		eventName.append(team.getDisplayName());
		
		actionBar.setTitle(eventName.toString());
		
		/* Main content */
		teamFlagImage = (ImageView) findViewById(R.id.competition_team_page_header_flag_img);
		teamName = (TextView) findViewById(R.id.competition_team_page_team_name);
		teamFootballNational = (TextView) findViewById(R.id.competition_team_page_info_national);
		teamImage = (ImageView) findViewById(R.id.competition_team_page_team_img);
		about = (TextView) findViewById(R.id.competition_team_page_about);
		founded = (TextView) findViewById(R.id.competition_team_page_founded);
		coach = (TextView) findViewById(R.id.competition_team_page_coach);
		location = (TextView) findViewById(R.id.competition_team_page_location);
		arenas = (TextView) findViewById(R.id.competition_team_page_arenas);
		photoFrom = (TextView) findViewById(R.id.competition_team_page_photo_from);
		
		/* Squad */
		/* Standings for Group X */
		/* Schedule for Group X */
	}
	
	
	
	private void setTeamInfoLayout() {
		if (team != null) {
			ImageAware imageAware = new ImageViewAware(teamFlagImage, false);
			ImageAware imageAware2 = new ImageViewAware(teamImage, false);
				
			String teamFlagUrl = team.getImages().getFlag().getImageURLForDeviceDensityDPI();
			String teamImageUrl = team.getImages().getBanner().getImageURLForDeviceDensityDPI();
				
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(teamFlagUrl, imageAware);
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(teamImageUrl, imageAware2);
			
			String name = team.getDisplayName();
			teamName.setText(name);
			
			teamFootballNational.setText(this.getResources().getString(R.string.team_page_team_info_header));
		}
	}
	
	
	
	private void setSquadLayout() {
		
	}
	
	
	
	private void setStandingsLayout() {
		
	}
	
	
	
	private void setScheduleLayout() {
		
	}

}
