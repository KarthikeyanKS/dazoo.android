
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.Storage;
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
		String userToken = Storage.sharedInstance().getUserToken();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_AUTH_TOKENS);
		sb.append(userToken);
				
		PerformUserLogout performuserLogout = new PerformUserLogout(contentCallbackListener, activityCallBackListener, sb.toString());
		
		return performuserLogout;
	}
}