
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVFeedItem;
import com.mitv.Consts;



public class GetUserTVFeedItems
	extends AsyncTaskWithUserToken<TVFeedItem[]> 
{
	private static final String URL_SUFFIX = Consts.URL_ACTIVITY_FEED;
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		this(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, false, 0, false, 0);
	}
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			int itemStartIndex,
			int itemLimit)
	{
		this(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE, true, itemStartIndex, true, itemLimit);
	}
	
	
	
	private GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier, 
			boolean useItemStartIndex,
			int itemStartIndex,
			boolean useItemLimit,
			int itemLimit)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, TVFeedItem[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
		if(useItemStartIndex)
		{
			this.urlParameters.add(Consts.API_SKIP, String.valueOf(itemStartIndex));
		}
		
		if(useItemLimit)
		{
			this.urlParameters.add(Consts.API_LIMIT, String.valueOf(itemLimit));
		}
	}
}