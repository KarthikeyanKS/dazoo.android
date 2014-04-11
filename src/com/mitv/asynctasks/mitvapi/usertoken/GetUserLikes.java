
package com.mitv.asynctasks.mitvapi.usertoken;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.UserLike;



public class GetUserLikes 
	extends AsyncTaskWithUserToken<UserLike[]> 
{
	private static final String TAG = GetUserLikes.class.getName();
	private static final String URL_SUFFIX = Constants.URL_LIKES_WITH_UPCOMING;
	
	
	
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
			ViewCallbackListener activityCallbackListener,
			boolean standaloneUserLikes) 
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standaloneUserLikes), UserLike[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			UserLike[] contentAsArray = (UserLike[]) requestResultObjectContent;
			
			ArrayList<UserLike> contentAsArrayList = new ArrayList<UserLike>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}