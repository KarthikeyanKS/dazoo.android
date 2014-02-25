
package com.millicom.mitv.asynctasks.usertoken;



import android.util.Log;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.asynctasks.AsyncTaskWithRelativeURL;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public abstract class AsyncTaskWithUserToken<T> 
	extends AsyncTaskWithRelativeURL<T> 
{	
	private static final String TAG = AsyncTaskWithUserToken.class.getName();
	

	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener, 
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			String urlSuffix) 
	{
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, null, false, httpRequestType, urlSuffix);
	}

	
	
//	public AsyncTaskWithUserToken(
//			ContentCallbackListener contentCallbackListener,
//			ActivityCallbackListener activityCallBackListener, 
//			RequestIdentifierEnum requestIdentifier,
//			Class<T> clazz,
//			boolean manualDeserialization,
//			HTTPRequestTypeEnum httpRequestType,
//			String urlSuffix) 
//	{
//		super(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, null, manualDeserialization, httpRequestType, urlSuffix);
//	}
	
	
	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener, 
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			Class clazzSingle,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			String urlSuffix) 
	{
		super(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, clazzSingle, manualDeserialization, httpRequestType, urlSuffix);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		String userToken = ContentManager.sharedInstance().getFromStorageUserToken();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.USER_AUTHORIZATION_HEADER_VALUE_PREFIX);
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
		headerParameters.add(Consts.USER_AUTHORIZATION_HEADER_KEY, headerValue);
		
		return super.doInBackground(params);
	}
}