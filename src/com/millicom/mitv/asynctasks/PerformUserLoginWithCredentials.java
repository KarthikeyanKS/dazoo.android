
package com.millicom.mitv.asynctasks;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserLoginData;
import com.millicom.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.Consts;



public class PerformUserLoginWithCredentials 
	extends AsyncTaskWithRelativeURL<UserLoginData> 
{
	private static final String TAG = "PerformUserLogin";
	
	private static final String URL_SUFFIX = Consts.URL_LOGIN;
	
	
	
	public PerformUserLoginWithCredentials(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String username,
			String password) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LOGIN, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserLoginDataPost postData = new UserLoginDataPost();
		postData.setEmail(username);
		postData.setPassword(password);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}