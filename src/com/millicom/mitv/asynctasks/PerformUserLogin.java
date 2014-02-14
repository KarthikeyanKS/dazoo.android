
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.UserData;
import com.mitv.Consts;



public class PerformUserLogin 
	extends AsyncTaskWithRelativeURL<UserData> 
{
	private static final String URL_SUFFIX = Consts.URL_LOGIN;
	
	
	
	public PerformUserLogin(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String username,
			String password) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LOGIN, UserData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		// TODO - Transform parameter data fields into json data
	}
}