
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserLike;
import com.mitv.Consts;



public class GetUserLikes extends AsyncTaskWithUserToken<UserLike[]> 
{
	private static final String URL_SUFFIX = Consts.URL_LIKES;
	
	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standaloneUserLikes)
	{
		if(standaloneUserLikes)
		{
			return RequestIdentifierEnum.USER_LIKES;
		}
		else
		{
			return RequestIdentifierEnum.USER_ACTIVITY_FEED_LIKES;
		}
	}
	
	
	
	public GetUserLikes(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			boolean standaloneUserLikes) 
	{
		super(contentCallbackListener, activityCallBackListener, getRequestIdentifier(standaloneUserLikes), UserLike[].class, UserLike.class, true, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}