
package com.mitv.models.gson.mitvapi;



import java.util.List;

import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class TVFeedItemJSON
{
	@SuppressWarnings("unused")
	private static final String	TAG	= TVFeedItemJSON.class.getName();
	
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */

	protected String itemType;
	
	protected String title;
	
	/* This variable is used if itemType == "BROADCAST" or itemType == "RECOMMENDED_BROADCAST" or itemType == "POPULAR_BROADCAST" or itemType == "POPULAR_TWITTER" */
	protected TVBroadcastWithChannelInfo broadcast;
	
	/* This variable is used if itemType == "POPULAR_BROADCASTS" */
	protected List<TVBroadcastWithChannelInfo> broadcasts;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public TVFeedItemJSON()
	{}

	
	
	public FeedItemTypeEnum getItemType()
	{
		return FeedItemTypeEnum.getFeedItemTypeEnumFromStringRepresentation(itemType);
	}
	
	
	
	public String getTitle()
	{
		return title;
	}



	public TVBroadcastWithChannelInfo getBroadcast()
	{
		return broadcast;
	}
}