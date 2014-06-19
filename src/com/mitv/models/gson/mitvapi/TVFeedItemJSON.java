
package com.mitv.models.gson.mitvapi;



import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class TVFeedItemJSON
{
	private static final String	TAG	= TVFeedItemJSON.class.getName();
	
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String itemType;
	
	private String title;
	
	/* This variable is used if itemType == "BROADCAST" or itemType == "RECOMMENDED_BROADCAST" or itemType == "POPULAR_BROADCAST" or itemType == "POPULAR_TWITTER" */
	private TVBroadcastWithChannelInfo broadcast;
	
	/* This variable is used if itemType == "POPULAR_BROADCASTS" */
	private List<TVBroadcastWithChannelInfo> broadcasts;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public TVFeedItemJSON(){}

	

	public String getTitle()
	{
		if(title == null)
		{
			title = "";
			
			Log.w(TAG, "title is null");
		}
		
		return title;
	}



	public TVBroadcastWithChannelInfo getBroadcast()
	{
		if(broadcast == null)
		{
			broadcast = new TVBroadcastWithChannelInfo();
			
			Log.w(TAG, "broadcast is null");
		}
		
		return broadcast;
	}
	
	
	
	protected String getItemTypeString()
	{
		if(itemType == null)
		{
			itemType = "";
			
			Log.w(TAG, "itemType is null");
		}
		
		return itemType;
	}
	
	
	
	public List<TVBroadcastWithChannelInfo> getBroadcasts()
	{
		if(broadcasts == null)
		{
			broadcasts = Collections.emptyList();
			
			Log.w(TAG, "broadcasts is null");
		}
		
		return broadcasts;
	}
}