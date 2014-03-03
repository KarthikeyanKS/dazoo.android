
package com.millicom.mitv.asynctasks.usertoken;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserLike;
import com.mitv.Consts;



public class GetUserLikes 
	extends AsyncTaskWithUserToken<UserLike[]> 
{
	private static final String TAG = GetUserLikes.class.getName();
	
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
		super(contentCallbackListener, activityCallBackListener, getRequestIdentifier(standaloneUserLikes), UserLike[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultObjectContent != null)
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