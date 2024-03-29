
package com.mitv.asynctasks.mitvapi.usertoken;



import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class PerformUserLogout
	extends AsyncTaskWithUserToken<UserLoginData> 
{
	/* Being called on UI thread, as it should */
	private static String buildURL()
	{
		String userToken = ContentManager.sharedInstance().getCacheManager().getUserToken();
		
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_AUTH_TOKENS);
		url.append(userToken);
		
		return url.toString();
	}
	
	
	
	public PerformUserLogout(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_LOGOUT, UserLoginData.class, HTTPRequestTypeEnum.HTTP_DELETE, buildURL(), false, retryThreshold);
	}
}