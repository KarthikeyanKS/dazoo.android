
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.ContentManager;
import com.millicom.mitv.asynctasks.usertoken.PerformUserLogout;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public class PerformUserLogoutBuilder
{	
	public PerformUserLogout build(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		String userToken = ContentManager.sharedInstance().getFromStorageUserToken();
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_AUTH_TOKENS);
		url.append(userToken);
				
		PerformUserLogout performuserLogout = new PerformUserLogout(contentCallbackListener, activityCallBackListener, url.toString());
		
		return performuserLogout;
	}
}