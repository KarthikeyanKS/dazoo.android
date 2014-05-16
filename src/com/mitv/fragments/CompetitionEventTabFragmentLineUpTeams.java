
package com.mitv.fragments;



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
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.adapters.list.CompetitionEventLineUpTeamsListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;



public class CompetitionEventTabFragmentLineUpTeams 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private Event event;
	
	private long eventID;

	private View whiteDivider;
	private TextView subsHeader;
	private LinearLayout eventListOfSubs;
	
	private LinearLayout listContainerLayout;
	private CompetitionEventLineUpTeamsListAdapter listAdapter;
	private CompetitionEventLineUpTeamsListAdapter listAdapterSubs;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionEventTabFragmentLineUpTeams()
	{
		super();
	}
	
	
	
	public CompetitionEventTabFragmentLineUpTeams(long eventID, String tabId, String tabTitle, EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
		
		this.eventID = eventID;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_event_tab_fragment_container, null);
		
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
        }
		
		return rootView;
	}
	
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        
        outState.putLong(Constants.INTENT_COMPETITION_EVENT_ID, eventID);
    }
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_event_lineup_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		long competitionID = getEvent().getCompetitionId();
		
		long eventID = getEvent().getEventId();
		
		ContentManager.sharedInstance().getElseFetchFromServiceEventLineUpData(this, false, competitionID, eventID);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		long eventID = getEvent().getEventId();
		
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
				long eventID = getEvent().getEventId();
				
				// Line up
				List<EventLineUp> eventLineUps = ContentManager.sharedInstance().getFromCacheInStartingLineUpLineUpDataByEventIDForSelectedCompetition(eventID);
	
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
				List<EventLineUp> eventLineUpsSubs = ContentManager.sharedInstance().getFromCacheSubstitutesLineUpDataByEventIDForSelectedCompetition(eventID);
				
				if (eventLineUpsSubs != null && !eventLineUpsSubs.isEmpty()) {
					
					whiteDivider.setVisibility(View.VISIBLE);
					
					StringBuilder sb = new StringBuilder();
					
					if (eventLineUpsSubs.size() > 1) {
						sb.append(getString(R.string.event_page_lineup_subs_header))
							.append("s");
					} else {
						sb.append(getString(R.string.event_page_lineup_subs_header));
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
					
					//TODO: This is not using the correct id for this tab.
					EventPageActivity.viewPagerForLineupTeams.heightsMap.put(0, listContainerLayout.getMeasuredHeight() + eventListOfSubs.getMeasuredHeight());
					EventPageActivity.viewPagerForLineupTeams.onPageScrolled(0, 0, 0); //TODO: Ugly solution to viewpager not updating height on first load.
					
					subsHeader.setVisibility(View.VISIBLE);
					eventListOfSubs.setVisibility(View.VISIBLE);
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
	
	
	
	private Event getEvent()
	{
		if(this.event == null)
		{
			Log.d(TAG, "Event ID is: " + eventID);
			
			this.event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		}
		
		return event;
	}
}
