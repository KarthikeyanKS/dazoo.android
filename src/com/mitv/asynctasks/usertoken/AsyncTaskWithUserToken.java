
package com.mitv.asynctasks.usertoken;



import android.text.TextUtils;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.asynctasks.AsyncTaskWithRelativeURL;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;



public abstract class AsyncTaskWithUserToken<T> 
	extends AsyncTaskWithRelativeURL<T> 
{	
	private static final String TAG = AsyncTaskWithUserToken.class.getName();
	private String userToken; /* Should not be set in doInBackground since that is not run on main thread */

	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener, 
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			String url) 
	{
		super(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, httpRequestType, url);
	}
	
	
	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener, 
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			String url) 
	{
		super(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, manualDeserialization, httpRequestType, url);
	}
	
	

	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		userToken = ContentManager.sharedInstance().getFromCacheUserToken();
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.USER_AUTHORIZATION_HEADER_VALUE_PREFIX);
		sb.append(" ");
		
		if(!TextUtils.isEmpty(userToken))
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