
package com.mitv.models.comparators;



import java.util.List;

import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVFeedItem;



public class TVFeedItemComparatorByTime 
	extends BaseComparator<TVFeedItem> 
{
	@Override
	public int compare(TVFeedItem lhsFeedItem, TVFeedItem rhsFeedItem) 
	{
		TVBroadcast lhsBroadcast = lhsFeedItem.getBroadcast();
		
		if(lhsBroadcast == null) 
		{
			List<TVBroadcastWithChannelInfo> feedItems = lhsFeedItem.getBroadcasts();
			
			lhsBroadcast = feedItems.get(0);
		}
		
		TVBroadcast rhsBroadcast = rhsFeedItem.getBroadcast();
		
		if(rhsBroadcast == null) 
		{
			List<TVBroadcastWithChannelInfo> feedItems = rhsFeedItem.getBroadcasts();
			
			rhsBroadcast = feedItems.get(0);
		}
		
		long left = lhsBroadcast.getBeginTimeMillis();
		long right = rhsBroadcast.getBeginTimeMillis();
	
		if (left > right) 
		{
			return 1;
		} 
		else if (left < right) 
		{
			return -1;
		} 
		else 
		{
			String leftProgramName = lhsBroadcast.getProgram().getTitle();
			String rightProgramName = rhsBroadcast.getProgram().getTitle();
			
			return leftProgramName.compareTo(rightProgramName);
		}
	}
}