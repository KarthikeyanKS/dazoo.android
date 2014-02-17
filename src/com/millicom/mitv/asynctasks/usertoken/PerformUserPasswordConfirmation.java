
package com.millicom.mitv.asynctasks.usertoken;



import android.util.Log;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.millicom.mitv.models.gson.serialization.UserPasswordConfirmationData;
import com.mitv.Consts;



public class PerformUserPasswordConfirmation 
	extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String TAG = "PerformUserPasswordConfirmation";
	
	private static final String URL_SUFFIX = Consts.URL_RESET_AND_CONFIRM_PASSWORD;
	

	
	public PerformUserPasswordConfirmation(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			String email,
			String newPassword,
			String resetPasswordToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserPasswordConfirmationData userPasswordConfirmationData = new UserPasswordConfirmationData();
		userPasswordConfirmationData.setEmail(email);
		userPasswordConfirmationData.setNewPassword(newPassword);
		userPasswordConfirmationData.setResetPasswordToken(resetPasswordToken);
		
		this.bodyContentData = gson.toJson(userPasswordConfirmationData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}