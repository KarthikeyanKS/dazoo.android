
package com.millicom.mitv.asynctasks.usertoken;



import java.util.ArrayList;
import java.util.Arrays;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVFeedItem;
import com.mitv.Consts;



public class GetUserTVFeedItems
	extends AsyncTaskWithUserToken<TVFeedItem[]> 
{
	private static final String URL_SUFFIX = Consts.URL_ACTIVITY_FEED;
	
	
	
	private static RequestIdentifierEnum getRequestIdentifier(int itemStartIndex)
	{
		if(itemStartIndex == 0)
		{
			return RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM;
		}
		else
		{
			return RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE;
		}
	}
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		this(contentCallbackListener, activityCallBackListener, false, 0, false, 0);
	}
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			int itemStartIndex,
			int itemLimit)
	{
		this(contentCallbackListener, activityCallBackListener, true, itemStartIndex, true, itemLimit);
	}
	
	
	
	
	private GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			boolean useItemStartIndex,
			int itemStartIndex,
			boolean useItemLimit,
			int itemLimit)
	{
		super(contentCallbackListener, activityCallBackListener, getRequestIdentifier(itemStartIndex), TVFeedItem[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
		if(useItemStartIndex)
		{
			this.urlParameters.add(Consts.API_SKIP, String.valueOf(itemStartIndex));
		}
		
		if(useItemLimit)
		{
			this.urlParameters.add(Consts.API_LIMIT, String.valueOf(itemLimit));
		}
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		TVFeedItem[] contentAsArray = (TVFeedItem[]) requestResultObjectContent;
		ArrayList<TVFeedItem> contentAsArrayList = new ArrayList<TVFeedItem>(Arrays.asList(contentAsArray));
		requestResultObjectContent = contentAsArrayList;
		 
		return null;
	}
}