
package com.mitv.asynctasks.usertoken;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.asynctasks.AsyncTaskWithRelativeURL;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;



public abstract class AsyncTaskWithUserToken<T> 
	extends AsyncTaskWithRelativeURL<T> 
{	
	private static final String TAG = AsyncTaskWithUserToken.class.getName();
	

	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener, 
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			String urlSuffix) 
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, false, httpRequestType, urlSuffix);
	}

	
	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener, 
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			Class clazzSingle,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			String urlSuffix) 
	{
		super(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, clazzSingle, manualDeserialization, httpRequestType, urlSuffix);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		String userToken = ContentManager.sharedInstance().getFromCacheUserToken();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.USER_AUTHORIZATION_HEADER_VALUE_PREFIX);
		sb.append(" ");
		
		if(ContentManager.sharedInstance().isLoggedIn())
		{
			sb.append(userToken);
		}
		else
		{
			Log.e(TAG, "User is not logged in. Verify token status.");
		}
		
		String headerValue = sb.toString();
		headerParameters.add(Constants.USER_AUTHORIZATION_HEADER_KEY, headerValue);
		
		return super.doInBackground(params);
	}
}