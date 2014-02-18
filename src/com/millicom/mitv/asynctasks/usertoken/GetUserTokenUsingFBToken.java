
package com.millicom.mitv.asynctasks.usertoken;



import android.util.Log;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.UserLoginData;
import com.millicom.mitv.models.gson.serialization.UserFacebookTokenData;
import com.mitv.Consts;



public class GetUserTokenUsingFBToken 
	extends AsyncTaskWithUserToken<UserLoginData> 
{
	private static final String TAG = "GetUserTokenUsingFBToken";
	
	private static final String URL_SUFFIX = Consts.URL_FACEBOOK_TOKEN;
	
	
	
	public GetUserTokenUsingFBToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String facebookToken) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_FB_TOKEN, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserFacebookTokenData userFacebookTokenData = new UserFacebookTokenData();
		userFacebookTokenData.setFacebookToken(facebookToken);
		
		this.bodyContentData = gson.toJson(userFacebookTokenData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}