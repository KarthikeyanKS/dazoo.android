
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.serialization.UserFacebookTokenData;



public class PerformUserLoginWithFacebookToken 
	extends AsyncTaskWithRelativeURL<UserLoginData> 
{
	private static final String TAG = PerformUserLoginWithFacebookToken.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_FACEBOOK_TOKEN;
	
	
	
	public PerformUserLoginWithFacebookToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String facebookToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_FB_TOKEN, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserFacebookTokenData userFacebookTokenData = new UserFacebookTokenData();
		userFacebookTokenData.setFacebookToken(facebookToken);
		
		this.bodyContentData = gson.toJson(userFacebookTokenData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}