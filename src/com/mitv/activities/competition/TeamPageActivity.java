
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
import com.mitv.adapters.list.CompetitionEventLineUpTeamsListAdapter;
import com.mitv.adapters.list.CompetitionTeamSquadsTeamsListAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class TeamPageActivity 
	extends BaseContentActivity 
	implements ViewCallbackListener, FetchDataProgressCallbackListener
{	
	private static final String TAG = TeamPageActivity.class.getName();

	
	private Team team;
	private long teamID;
	private long competitionID;
	
	
	/* Main content */
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
	private List<TeamSquad> teamSquads;
	private LinearLayout squadListContainer;
	private CompetitionTeamSquadsTeamsListAdapter squadListAdapter;
	
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
		
		competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);
		
		teamID = intent.getLongExtra(Constants.INTENT_COMPETITION_TEAM_ID, 0);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_TEAM_BY_ID);
		
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
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setMainLayoutLayout();
//				setSquadLayout();
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
		
		/* Always re-fetch the data from the service */
		boolean forceRefreshOfHighlights = true;
		
		ContentManager.sharedInstance().getElseFetchFromServiceTeamByID(this, forceRefreshOfHighlights, competitionID, teamID);
	}

	
	
	@Override
	protected boolean hasEnoughDataToShowContent() 
	{
		boolean hasData = ContentManager.sharedInstance().getFromCacheHasTeamData(competitionID);
		
		return hasData;
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
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
		founded = (TextView) findViewById(R.id.competition_team_page_founded);
		coach = (TextView) findViewById(R.id.competition_team_page_coach);
		location = (TextView) findViewById(R.id.competition_team_page_location);
		arenas = (TextView) findViewById(R.id.competition_team_page_arenas);
		photoFrom = (TextView) findViewById(R.id.competition_team_page_photo_from);
		
		/* Squad */
		squadListContainer = (LinearLayout) findViewById(R.id.competition_team_page_squad_list);
		
		/* Standings for Group X */
		/* Schedule for Group X */
	}
	
	
	
	private void setMainLayoutLayout() {
		if (team != null) {
			
			ImageAware imageAwareForTeamFlag = new ImageViewAware(teamFlagImage, false);
			
			String teamFlagUrl = team.getImages().getFlag().getImageURLForDeviceDensityDPI();
			
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(teamFlagUrl, imageAwareForTeamFlag);

			ImageAware imageAwareForTeamBanner = new ImageViewAware(teamImage, false);
				
			String teamBannerUrl = "";
				
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionTeamBannerOptions(teamBannerUrl, imageAwareForTeamBanner);	

			String name = team.getDisplayName();
			teamName.setText(name);
			
			teamFootballNational.setText(this.getResources().getString(R.string.team_page_team_info_header));
			
			/* TODO Change to real data */
			String textAbout = this.getResources().getString(R.string.team_page_team_info_about_hard_coded);
			about.setText(textAbout);
			
			founded.setText(this.getResources().getString(R.string.team_page_team_founded_hard_coded));
			coach.setText(this.getResources().getString(R.string.team_page_team_coach_hard_coded));
			location.setText(this.getResources().getString(R.string.team_page_team_location_hard_coded));
			arenas.setText(this.getResources().getString(R.string.team_page_team_arenas_hard_coded));
			photoFrom.setText(this.getResources().getString(R.string.team_page_team_photo_from_hard_coded));
		}
	}
	
	
	
	private void setSquadLayout() {
		squadListContainer.removeAllViews();
		
//		teamSquads = ContentManager.sharedInstance().getFromCacheTeamSquadsDataByTeamID(teamID);
		
		squadListAdapter = new CompetitionTeamSquadsTeamsListAdapter(this, teamSquads);
		
		for (int i = 0; i < squadListAdapter.getCount(); i++) 
		{
            View listItem = squadListAdapter.getView(i, null, squadListContainer);
           
            if (listItem != null) 
            {
            	squadListContainer.addView(listItem);
            }
        }
		
		squadListContainer.measure(0, 0);
	}
	
	
	
	private void setStandingsLayout() {
		
	}
	
	
	
	private void setScheduleLayout() {
		
	}
	
	
	
	private Team getTeam()
	{
		if(this.team == null)
		{
			Log.d(TAG, "Team ID is: " + teamID);
			
			this.team = ContentManager.sharedInstance().getFromCacheTeamByID(teamID);
		}
		
		return team;
	}

}
