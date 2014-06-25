
package com.mitv.models.objects.mitvapi;



import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.models.gson.mitvapi.TVFeedItemJSON;



public class TVFeedItem
	extends TVFeedItemJSON
{	
	public TVFeedItem(){}
	
	
	
	public FeedItemTypeEnum getItemType()
	{
		return FeedItemTypeEnum.getFeedItemTypeEnumFromStringRepresentation(getItemTypeString());
	}
}