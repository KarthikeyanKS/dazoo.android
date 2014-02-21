
package com.millicom.mitv.asynctasks;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserLoginData;
import com.millicom.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.Consts;



public class PerformUserSignUp 
	extends AsyncTaskWithRelativeURL<UserLoginData> 
{
	private static final String TAG = "PerformUserSignUp";
	
	private static final String URL_SUFFIX = Consts.URL_REGISTER;
	
	
	
	public PerformUserSignUp(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String email,
			String password,
			String firstname,
			String lastname)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_SIGN_UP, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserRegistrationData postData = new UserRegistrationData();
		postData.setEmail(email);
		postData.setPassword(password);
		postData.setFirstName(firstname);
		postData.setLastName(lastname);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}