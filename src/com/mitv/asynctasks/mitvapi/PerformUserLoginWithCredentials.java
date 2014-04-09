
package com.mitv.asynctasks.mitvapi;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class PerformUserLoginWithCredentials 
	extends AsyncTaskBase<UserLoginData> 
{
	private static final String TAG = PerformUserLoginWithCredentials.class.getName();
	
	
	
	private static String getUrl(boolean usingHashedPassword)
	{
		if(usingHashedPassword)
		{
			return Constants.URL_LOGIN_WITH_HASHED_PASSWORD;
		}
		else
		{
			return Constants.URL_LOGIN_WITH_PLAINTEXT_PASSWORD;
		}
	}
	
	
	
	
	public PerformUserLoginWithCredentials(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			UserLoginDataPost userLoginDataPost,
			boolean usingHashedPassword) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_LOGIN, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, getUrl(usingHashedPassword));
		
		this.bodyContentData = gson.toJson(userLoginDataPost);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}