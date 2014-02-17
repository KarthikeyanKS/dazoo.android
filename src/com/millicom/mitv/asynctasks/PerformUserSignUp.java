
package com.millicom.mitv.asynctasks;



import android.util.Log;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.UserData;
import com.millicom.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.Consts;



public class PerformUserSignUp 
	extends AsyncTaskWithRelativeURL<UserData> 
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
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_SIGN_UP, UserData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserRegistrationData userRegistrationData = new UserRegistrationData();
		userRegistrationData.setEmail(email);
		userRegistrationData.setPassword(password);
		userRegistrationData.setFirstName(firstname);
		userRegistrationData.setLastName(lastname);
		
		this.bodyContentData = gson.toJson(userRegistrationData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}