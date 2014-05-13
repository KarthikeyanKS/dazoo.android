
package com.mitv.fragments;



import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.adapters.list.CompetitionEventStandingsListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Standings;



public class CompetitionEventTabFragmentGroupStandings 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private Event event;
	
	private long eventID;
	
	private LinearLayout listContainerLayout;
	private CompetitionEventStandingsListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionEventTabFragmentGroupStandings()
	{
		super();
	}
	
	
	
	public CompetitionEventTabFragmentGroupStandings(long eventID, String tabId, String tabTitle, EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
		
		this.eventID = eventID;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_table, null);
		
		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_table_container);
	
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
		
		long competitionID = getEvent().getCompetitionId();
		
		long phaseID = getEvent().getPhaseId();
		
		ContentManager.sharedInstance().getElseFetchFromServiceEventStandingsData(this, false, competitionID, phaseID);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		long phaseID = getEvent().getPhaseId();
		
		return ContentManager.sharedInstance().getFromCacheHasStandingsForPhaseInSelectedCompetition(phaseID);
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
				long phaseID = getEvent().getPhaseId();
				
				List<Standings> standings = ContentManager.sharedInstance().getFromCacheStandingsForPhaseInSelectedCompetition(phaseID);
	
				listAdapter = new CompetitionEventStandingsListAdapter(activity, standings);
				
				for (int i = 0; i < listAdapter.getCount(); i++) 
				{
		            View listItem = listAdapter.getView(i, null, listContainerLayout);
		           
		            if (listItem != null) 
		            {
		            	listContainerLayout.addView(listItem);
		            }
		        }
				
				listContainerLayout.measure(0, 0);
				
				CompetitionPageActivity.viewPager.heightsMap.put(1, listContainerLayout.getMeasuredHeight());
				
				CompetitionPageActivity.viewPager.onPageScrolled(1, 0, 0); //TODO: Ugly solution to viewpager not updating height on first load.
				
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
