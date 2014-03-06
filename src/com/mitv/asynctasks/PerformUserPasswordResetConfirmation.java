
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.DummyData;
import com.mitv.models.gson.serialization.UserPasswordResetConfirmationData;



public class PerformUserPasswordResetConfirmation 
	extends AsyncTaskWithRelativeURL<DummyData> 
{
	private static final String TAG = PerformUserPasswordResetConfirmation.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_RESET_AND_CONFIRM_PASSWORD;
	

	
	public PerformUserPasswordResetConfirmation(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			String email,
			String newPassword,
			String resetPasswordToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserPasswordResetConfirmationData postData = new UserPasswordResetConfirmationData();
		postData.setEmail(email);
		postData.setNewPassword(newPassword);
		postData.setResetPasswordToken(resetPasswordToken);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}