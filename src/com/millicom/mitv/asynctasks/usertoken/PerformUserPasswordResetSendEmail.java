
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.mitv.Consts;



public class PerformUserPasswordResetSendEmail 
	extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String URL_SUFFIX = Consts.URL_RESET_PASSWORD_SEND_EMAIL;
	
	
	private String email;
	
	
	
	public PerformUserPasswordResetSendEmail(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			String email)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_EMAIL, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		this.email = email;
	}
	

	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}