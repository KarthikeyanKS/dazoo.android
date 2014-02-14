
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserData;
import com.mitv.Consts;



public class PerformUserSignUp 
	extends AsyncTaskWithRelativeURL<UserData> 
{
	private static final String URL_SUFFIX = Consts.URL_REGISTER;
	
	
	private String email;
	private String password;
	private String firstname;
	private String lastname;
	
	
	
	public PerformUserSignUp(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String email,
			String password,
			String firstname,
			String lastname)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_SIGN_UP, UserData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}