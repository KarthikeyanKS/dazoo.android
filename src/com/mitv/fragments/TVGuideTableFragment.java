
package com.mitv.fragments;



import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
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
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.SwipeClockTimeSelectedCallbackListener;
import com.mitv.listadapters.AdListAdapter;
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
	implements ActivityCallbackListener, SwipeClockTimeSelectedCallbackListener
{
	@SuppressWarnings("unused")
	private static final String TAG = TVGuideTableFragment.class.getName();

	
	private String tvTagDisplayName;
	private View rootView;
	private Activity activity;
	private ListView tvGuideListView;
	private ArrayList<TVChannelGuide> tvChannelGuides;
	private String tvTagIdAsString;
	private SwipeClockBar swipeClockBar;
	private TVGuideListAdapter tvGuideListAdapter;
	private boolean isToday;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private TVGuideTagListAdapter tvTagListAdapter;
	private int hour;
	@SuppressWarnings("rawtypes")
	public HashMap<String, AdListAdapter> adapterMap;


	
	public static TVGuideTableFragment newInstance(TVTag tag, TVDate date)
	{
		TVGuideTableFragment fragment = new TVGuideTableFragment();
		
		fragment.adapterMap = new HashMap<String, AdListAdapter>();//ContentManager.sharedInstance().getFromCacheAdapterMap();
		
		Bundle bundle = new Bundle();
		
		bundle.putString(Constants.FRAGMENT_EXTRA_TAG_DISPLAY_NAME, tag.getDisplayName());
		
		bundle.putString(Constants.FRAGMENT_EXTRA_TAG_ID, tag.getId());
		
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();

		updateSwipeClockBarWithDayAndTime();
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
		
		tvTagIdAsString = bundle.getString(Constants.FRAGMENT_EXTRA_TAG_ID);

		if (getResources().getString(R.string.all_categories_name).equals(tvTagDisplayName)) 
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

		// reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		tvChannelGuides = null;
		taggedBroadcasts = null;

		if (getResources().getString(R.string.all_categories_name).equals(tvTagDisplayName)) 
		{
			ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
		} 
		else 
		{
			ContentManager.sharedInstance().getElseFetchFromServiceTaggedBroadcastsForSelectedTVDate(this, false);
		}
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{	
		switch(fetchRequestResult)
		{
			case SUCCESS:
			{
				if (getResources().getString(R.string.all_categories_name).equals(tvTagDisplayName)) 
				{
					TVGuide tvGuideForSelectedDay = ContentManager.sharedInstance().getFromCacheTVGuideForSelectedDay();
					tvChannelGuides = tvGuideForSelectedDay.getTvChannelGuides();
				} 
				else 
				{	
					HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = ContentManager.sharedInstance().getFromCacheTaggedBroadcastsForSelectedTVDate();
					taggedBroadcasts = taggedBroadcastForDay.get(tvTagIdAsString);
				}
				updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
				break;
			}
			
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}

	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				if (getResources().getString(R.string.all_categories_name).equals(tvTagDisplayName)) 
				{
					tvGuideListAdapter = (TVGuideListAdapter) adapterMap.get(tvTagDisplayName);
					
					if (tvGuideListAdapter == null) 
					{
						TVDate tvDateSelected = ContentManager.sharedInstance().getFromCacheTVDateSelected();
						tvGuideListAdapter = new TVGuideListAdapter(activity, tvChannelGuides, tvDateSelected, hour, isToday);
						adapterMap.put(tvTagDisplayName, tvGuideListAdapter);
					}

					tvGuideListView.setAdapter(tvGuideListAdapter);
					tvGuideListAdapter.notifyDataSetChanged();
				} 
				else 
				{
					final int index = TVBroadcastWithChannelInfo.getClosestBroadcastIndex(taggedBroadcasts, 0);

					// Remove all broadcasts that already ended
					new Runnable() {

						@Override
						public void run() {
							ArrayList<TVBroadcast> toRemove = new ArrayList<TVBroadcast>();

							if(taggedBroadcasts != null) {
								for (int i = index; i < taggedBroadcasts.size(); i++) {
									if (index < taggedBroadcasts.size() - 1 && index >= 0) {
										if (taggedBroadcasts.get(i).hasEnded()) {
											toRemove.add(taggedBroadcasts.get(i));
										}
									}
								}
	
								for (TVBroadcast broadcast : toRemove) {
									taggedBroadcasts.remove(broadcast);
								}
							}
						}
					}.run();

					tvTagListAdapter = (TVGuideTagListAdapter) adapterMap.get(tvTagDisplayName);

					if (tvTagListAdapter == null) 
					{
						tvTagListAdapter = new TVGuideTagListAdapter(activity, tvTagDisplayName, taggedBroadcasts, index);
						
						adapterMap.put(tvTagDisplayName, tvTagListAdapter);
					}

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


	@Override
	public void onTimeChange(int hour) {
		if (tvGuideListAdapter != null) 
		{
			tvGuideListAdapter.refreshList(hour);
		}

	}
}