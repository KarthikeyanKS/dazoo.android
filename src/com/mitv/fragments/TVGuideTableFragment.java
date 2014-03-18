
package com.mitv.fragments;



import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.SwipeClockTimeSelectedCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.listadapters.TVGuideListAdapter;
import com.mitv.listadapters.TVGuideTagListAdapter;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVDate;
import com.mitv.models.TVGuide;
import com.mitv.models.TVTag;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.elements.SwipeClockBar;



public class TVGuideTableFragment 
	extends BaseFragment 
	implements ViewCallbackListener, SwipeClockTimeSelectedCallbackListener
{
	private static final String TAG = TVGuideTableFragment.class.getName();

	
	private Activity activity;
	private View rootView;
	private ListView tvGuideListView;
	private ArrayList<TVChannelGuide> tvChannelGuides;
	private SwipeClockBar swipeClockBar;
	private TVGuideListAdapter tvGuideListAdapter;
	
	
	private String tvTagDisplayName;
	private String tvTagIdAsString;
	private int hour;
	private boolean isToday;
	
	
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private TVGuideTagListAdapter tvTagListAdapter;
		
	public static TVGuideTableFragment newInstance(TVTag tag, TVDate date)
	{
		TVGuideTableFragment fragment = new TVGuideTableFragment();
		
		Bundle bundle = new Bundle();
		
		bundle.putString(Constants.FRAGMENT_EXTRA_TAG_DISPLAY_NAME, tag.getDisplayName());
		
		bundle.putString(Constants.FRAGMENT_EXTRA_TAG_ID, tag.getId());
		
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	
	
	private void updateSwipeClockBarWithDayAndTime() 
	{
		if(swipeClockBar != null) 
		{
			isToday = ContentManager.sharedInstance().selectedTVDateIsToday();
			
			if(isToday) 
			{
				hour = ContentManager.sharedInstance().getFromCacheSelectedHour();
			} 
			else 
			{
				hour = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();
			}

			swipeClockBar.setHour(hour);
			swipeClockBar.setToday(isToday);
		}
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		Bundle bundle = getArguments();
		
		tvTagDisplayName = bundle.getString(Constants.FRAGMENT_EXTRA_TAG_DISPLAY_NAME);
		
		Log.d(TAG, "Fragment view " + tvTagDisplayName + " was created");
		
		tvTagIdAsString = bundle.getString(Constants.FRAGMENT_EXTRA_TAG_ID);

		if(isAllCategoriesTag())
		{
			rootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
			tvGuideListView = (ListView) rootView.findViewById(R.id.tvguide_table_listview);

			swipeClockBar = (SwipeClockBar) rootView.findViewById(R.id.tvguide_swype_clock_bar);
			FontTextView selectedHourTextView = (FontTextView) rootView.findViewById(R.id.timebar_selected_hour_textview);
			swipeClockBar.setSelectedHourTextView(selectedHourTextView);
			swipeClockBar.setTimeSelectedListener(this);
			updateSwipeClockBarWithDayAndTime();
		} 
		else
		{
			rootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
			tvGuideListView = (ListView) rootView.findViewById(R.id.fragment_tvguide_type_tag_listview);
		}
		
		super.initRequestCallbackLayouts(rootView);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		Log.d(TAG, "Fragment view " + tvTagDisplayName + " was destroyed,");
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();

		updateSwipeClockBarWithDayAndTime();
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		tvChannelGuides = null;
		taggedBroadcasts = null;

		if (isAllCategoriesTag())
		{
			ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
		} 
		else 
		{
			ContentManager.sharedInstance().getElseBuildTaggedBroadcastsForSelectedTVDate(this, tvTagDisplayName);
		}
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasTVGuideForSelectedTVDate();
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{	
		switch(fetchRequestResult)
		{
			case SUCCESS:
			{
				if (isAllCategoriesTag())
				{
					TVGuide tvGuideForSelectedDay = ContentManager.sharedInstance().getFromCacheTVGuideForSelectedDay();
					
					tvChannelGuides = tvGuideForSelectedDay.getTvChannelGuides();
				} 
				else
				{
					HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = ContentManager.sharedInstance().getFromCacheTaggedBroadcastsForSelectedTVDate();
					
					taggedBroadcasts = taggedBroadcastForDay.get(tvTagIdAsString);
				}
				
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				break;
			}
			
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}

	private boolean isAllCategoriesTag() {
		boolean isAllCategoriesTag = tvTagIdAsString.equals(Constants.ALL_CATEGORIES_TAG_ID);
		return isAllCategoriesTag;
	}
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCESS_WITH_CONTENT:
			{	
				if(isAllCategoriesTag()) 
				{
					TVDate tvDateSelected = ContentManager.sharedInstance().getFromCacheTVDateSelected();
					tvGuideListAdapter = new TVGuideListAdapter(activity, tvChannelGuides, tvDateSelected, hour, isToday);
				
					tvGuideListView.setAdapter(tvGuideListAdapter);
					
					tvGuideListAdapter.notifyDataSetChanged();
					Log.d(TAG, "PROFILING: updateUI:SUCCEEDED_WITH_DATA");
				}
				else 
				{
					int startIndex = TVBroadcastWithChannelInfo.getClosestBroadcastIndex(taggedBroadcasts, 0);

					RemoveAlreadyEndedBroadcastsTask removeAlreadyEndedBroadcastsTask = new RemoveAlreadyEndedBroadcastsTask(startIndex);
					removeAlreadyEndedBroadcastsTask.run();
					
					tvTagListAdapter = new TVGuideTagListAdapter(activity, tvTagDisplayName, taggedBroadcasts, startIndex);
						
					tvGuideListView.setAdapter(tvTagListAdapter);
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
	
	
	
	private class RemoveAlreadyEndedBroadcastsTask
		implements Runnable
	{
		private int startIndex;
		
		
		
		public RemoveAlreadyEndedBroadcastsTask(int startIndex)
		{
			this.startIndex = startIndex;
		}
		
		
		
		@Override
		public void run() 
		{
			ArrayList<TVBroadcast> tvBroadcastsToRemove = new ArrayList<TVBroadcast>();

			if(taggedBroadcasts != null && startIndex >= 0)
			{
				for (int i = startIndex; i < taggedBroadcasts.size(); i++)
				{
					boolean hasEnded = taggedBroadcasts.get(i).hasEnded();

					if(hasEnded) 
					{
						tvBroadcastsToRemove.add(taggedBroadcasts.get(i));
					}
				}

				for (TVBroadcast broadcast : tvBroadcastsToRemove) 
				{
					taggedBroadcasts.remove(broadcast);
				}
			}
		}
	}

	

	@Override
	public void onTimeChange(int hour)
	{
		if (tvGuideListAdapter != null) 
		{
			tvGuideListAdapter.refreshList(hour);
		}
	}
}