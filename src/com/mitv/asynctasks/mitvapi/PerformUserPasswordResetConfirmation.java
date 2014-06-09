
package com.mitv.asynctasks.mitvapi;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.gson.serialization.UserPasswordResetConfirmationData;
import com.mitv.models.objects.mitvapi.DummyData;



public class PerformUserPasswordResetConfirmation 
	extends AsyncTaskBase<DummyData> 
{
	private static final String TAG = PerformUserPasswordResetConfirmation.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_RESET_AND_CONFIRM_PASSWORD;
	

	
	public PerformUserPasswordResetConfirmation(
			ContentCallbackListener contentCallbackListener, 
			ViewCallbackListener activityCallbackListener,
			String email,
			String newPassword,
			String resetPasswordToken,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX, false, retryThreshold);
		
		UserPasswordResetConfirmationData postData = new UserPasswordResetConfirmationData();
		postData.setEmail(email);
		postData.setNewPassword(newPassword);
		postData.setResetPasswordToken(resetPasswordToken);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}