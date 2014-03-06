
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.serialization.UserRegistrationData;



public class PerformUserSignUp 
	extends AsyncTaskWithRelativeURL<UserLoginData> 
{
	private static final String TAG = PerformUserSignUp.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_REGISTER;
	
	
	
	public PerformUserSignUp(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String email,
			String password,
			String firstname,
			String lastname)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_SIGN_UP, UserLoginData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserRegistrationData postData = new UserRegistrationData();
		postData.setEmail(email);
		postData.setPassword(password);
		postData.setFirstName(firstname);
		postData.setLastName(lastname);
		
		this.bodyContentData = gson.toJson(postData);
		
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