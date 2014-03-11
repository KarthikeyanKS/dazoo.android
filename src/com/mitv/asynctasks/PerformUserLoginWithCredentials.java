
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.serialization.UserLoginDataPost;



public class PerformUserLoginWithCredentials 
	extends AsyncTaskWithRelativeURL<UserLoginData> 
{
	private static final String TAG = "PerformUserLogin";
	
	private static final String URL_SUFFIX = Constants.URL_LOGIN;
	
	
	
	public PerformUserLoginWithCredentials(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener,
			String username,
			String password) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_LOGIN, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserLoginDataPost postData = new UserLoginDataPost();
		postData.setEmail(username);
		postData.setPassword(password);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}