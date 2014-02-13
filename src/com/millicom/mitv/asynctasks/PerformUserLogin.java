
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserData;
import com.mitv.Consts;



public class PerformUserLogin extends AsyncTaskWithRelativeURL<UserData> 
{
	private static final String URL_SUFFIX = Consts.URL_LOGIN;
	
	
	
	public PerformUserLogin(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LOGIN, UserData.class, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		return null;
	}
}