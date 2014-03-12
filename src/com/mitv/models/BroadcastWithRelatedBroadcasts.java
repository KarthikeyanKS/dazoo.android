package com.mitv.models;

import java.util.ArrayList;

/**
 * The purpose of this class is to act as a super class to the classes
 * UpcomingBroadcastsForBroascast and RepeatingBroadcastsForBroadcasts
 * @author consultant_hdme
 *
 */
public class BroadcastWithRelatedBroadcasts 
{
	protected ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts;
	
	public BroadcastWithRelatedBroadcasts(ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) 
	{
		this.relatedBroadcasts = relatedBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getRelatedBroadcasts() 
	{
		return relatedBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getRelatedBroadcastsWithExclusions(TVBroadcast tvBroadcast) 
	{
		TVBroadcastWithChannelInfo TVBroadcastWithChannelInfoToExclude = null;
		
		for(TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo : relatedBroadcasts)
		{
			if(tvBroadcastWithChannelInfo.getBeginTimeMillis().equals(tvBroadcast.getBeginTimeMillis()))
			{
				TVBroadcastWithChannelInfoToExclude = tvBroadcastWithChannelInfo;
			}
		}
		
		if(TVBroadcastWithChannelInfoToExclude != null)
		{
			ArrayList<TVBroadcastWithChannelInfo> tvBroadcastWithChannelInfoList = new ArrayList<TVBroadcastWithChannelInfo>(relatedBroadcasts);
			tvBroadcastWithChannelInfoList.remove(TVBroadcastWithChannelInfoToExclude);
			
			return tvBroadcastWithChannelInfoList;
		}
		else
		{
			return relatedBroadcasts;
		}
	}
}
