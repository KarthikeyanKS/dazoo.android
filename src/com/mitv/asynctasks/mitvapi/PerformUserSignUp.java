
package com.mitv.asynctasks.mitvapi;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class PerformUserSignUp 
	extends AsyncTaskBase<UserLoginData> 
{
	private static final String TAG = PerformUserSignUp.class.getName();
	
	
	
	private static String getUrl(boolean usingHashedPassword)
	{
		if(usingHashedPassword)
		{
			return Constants.URL_REGISTER_WITH_HASHED_PASSWORD;
		}
		else
		{
			return Constants.URL_REGISTER_WITH_PLAINTEXT_PASSWORD;
		}
	}
	
	
	
	public PerformUserSignUp(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			UserRegistrationData userRegistrationData,
			boolean usingHashedPassword)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_SIGN_UP, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, getUrl(usingHashedPassword));
		
		this.bodyContentData = gson.toJson(userRegistrationData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		// TODO Backend support 
		/* 
		 * This is a backend restriction and should be changed. 
		 * In the future, appropriate error response codes should be returned.
		 */
		if(requestResultStatus == FetchRequestResultEnum.BAD_REQUEST)
		{
			if(response.hasResponseString())
			{
				String responseString = response.getResponseString();
					
				if(responseString.equals(FetchRequestResultEnum.USER_SIGN_UP_EMAIL_ALREADY_TAKEN.getDescription()))
				{
					requestResultStatus = FetchRequestResultEnum.USER_SIGN_UP_EMAIL_ALREADY_TAKEN;
				}
				else if(responseString.equals(FetchRequestResultEnum.USER_SIGN_UP_EMAIL_IS_INVALID.getDescription()))
				{
					requestResultStatus = FetchRequestResultEnum.USER_SIGN_UP_EMAIL_IS_INVALID;
				}
				else if(responseString.equals(FetchRequestResultEnum.USER_SIGN_UP_PASSWORD_TOO_SHORT.getDescription()))
				{
					requestResultStatus = FetchRequestResultEnum.USER_SIGN_UP_PASSWORD_TOO_SHORT;
				}
				else if(responseString.equals(FetchRequestResultEnum.USER_SIGN_UP_FIRST_NAME_NOT_SUPLIED.getDescription()))
				{
					requestResultStatus = FetchRequestResultEnum.USER_SIGN_UP_FIRST_NAME_NOT_SUPLIED;
				}
				else
				{
					Log.w(TAG, "Response string was not empty, but the case was unhandled.");
				}
			}
			else
			{
				Log.w(TAG, "Response string was empty.");
			}
		}
		// Otherwise keep the same result status
		
		super.onPostExecute(result);	
	}
}