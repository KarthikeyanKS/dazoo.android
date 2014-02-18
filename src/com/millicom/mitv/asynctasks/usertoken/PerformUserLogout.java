
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.asynctasks.builders.PerformUserLogoutBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.UserLoginData;



public class PerformUserLogout
	extends AsyncTaskWithUserToken<UserLoginData> 
{
	public static PerformUserLogout newGetTVBroadcastsFromSeriesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		PerformUserLogoutBuilder performUserLogoutBuilder = new PerformUserLogoutBuilder();
		
		PerformUserLogout performuserLogout = performUserLogoutBuilder.build(contentCallbackListener, activityCallBackListener);
		
		return performuserLogout;
	}
	
	
	
	public PerformUserLogout(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LOGOUT, UserLoginData.class, HTTPRequestTypeEnum.HTTP_DELETE, url);
	}
}