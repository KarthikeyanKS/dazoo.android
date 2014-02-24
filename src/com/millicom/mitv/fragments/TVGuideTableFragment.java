
package com.millicom.mitv.fragments;



import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.TVTag;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.adapters.AdListAdapter;
import com.mitv.adapters.TVGuideListAdapter;
import com.mitv.adapters.TVGuideTagListAdapter;
import com.mitv.customviews.SwipeClockBar;



public class TVGuideTableFragment 
	extends BaseFragment 
	implements ActivityCallbackListener
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
	private boolean isToday = false;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private TVGuideTagListAdapter tvTagListAdapter;
	private int hour;
	public HashMap<String, AdListAdapter> adapterMap;
	private BroadcastReceiver broadcastReceiverClock;

	
	
	private void initBroadcastReceivers() 
	{
		broadcastReceiverClock = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				if (intent.getAction() != null && intent.getAction().equals(Consts.INTENT_EXTRA_CLOCK_SELECTION)) 
				{
					hour = intent.getExtras().getInt(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE);
					
					if (tvGuideListAdapter != null) 
					{
						tvGuideListAdapter.refreshList(Integer.valueOf(hour));
					}
				}
			}
		};
	}

	private void registerBroadcastReceivers() 
	{
		LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiverClock, new IntentFilter(Consts.INTENT_EXTRA_CLOCK_SELECTION));
	}

	
	
	private void unregisterBroadcastReceivers() 
	{
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiverClock);
	}

	
	
	public static TVGuideTableFragment newInstance(TVTag tag, TVDate date, HashMap<String, AdListAdapter> adapterMap)
	{
		TVGuideTableFragment fragment = new TVGuideTableFragment();
		
		fragment.adapterMap = adapterMap;
		
		Bundle bundle = new Bundle();
		
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG_DISPLAY_NAME, tag.getDisplayName());
		
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG_ID, tag.getId());
		
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		initBroadcastReceivers();
	}
	
	
	
	@Override
	public void onResume() 
	{
		registerBroadcastReceivers();
		
		super.onResume();
	}
	
	
	
	@Override
	public void onPause() 
	{
		unregisterBroadcastReceivers();
		
		super.onPause();
	}

	
	
	@Override
	public void onDetach() 
	{
		super.onDetach();
		
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiverClock);
	}

	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiverClock);
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		Bundle bundle = getArguments();
		
		tvTagDisplayName = bundle.getString(Consts.FRAGMENT_EXTRA_TAG_DISPLAY_NAME);
		
		tvTagIdAsString = bundle.getString(Consts.FRAGMENT_EXTRA_TAG_ID);

		if (getResources().getString(R.string.all_categories_name).equals(tvTagDisplayName)) 
		{
			hour = ContentManager.sharedInstance().getFromStorageSelectedHour();

			rootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
			tvGuideListView = (ListView) rootView.findViewById(R.id.tvguide_table_listview);

			swipeClockBar = (SwipeClockBar) rootView.findViewById(R.id.tvguide_swype_clock_bar);
			swipeClockBar.setHour(hour);

			// TODO figure this out?
			swipeClockBar.setToday(isToday);

			registerBroadcastReceivers();
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
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult) 
	{	
		switch(fetchRequestResult)
		{
			case SUCCESS:
			{
				if (getResources().getString(R.string.all_categories_name).equals(tvTagDisplayName)) 
				{
					TVGuide tvGuideForSelectedDay = ContentManager.sharedInstance().getFromStorageTVGuideForSelectedDay();
					tvChannelGuides = tvGuideForSelectedDay.getTvChannelGuides();
				} 
				else 
				{	
					HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = ContentManager.sharedInstance().getFromStorageTaggedBroadcastsForSelectedTVDate();
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
						TVDate tvDateSelected = ContentManager.sharedInstance().getFromStorageTVDateSelected();
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

					if (tvTagListAdapter == null) {
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
}