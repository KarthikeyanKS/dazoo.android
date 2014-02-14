
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
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, OldTVFeedItem.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}