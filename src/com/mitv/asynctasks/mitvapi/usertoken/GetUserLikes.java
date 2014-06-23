
package com.mitv.asynctasks.mitvapi.usertoken;



import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.UserLike;



public class GetUserLikes 
	extends AsyncTaskWithUserToken<UserLike[]> 
{
	@SuppressWarnings("unused")
	private static final String TAG = GetUserLikes.class.getName();
	
	
	private static final String URL_SUFFIX = Constants.URL_LIKES_WITH_UPCOMING;
	
		
	
	private static RequestIdentifierEnum getRequestIdentifier(
			boolean initialCall,
			boolean standaloneUserLikes)
	{
		if(initialCall)
		{
			return RequestIdentifierEnum.USER_LIKES_INITIAL_CALL;
		}
		else
		{
			if(standaloneUserLikes)
			{
				return RequestIdentifierEnum.USER_LIKES_STANDALONE;
			}
			else
			{
				return RequestIdentifierEnum.USER_ACTIVITY_FEED_LIKES;
			}
		}
	}
	
	
	
	public GetUserLikes(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean initialCall,
			boolean standaloneUserLikes,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(initialCall, standaloneUserLikes), UserLike[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, false, retryThreshold);
	}
}