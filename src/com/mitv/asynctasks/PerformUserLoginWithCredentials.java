
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.serialization.UserLoginDataPost;



public class PerformUserLoginWithCredentials 
	extends AsyncTaskWithRelativeURL<UserLoginData> 
{
	private static final String TAG = PerformUserLoginWithCredentials.class.getName();
	
	
	
	private static String getUrl(boolean usingHashedPassword)
	{
		if(usingHashedPassword)
		{
			return Constants.URL_LOGIN_WITH_HASH;
		}
		else
		{
			return Constants.URL_LOGIN_WITH_PLAINTEXT_PASSWORD;
		}
	}
	
	
	
	
	public PerformUserLoginWithCredentials(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String username,
			String password,
			boolean usingHashedPassword) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_LOGIN, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, getUrl(usingHashedPassword));
		
		UserLoginDataPost postData = new UserLoginDataPost();
		postData.setEmail(username);
		postData.setPassword(password);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}