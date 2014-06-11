
package com.mitv.fragments;



import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.adapters.list.TVGuideListAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVGuide;
import com.mitv.models.objects.mitvapi.TVTag;



public class TVGuideTabFragmentAllPrograms
	extends TVGuideTabFragment
{
	private static final String TAG = TVGuideTabFragmentAllPrograms.class.getName();
	
	
	private ListView listView;
	private TVGuideListAdapter listAdapter;
	
	private ArrayList<TVChannelGuide> tvChannelGuides;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public TVGuideTabFragmentAllPrograms()
	{
		super();
	}
	
	
	
	public TVGuideTabFragmentAllPrograms(TVTag tag)
	{
		super(tag.getId(), tag.getDisplayName(), TVGuideTabTypeEnum.ALL_PROGRAMS);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
		
		listView = (ListView) rootView.findViewById(R.id.tvguide_table_listview);

		setSwipeClockBar();
		
		updateSwipeClockBarWithDayAndTime();
		
		super.initRequestCallbackLayouts(rootView);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		tvChannelGuides = null;
		
		ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasTVTagsAndGuideForSelectedTVDate();
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			boolean noContent = true;

			TVGuide tvGuideForSelectedDay = ContentManager.sharedInstance().getFromCacheTVGuideForSelectedDay();

			tvChannelGuides = tvGuideForSelectedDay.getTvChannelGuides();
			
			if(tvChannelGuides != null && !tvChannelGuides.isEmpty()) 
			{
				noContent = false;
			}

			if(noContent) 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
			} 
			else 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
				TVDate tvDateSelected = ContentManager.sharedInstance().getFromCacheTVDateSelected();
					
				int selectedHour = getSelectedHour();
				
				boolean isToday = ContentManager.sharedInstance().selectedTVDateIsToday();
				
				listAdapter = new TVGuideListAdapter(activity, tvChannelGuides, tvDateSelected, selectedHour, isToday);
				
				if(listAdapter.areAdsEnabled() == false) 
				{
					setSwipeCLockbackground();
				}
					
				listView.setAdapter(listAdapter);
					
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
	
	
	
	@Override
	public void onTimeChange(int hour)
	{
		if (listAdapter != null) 
		{
			listAdapter.refreshList(hour);
		}
	}
}
