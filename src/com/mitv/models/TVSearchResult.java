
package com.mitv.models;



import java.util.ArrayList;

import com.mitv.models.gson.TVSearchResultJSON;



public class TVSearchResult 
	extends TVSearchResultJSON 
{
	private static String searchQuery;
	
	
	
	public TVBroadcastWithChannelInfo getNextBroadcast() 
	{
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = null;
		
		ArrayList<TVBroadcastWithChannelInfo> broadcasts = getEntity().getBroadcasts();
		
		if(broadcasts != null) 
		{
			if(broadcasts.size() > 0) 
			{
				broadcastWithChannelInfo = broadcasts.get(0);
			}
		}
	
		return broadcastWithChannelInfo;
	}
	
	
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