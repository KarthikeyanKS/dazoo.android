
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserData;
import com.mitv.Consts;



public class PerformUserLogin 
	extends AsyncTaskWithRelativeURL<UserData> 
{
	private static final String URL_SUFFIX = Consts.URL_LOGIN;
	
	
	private String username;
	private String password;
	
	
	
	public PerformUserLogin(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String username,
			String password) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LOGIN, UserData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		this.username = username;
		this.password = password;
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}