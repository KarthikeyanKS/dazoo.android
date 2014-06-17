
package com.mitv.fragments;



import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.adapters.list.TVGuideListAdapter;
import com.mitv.adapters.list.TVGuideTagListAdapter;
import com.mitv.asynctasks.other.RemoveAlreadyEndedBroadcastsTask;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.utilities.GenericUtils;



public class TVGuideTabFragmentBroadcast
	extends TVGuideTabFragment 
{
	private static final String TAG = TVGuideTabFragmentBroadcast.class.getName();

	// TODO - Hardcoded
	private static final String SPORTS_TAG_ID = "SPORT";
			
		
	private ListView listView;
	private TVGuideListAdapter listAdapter;
	
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private TVGuideTagListAdapter tvTagListAdapter;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public TVGuideTabFragmentBroadcast()
	{
		super();
	}
	
	
	
	public TVGuideTabFragmentBroadcast(TVTag tag)
	{
		super(tag.getId(), tag.getDisplayName(), TVGuideTabTypeEnum.PROGRAM_CATEGORY);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
			
		listView = (ListView) rootView.findViewById(R.id.fragment_tvguide_type_tag_listview);

		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		taggedBroadcasts = null;
		
		String loadingMessage = String.format(GenericUtils.getCurrentLocale(activity), "%s %s", getString(R.string.loading_message_tag), getTabTitle());
		
		setLoadingLayoutDetailsMessage(loadingMessage);
		
		ContentManager.sharedInstance().getElseBuildTaggedBroadcastsForSelectedTVDate(this, getTabTitle());
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getCacheManager().containsTVTagsAndGuideForSelectedTVDate();
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{	
		if(fetchRequestResult.wasSuccessful())
		{
			boolean noContent = true;
			
			HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = ContentManager.sharedInstance().getCacheManager().getTaggedBroadcastsForSelectedTVDate();
				
			if(taggedBroadcastForDay != null) 
			{
				taggedBroadcasts = taggedBroadcastForDay.get(getTabId());
	
				if(taggedBroadcasts != null && !taggedBroadcasts.isEmpty()) 
				{
					noContent = false;
				}
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
			case SUCCESS_WITH_NO_CONTENT: 
			{
				clearContentOnTagAndReload();
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
	
	
	
	protected void clearContentOnTagAndReload() 
	{
		final int startIndex = TVBroadcastWithChannelInfo.getClosestBroadcastIndex(taggedBroadcasts, 0);

		RemoveAlreadyEndedBroadcastsTask removeAlreadyEndedBroadcastsTask = new RemoveAlreadyEndedBroadcastsTask(taggedBroadcasts, startIndex) {
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				boolean enableCompetitionBanners = false;
				
				boolean isSportsTag = getTabId().equalsIgnoreCase(SPORTS_TAG_ID);
				
				if(isSportsTag)
				{
					enableCompetitionBanners = true;
				}
				
				tvTagListAdapter = new TVGuideTagListAdapter(activity, getTabTitle(), taggedBroadcasts, startIndex, enableCompetitionBanners);
					
				listView.setAdapter(tvTagListAdapter);
			}
		};
		
		removeAlreadyEndedBroadcastsTask.execute();

	}
	
}