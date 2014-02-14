
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserData;
import com.mitv.Consts;



public class GetUserTokenUsingFBToken extends AsyncTaskWithUserToken<UserData> 
{
	private static final String URL_SUFFIX = Consts.URL_FACEBOOK_TOKEN;
	
	
	private String facebookToken;
	
	
	
	public GetUserTokenUsingFBToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String facebookToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_FB_TOKEN, UserData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		this.facebookToken = facebookToken;
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}

}