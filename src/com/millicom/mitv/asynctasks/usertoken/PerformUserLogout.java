
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserLoginData;
import com.mitv.Consts;



public class PerformUserLogout
	extends AsyncTaskWithUserToken<UserLoginData> 
{
	private static String buildURL()
	{
		String userToken = ContentManager.sharedInstance().getFromStorageUserToken();
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_AUTH_TOKENS);
		url.append(userToken);
		
		return url.toString();
	}
	
	
	
	public PerformUserLogout(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LOGOUT, UserLoginData.class, HTTPRequestTypeEnum.HTTP_DELETE, buildURL());
	}
}