
package com.mitv.models.comparators;



import java.util.ArrayList;

import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVFeedItem;



public class TVFeedItemComparatorByTime 
	extends BaseComparator<TVFeedItem> 
{
	@Override
	public int compare(TVFeedItem lhsFeedItem, TVFeedItem rhsFeedItem) 
	{
		TVBroadcast lhsBroadcast = lhsFeedItem.getBroadcast();
		if(lhsBroadcast == null) {
			ArrayList<TVBroadcastWithChannelInfo> feedItems = lhsFeedItem.getBroadcasts();
			lhsBroadcast = feedItems.get(0);
		}
		
		TVBroadcast rhsBroadcast = rhsFeedItem.getBroadcast();
		if(rhsBroadcast == null) {
			ArrayList<TVBroadcastWithChannelInfo> feedItems = rhsFeedItem.getBroadcasts();
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