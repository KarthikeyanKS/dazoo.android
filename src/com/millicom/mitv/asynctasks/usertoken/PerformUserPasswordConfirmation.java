
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.mitv.Consts;



public class PerformUserPasswordConfirmation extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String URL_SUFFIX = Consts.URL_RESET_AND_CONFIRM_PASSWORD;
	
	
	private String email;
	private String newPassword;
	private String resetPasswordToken;
	
	
	
	public PerformUserPasswordConfirmation(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			String email,
			String newPassword,
			String resetPasswordToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		this.email = email;
		this.newPassword = newPassword;
		this.resetPasswordToken = resetPasswordToken;
	}
	

	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}