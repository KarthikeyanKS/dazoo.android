
package com.millicom.mitv.asynctasks;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.millicom.mitv.models.gson.serialization.UserPasswordResetPasswordData;
import com.mitv.Consts;



public class PerformUserPasswordResetSendEmail 
	extends AsyncTaskWithRelativeURL<DummyData> 
{
	private static final String TAG = "PerformUserPasswordResetSendEmail";
	
	private static final String URL_SUFFIX = Consts.URL_RESET_PASSWORD_SEND_EMAIL;
	
	
	
	public PerformUserPasswordResetSendEmail(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			String email)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_EMAIL, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserPasswordResetPasswordData postData = new UserPasswordResetPasswordData();
		postData.setEmail(email);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}