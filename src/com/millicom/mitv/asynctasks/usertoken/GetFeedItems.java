
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldTVFeedItem;



public class GetFeedItems
	extends AsyncTaskWithUserToken<OldTVFeedItem> 
{
	private static final String URL_SUFFIX = Consts.URL_ACTIVITY_FEED;
	
	
	
	public GetFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		this(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, false, 0, false, 0);
	}
	
	
	
	public GetFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			int itemStartIndex,
			int itemLimit)
	{
		this(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE, true, itemStartIndex, true, itemLimit);
	}
	
	
	
	private GetFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier, 
			boolean useItemStartIndex,
			int itemStartIndex,
			boolean useItemLimit,
			int itemLimit)
	{
		super(contentCallbackListener, activityCallBackListener, requestIdentifier, OldTVFeedItem.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
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