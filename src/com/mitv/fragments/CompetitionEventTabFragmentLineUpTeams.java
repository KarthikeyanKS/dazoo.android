
package com.mitv.fragments;



import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.adapters.list.CompetitionEventLineUpTeamsListAdapter;
import com.mitv.adapters.pager.CompetitionEventLineupTeamsTabFragmentStatePagerAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.EventLineUpComparatorByPositionAndShirtNumber;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;
import com.mitv.ui.elements.CustomViewPager;



public class CompetitionEventTabFragmentLineUpTeams 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private CustomViewPager viewPager;
	
	private long competitionID;
	private long eventID;
	private long teamID;

	private View whiteDivider;
	private TextView subsHeader;
	private LinearLayout eventListOfSubs;
	
	private LinearLayout containerLayout; 
	private LinearLayout listContainerLayout;
	private CompetitionEventLineUpTeamsListAdapter listAdapter;
	private CompetitionEventLineUpTeamsListAdapter listAdapterSubs;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionEventTabFragmentLineUpTeams()
	{
		super();
	}
	
	
	
	public CompetitionEventTabFragmentLineUpTeams(
			final CustomViewPager viewPager,
			final long competitionID,
			final long eventID,
			final long teamID,
			final String tabId, 
			final String tabTitle, 
			final EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
		
		this.competitionID = competitionID;
		this.eventID = eventID;
		this.teamID = teamID;
		
		this.viewPager = viewPager;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_event_tab_fragment_container_lineup, null);
		
		containerLayout = (LinearLayout) rootView.findViewById(R.id.competition_event_table_container_layout);
		
		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_event_table_container);
		
		eventListOfSubs = (LinearLayout) rootView.findViewById(R.id.competition_event_table_substitute_players_container);
		
		subsHeader = (TextView) rootView.findViewById(R.id.competition_event_subs_header);
		
		whiteDivider = (View) rootView.findViewById(R.id.competition_event_header_divider);
	
		super.initRequestCallbackLayouts(rootView);
		
		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
			eventID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_EVENT_ID, 0);
			teamID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_TEAM_ID, 0);
			competitionID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_ID, 0);
        }
		
		return rootView;
	}
	
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        
        outState.putLong(Constants.INTENT_COMPETITION_EVENT_ID, eventID);
        outState.putLong(Constants.INTENT_COMPETITION_TEAM_ID, teamID);
        outState.putLong(Constants.INTENT_COMPETITION_ID, competitionID);
    }
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_event_lineup_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		ContentManager.sharedInstance().getElseFetchFromServiceEventLineUpData(this, false, competitionID, eventID);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{		
		return ContentManager.sharedInstance().getFromCacheHasLineUpDataByEventIDForSelectedCompetition(eventID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
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
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status)
	{
		super.updateUIBaseElements(status);
	
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				listContainerLayout.removeAllViews();
				
				// Line up - main
				List<EventLineUp> eventLineUps = ContentManager.sharedInstance().getFromCacheInStartingLineUpLineUpDataByEventIDForSelectedCompetition(eventID, teamID);
	
				Collections.sort(eventLineUps, new EventLineUpComparatorByPositionAndShirtNumber());
				
				listAdapter = new CompetitionEventLineUpTeamsListAdapter(activity, eventLineUps);
				
				for (int i = 0; i < listAdapter.getCount(); i++) 
				{
		            View listItem = listAdapter.getView(i, null, listContainerLayout);
		           
		            if (listItem != null) 
		            {
		            	listContainerLayout.addView(listItem);
		            }
		        }
				
				listContainerLayout.measure(0, 0);
				
				// Line up - Substitutes
				List<EventLineUp> eventLineUpsSubs = ContentManager.sharedInstance().getFromCacheSubstitutesLineUpDataByEventIDForSelectedCompetition(eventID, teamID);
				
				Collections.sort(eventLineUpsSubs, new EventLineUpComparatorByPositionAndShirtNumber());
				
				if (eventLineUpsSubs != null && !eventLineUpsSubs.isEmpty()) 
				{	
					eventListOfSubs.removeAllViews();
					
					whiteDivider.setVisibility(View.VISIBLE);
					
					StringBuilder sb = new StringBuilder();

					sb.append(activity.getResources().getString(R.string.event_page_lineup_subs_header));
					
					if (eventLineUpsSubs.size() > 1) {
						sb.append("s");
					} 
					
					subsHeader.setText(sb.toString());
					
					listAdapterSubs = new CompetitionEventLineUpTeamsListAdapter(activity, eventLineUpsSubs);
					
					for (int i = 0; i < listAdapterSubs.getCount(); i++) 
					{
			            View listItem = listAdapterSubs.getView(i, null, eventListOfSubs);
			           
			            if (listItem != null) 
			            {
			            	eventListOfSubs.addView(listItem);
			            }
			        }
					
					eventListOfSubs.measure(0, 0);
					
					subsHeader.setVisibility(View.VISIBLE);
					eventListOfSubs.setVisibility(View.VISIBLE);
				}
				else
				{
					subsHeader.setVisibility(View.GONE);
					eventListOfSubs.setVisibility(View.GONE);
				}
				
				if(getType() == EventTabTypeEnum.EVENT_LINEUP_HOME_TEAM)
				{
					// TODO Check for the + 100 if thats correct. Seems to solve the bug with the list view.
					viewPager.heightsMap.put(CompetitionEventLineupTeamsTabFragmentStatePagerAdapter.HOME_TEAM_POSITION, listContainerLayout.getMeasuredHeight() + eventListOfSubs.getMeasuredHeight() + 100);
					
					viewPager.onPageScrolled(CompetitionEventLineupTeamsTabFragmentStatePagerAdapter.HOME_TEAM_POSITION, 0, 0); //TODO: Ugly solution to viewpager not updating height on first load.
				}
				else
				{
					viewPager.heightsMap.put(CompetitionEventLineupTeamsTabFragmentStatePagerAdapter.AWAY_TEAM_POSITION, listContainerLayout.getMeasuredHeight() + eventListOfSubs.getMeasuredHeight() + 100);
				}
				
				containerLayout.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//TODO: Why is competitionID 0 here?
						long competitionIdForTracking = 0;
						if (competitionID != 0) { 
							competitionIdForTracking = competitionID;
						}
						else if (eventID != 0) {
							competitionIdForTracking = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID).getCompetitionId();
						}
						TrackingGAManager.sharedInstance().senduserCompetitionLineupPressedEvent(ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionIdForTracking).getDisplayName());
					}
				});
				
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
}
