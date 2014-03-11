
package com.mitv.asynctasks.usertoken;



import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.UserLoginData;



public class PerformUserLogout
	extends AsyncTaskWithUserToken<UserLoginData> 
{
	private static String buildURL()
	{
		String userToken = ContentManager.sharedInstance().getFromCacheUserToken();
		
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_AUTH_TOKENS);
		url.append(userToken);
		
		return url.toString();
	}
	
	
	
	public PerformUserLogout(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_LOGOUT, UserLoginData.class, HTTPRequestTypeEnum.HTTP_DELETE, buildURL());
	}
}