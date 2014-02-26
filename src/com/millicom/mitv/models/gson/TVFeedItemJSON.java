
package com.millicom.mitv.models.gson;



import java.util.List;
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;



public class TVFeedItemJSON
{
	@SuppressWarnings("unused")
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



	public List<TVBroadcastWithChannelInfo> getBroadcasts() 
	{
		return broadcasts;
	}
}