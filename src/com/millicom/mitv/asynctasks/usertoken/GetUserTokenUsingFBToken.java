
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.UserData;
import com.mitv.Consts;



public class GetUserTokenUsingFBToken 
	extends AsyncTaskWithUserToken<UserData> 
{
	private static final String URL_SUFFIX = Consts.URL_FACEBOOK_TOKEN;
	
	
	
	public GetUserTokenUsingFBToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String facebookToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_FB_TOKEN, UserData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		// TODO - Transform parameter data fields into json data
	}
}