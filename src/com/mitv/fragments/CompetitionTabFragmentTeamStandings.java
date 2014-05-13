
package com.mitv.fragments;



import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mitv.R;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.adapters.list.CompetitionStandingsByGroupListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Standings;



public class CompetitionTabFragmentTeamStandings 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	@SuppressWarnings("unused")
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private LinearLayout listContainerLayout;
	private CompetitionStandingsByGroupListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionTabFragmentTeamStandings()
	{
		super();
	}
	
	
	
	public CompetitionTabFragmentTeamStandings(String tabId, String tabTitle, EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_table, null);
		
		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_table_container);

		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasTeamsGroupedByPhaseForSelectedCompetition();
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
				Map<Long, List<Standings>> standingsByPhase = ContentManager.sharedInstance().getFromCacheAllStandingsGroupedByPhaseForSelectedCompetition();

				listAdapter = new CompetitionStandingsByGroupListAdapter(activity, standingsByPhase);
				
				for (int i = 0; i < listAdapter.getCount(); i++) 
				{
		            View listItem = listAdapter.getView(i, null, listContainerLayout);
		            
		            if (listItem != null)
		            {
		            	listContainerLayout.addView(listItem);
		            }
		        } 

				listContainerLayout.measure(0, 0);
				
				CompetitionPageActivity.viewPager.heightsMap.put(2, listContainerLayout.getMeasuredHeight());
				
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
