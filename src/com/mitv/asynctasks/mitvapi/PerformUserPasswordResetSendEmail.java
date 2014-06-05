
package com.mitv.asynctasks.mitvapi;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.gson.serialization.UserPasswordResetPasswordData;
import com.mitv.models.objects.mitvapi.DummyData;



public class PerformUserPasswordResetSendEmail 
	extends AsyncTaskBase<DummyData> 
{
	private static final String TAG = PerformUserPasswordResetSendEmail.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_RESET_PASSWORD_SEND_EMAIL;
	
	
	
	public PerformUserPasswordResetSendEmail(
			ContentCallbackListener contentCallbackListener, 
			ViewCallbackListener activityCallbackListener,
			String email,
			boolean isRetry)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_EMAIL, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX, false, isRetry);
		
		UserPasswordResetPasswordData postData = new UserPasswordResetPasswordData();
		postData.setEmail(email);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}