
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
import com.mitv.adapters.list.CompetitionStandingsByGroupListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.ui.elements.CustomViewPager;



public class CompetitionTabFragmentTeamStandings 
	extends CompetitionTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = CompetitionTabFragmentGroupStage.class.getName();
	
	
	private long competitionID;
	
	private CustomViewPager viewPager;
	private LinearLayout listContainerLayout;
	private CompetitionStandingsByGroupListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionTabFragmentTeamStandings()
	{
		super();
	}
	
	
	
	public CompetitionTabFragmentTeamStandings(
			final CustomViewPager viewPager,
			final long competitionID, 
			final String tabId, 
			final String tabTitle,
			final EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
		
		this.competitionID = competitionID;
		
		this.viewPager = viewPager;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_tab_fragment_container, null);
		
		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_table_container);

		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID);
		
		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
        	competitionID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_ID, 0);
        }
		
		setEmptyLayoutDetailsMessage(getString(R.string.competition_standings_no_data_text));
		
		return rootView;
	}
	
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        
        outState.putLong(Constants.INTENT_COMPETITION_ID, competitionID);
    }
	
	

	@Override
	public void onResume()
	{
		super.onResume();
		
		Log.w(TAG, "Something");
	}
	
	
	
	@Override
	protected void loadData()
	{
		List<Phase> phases = ContentManager.sharedInstance().getFromCacheAllPhasesForSelectedCompetition();
		
		if(phases.isEmpty() == false)
		{
			updateUI(UIStatusEnum.LOADING);
			
			String loadingString = getString(R.string.competition_standings_loading_text);
			
			setLoadingLayoutDetailsMessage(loadingString);
			
			ContentManager.sharedInstance().getElseFetchFromServiceStandingsForMultiplePhases(this, false, phases);
		}
		else
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
		}
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasCompetitionData(competitionID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			switch(requestIdentifier)
			{
				case COMPETITION_INITIAL_DATA:
				{
					this.loadData();
					break;
				}
				
				case COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID:
				{
					Map<Long, List<Standings>> standingsByPhase = ContentManager.sharedInstance().getFromCacheAllStandingsGroupedByPhaseForSelectedCompetition();
					
					if(standingsByPhase.isEmpty())
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
					}
					else
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					}
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unhandled request identifier");
					break;
				}
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
				listContainerLayout.removeAllViews();
				
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
				
				viewPager.heightsMap.put(2, listContainerLayout.getMeasuredHeight());
				
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
