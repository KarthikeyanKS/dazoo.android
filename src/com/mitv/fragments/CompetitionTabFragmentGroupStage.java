
package com.mitv.fragments;



import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.adapters.list.CompetitionEventsByGroupListAdapter;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.ui.elements.CustomViewPager;



public class CompetitionTabFragmentGroupStage 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	@SuppressWarnings("unused")
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private long competitionID;
	
	private CustomViewPager viewPager;
	private LinearLayout listContainerLayout;
	private CompetitionEventsByGroupListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionTabFragmentGroupStage()
	{
		super();
	}

	
	
	public CompetitionTabFragmentGroupStage(
			final CustomViewPager viewPager,
			final long competitionID, 
			final String tabId, 
			final String tabTitle, 
			final EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
		
		this.viewPager = viewPager;
		this.competitionID = competitionID;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_tab_fragment_container, null);
		
		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_table_container);

		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
        	competitionID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_ID, 0);
        }
		
		return rootView;
	}
	
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        
        outState.putLong(Constants.INTENT_COMPETITION_ID, competitionID);
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
		return ContentManager.sharedInstance().getFromCacheHasEventsGroupedByPhaseForSelectedCompetition();
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
				
				Map<Long, List<Event>> eventsByGroups = ContentManager.sharedInstance().getFromCacheAllEventsGroupedByGroupStageForSelectedCompetition();

				listAdapter = new CompetitionEventsByGroupListAdapter(activity, eventsByGroups);
				
				for (int i = 0; i < listAdapter.getCount(); i++) 
				{
		            View listItem = listAdapter.getView(i, null, listContainerLayout);
		           
		            if (listItem != null) 
		            {
		            	listContainerLayout.addView(listItem);
		            }
		        }
				
				listContainerLayout.measure(0, 0);
				
				viewPager.heightsMap.put(CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION, listContainerLayout.getMeasuredHeight());
				if (viewPager.getCurrentItem() == CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION) {
					viewPager.onPageScrolled(CompetitionTabFragmentStatePagerAdapter.GROUP_STAGE_POSITION, 0, 0);
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
}
