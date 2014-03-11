
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.DummyData;
import com.mitv.models.gson.serialization.UserPasswordResetPasswordData;



public class PerformUserPasswordResetSendEmail 
	extends AsyncTaskWithRelativeURL<DummyData> 
{
	private static final String TAG = PerformUserPasswordResetSendEmail.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_RESET_PASSWORD_SEND_EMAIL;
	
	
	
	public PerformUserPasswordResetSendEmail(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallbackListener,
			String email)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_EMAIL, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserPasswordResetPasswordData postData = new UserPasswordResetPasswordData();
		postData.setEmail(email);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}