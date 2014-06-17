
package com.mitv.fragments;



import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.adapters.list.CompetitionEventEventsByGroupListAdapter;
import com.mitv.adapters.pager.CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.ui.elements.CustomViewPager;



public class CompetitionEventTabFragmentGroupStage 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private Event event;
	
	private long eventID;
	private long phaseId;
	
	private CustomViewPager viewPager;
	private LinearLayout listContainerLayout;
	private CompetitionEventEventsByGroupListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionEventTabFragmentGroupStage()
	{
		super();
	}
	
	
	
	public CompetitionEventTabFragmentGroupStage(
			final CustomViewPager viewPager,
			final long eventID, 
			final String tabId,
			final String tabTitle,
			final EventTabTypeEnum tabType,
			final long phaseId)
	{
		super(tabId, tabTitle, tabType);
		
		this.viewPager = viewPager;
		this.eventID = eventID;
		this.phaseId = phaseId;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_event_tab_fragment_container, null);
		
		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_event_table_container_layout);
	
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
		updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getCacheManager().containsEventsGroupedByPhaseForSelectedCompetition();
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
				
				List<Event> events = ContentManager.sharedInstance().getCacheManager().getEventsForPhaseInSelectedCompetition(phaseId);
	
				String viewBottomMessage = getString(R.string.event_page_groups_list_show_more);
				
				Runnable procedure = getNavigateToCompetitionPageProcedure();
				
				listAdapter = new CompetitionEventEventsByGroupListAdapter(activity, events, true, viewBottomMessage, procedure);
				
				for (int i = 0; i < listAdapter.getCount(); i++) 
				{
		            View listItem = listAdapter.getView(i, null, listContainerLayout);
		           
		            if (listItem != null) 
		            {
		            	listContainerLayout.addView(listItem);
		            }
		        }
				
				listContainerLayout.measure(0, 0);
				
				viewPager.heightsMap.put(CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION, listContainerLayout.getMeasuredHeight() + 200);
				
				viewPager.onPageScrolled(CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION, 0, 0);
				
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
			
			this.event = ContentManager.sharedInstance().getCacheManager().getEventByIDForSelectedCompetition(eventID);
		}
		
		return event;
	}
	
	
	
	private Runnable getNavigateToCompetitionPageProcedure()
	{
		return new Runnable() 
		{
			public void run() 
			{
				Intent intent = new Intent(activity, CompetitionPageActivity.class);			
				
				intent.putExtra(Constants.INTENT_COMPETITION_ID, getEvent().getCompetitionId());
                intent.putExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION);
                
				activity.startActivity(intent);
			}
		};
	}
}
