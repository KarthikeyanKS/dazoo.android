package com.mitv.activities.competition;

import android.content.Intent;
import android.os.Bundle;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;

public class TeamPageActivity extends BaseContentActivity implements ViewCallbackListener, FetchDataProgressCallbackListener {
	
	private static final String TAG = TeamPageActivity.class.getName();
	
	private Event event;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded())
		{
			return;
		}
		
		setContentView(R.layout.layout_competition_team_page_main);
		
		Intent intent = getIntent();
		
		long teamID = intent.getLongExtra(Constants.INTENT_COMPETITION_TEAM_ID, 0);
		
		long eventID = intent.getLongExtra(Constants.INTENT_COMPETITION_EVENT_ID, 0);
		
		event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
				
		long phaseID = event.getPhaseId();
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
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
//		boolean forceRefreshOfHighlights = true;
		
//		ContentManager.sharedInstance().getElseFetchFromServiceEventHighlighstData(this, forceRefreshOfHighlights, event.getCompetitionId(), event.getEventId());
	}

	
	
	@Override
	protected boolean hasEnoughDataToShowContent() {
		boolean hasData = true; //ContentManager.sharedInstance().getFromCacheHasEventData(event.getCompetitionId(), event.getEventId());
		
		return hasData;
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
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
	}
	
	
	
	private void initLayout() {
		/* Basic layout */
		/* Squad */
		/* Standings for Group X */
		/* Schedule for Group X */
	}
	
	
	
	private void setTeamInfoLayout() {
		
	}
	private void setSquadLayout() {
		
	}
	private void setStandingsLayout() {
		
	}
	private void setScheduleLayout() {
		
	}

}
