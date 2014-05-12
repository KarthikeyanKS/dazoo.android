
package com.mitv.fragments;



import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.adapters.list.CompetitionEventsByGroupListAdapter;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.utilities.SetListViewToHeightBasedOnChildren;



public class EventTabFragmentGroupStage 
	extends EventTabFragment
	implements ViewCallbackListener
{
	@SuppressWarnings("unused")
	private static final String TAG = EventTabFragmentGroupStage.class.getName();
	
	
	private LinearLayout listContainerLayout;
	private ListView listView;
	private CompetitionEventsByGroupListAdapter listAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public EventTabFragmentGroupStage()
	{
		super();
	}

	
	
	public EventTabFragmentGroupStage(String tabId, String tabTitle, EventTabTypeEnum tabType)
	{
		super(tabId, tabTitle, tabType);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_table, null);
		
//		listContainerLayout =  (LinearLayout) rootView.findViewById(R.id.competition_table_container);
		listView = (ListView) rootView.findViewById(R.id.competition_table_listview);

		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();
		
		//listContainerLayout.measure(0, 0);
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
				Map<Long, List<Event>> eventsByGroups = ContentManager.sharedInstance().getFromCacheAllEventsGroupedByGroupStageForSelectedCompetition();

				listAdapter = new CompetitionEventsByGroupListAdapter(activity, eventsByGroups);
				
				listView.setAdapter(listAdapter);
				
				SetListViewToHeightBasedOnChildren.setListViewHeightBasedOnChildren(listView);
				
				listAdapter.notifyDataSetChanged();
				
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
