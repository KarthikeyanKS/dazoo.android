
package com.mitv.fragments;



import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.adapters.list.CompetitionTeamsByGroupListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Team;



public class EventTabFragmentTeams 
	extends EventTabFragment
	implements ViewCallbackListener
{
	private static final String TAG = EventTabFragmentGroupStage.class.getName();
	
	
	private ListView listView;
	private CompetitionTeamsByGroupListAdapter listAdapter;
	
	
	
	public EventTabFragmentTeams(String tabId, String tabTitle, EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_table, null);
		
		listView = (ListView) rootView.findViewById(R.id.competition_table_listview);

		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		// TODO - Implement me
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		// TODO - Implement me
		return true;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			// TODO - Implement me
			
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
				Map<String, List<Team>> teamsByGroup = ContentManager.sharedInstance().getFromCacheAllTeamsGroupedByPhaseForSelectedCompetition();

				listAdapter = new CompetitionTeamsByGroupListAdapter(activity, teamsByGroup);
				
				listView.setAdapter(listAdapter);
					
				listAdapter.notifyDataSetChanged();
					
				Log.d(TAG, "PROFILING: updateUI:SUCCEEDED_WITH_DATA");
					
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
