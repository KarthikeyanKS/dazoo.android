
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
import com.mitv.adapters.list.CompetitionEventHighlightsListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;



public class CompetitionEventTabFragmentHighlights 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private Event event;
	
	private long eventID;
	
	private LinearLayout listContainerLayout;
	private CompetitionEventHighlightsListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionEventTabFragmentHighlights()
	{
		super();
	}
	
	
	
	public CompetitionEventTabFragmentHighlights(long eventID, String tabId, String tabTitle, EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
		
		this.eventID = eventID;
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Event event = getEvent();
		
		boolean isEventLive = event.isLive();
		
		if(isEventLive)
		{
			int reloadIntervalInSeconds = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageHighlightReloadInterval();
		
			setBackgroundLoadTimerValueInSeconds(reloadIntervalInSeconds);
		}
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_event_tab_fragment_container, null);
		
		listContainerLayout = (LinearLayout) rootView.findViewById(R.id.competition_event_table_container_layout);
	
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
		
		String emptyContentString = getString(R.string.competition_event_highlights_no_content_text);
		
		setEmptyLayoutDetailsMessage(emptyContentString);
		
		String loadingString = getString(R.string.competition_event_highlights_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		long competitionID = getEvent().getCompetitionId();
		
		long eventID = getEvent().getEventId();
		
		ContentManager.sharedInstance().getElseFetchFromServiceEventHighlighstData(this, false, competitionID, eventID);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		long competitionID = getEvent().getCompetitionId();
		
		long eventID = getEvent().getEventId();
		
		ContentManager.sharedInstance().getElseFetchFromServiceEventHighlighstData(this, false, competitionID, eventID);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		long eventID = getEvent().getEventId();
		
		return ContentManager.sharedInstance().getFromCacheHasHighlightsDataByEventIDForSelectedCompetition(eventID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			long eventID = getEvent().getEventId();
			
			List<EventHighlight> eventHighlights = ContentManager.sharedInstance().getFromCacheHighlightsDataByEventIDForSelectedCompetition(eventID, true);
			
			if(eventHighlights.isEmpty() == false)
			{
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
				
				List<EventHighlight> eventHighlights = ContentManager.sharedInstance().getFromCacheHighlightsDataByEventIDForSelectedCompetition(eventID, true);
	
				listAdapter = new CompetitionEventHighlightsListAdapter(activity, eventHighlights);
				
				for (int i = 0; i < listAdapter.getCount(); i++)
				{
		            View listItem = listAdapter.getView(i, null, listContainerLayout);
		           
		            if (listItem != null) 
		            {
		            	listContainerLayout.addView(listItem);
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
