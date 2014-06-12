
package com.mitv.asynctasks.mitvapi.usertoken;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.comparators.TVFeedItemComparatorByTime;
import com.mitv.models.objects.mitvapi.TVFeedItem;



public class GetUserTVFeedItems
	extends AsyncTaskWithUserToken<TVFeedItem[]> 
{
	private static final String TAG = GetUserTVFeedItems.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_ACTIVITY_FEED;
	
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
			ViewCallbackListener activityCallbackListener,
			int retryThreshold)
	{
		this(contentCallbackListener, activityCallbackListener, false, 0, false, 0, retryThreshold);
	}
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int itemStartIndex,
			int itemLimit,
			int retryThreshold)
	{
		this(contentCallbackListener, activityCallbackListener, true, itemStartIndex, true, itemLimit, retryThreshold);
	}
	
	
	
	private GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean useItemStartIndex,
			int itemStartIndex,
			boolean useItemLimit,
			int itemLimit,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(itemStartIndex), TVFeedItem[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, false, retryThreshold);
		
		if(useItemStartIndex)
		{
			this.urlParameters.add(Constants.API_SKIP, String.valueOf(itemStartIndex));
		}
		
		if(useItemLimit)
		{
			this.urlParameters.add(Constants.API_LIMIT, String.valueOf(itemLimit));
		}
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVFeedItem[] contentAsArray = (TVFeedItem[]) requestResultObjectContent;
		
			ArrayList<TVFeedItem> contentAsArrayList = new ArrayList<TVFeedItem>(Arrays.asList(contentAsArray));
			
			Collections.sort(contentAsArrayList, new TVFeedItemComparatorByTime());
			
			requestResultObjectContent = contentAsArrayList;
		}
		
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
	
}