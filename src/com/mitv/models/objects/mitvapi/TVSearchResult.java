
package com.mitv.models.objects.mitvapi;



import java.util.List;

import com.mitv.models.gson.mitvapi.TVSearchResultJSON;



public class TVSearchResult 
	extends TVSearchResultJSON 
{
	private static String searchQuery;
	
	
	
	public TVBroadcastWithChannelInfo getNextBroadcast() 
	{
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = null;
		
		List<TVBroadcastWithChannelInfo> broadcasts = getEntity().getBroadcasts();
		
		if(broadcasts != null) 
		{
			if(broadcasts.size() > 0) 
			{
				broadcastWithChannelInfo = broadcasts.get(0);
			}
		}
	
		return broadcastWithChannelInfo;
	}
	
	
	
	/* The toString representation will always be the original search query */
	@Override
	public String toString()
	{
		if(searchQuery == null)
		{
			return "";
		}
		else
		{
			return searchQuery;
		}
	}

	

	public static void setSearchQuery(String search) 
	{
		searchQuery = search;
	}
}