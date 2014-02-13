package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.TVFeedItem;



public class GetFeedItems extends AsyncTaskWithUserToken<TVFeedItem> 
{	
	private static final String URL_SUFFIX = Consts.URL_ACTIVITY_FEED;
	
	public GetFeedItems(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, TVFeedItem.class, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		return null;
	}

}
