
package com.mitv.asynctasks;



import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;



public abstract class AsyncTaskWithRelativeURL<T> 
	extends AsyncTaskBase<T>
{
	public AsyncTaskWithRelativeURL(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			String url) 
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, false, httpRequestType, url);
	}
	
	
	
	public AsyncTaskWithRelativeURL(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			String url) 
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, manualDeserialization, httpRequestType, url, new URLParameters(), new HeaderParameters(), null);
	}
	
	
	
	public AsyncTaskWithRelativeURL(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			Class clazzSingle,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			String url) 
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, clazzSingle, manualDeserialization, httpRequestType, url, new URLParameters(), new HeaderParameters(), null);
	}

	
	
	public AsyncTaskWithRelativeURL(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			Class clazzSingle,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			String url,
			URLParameters urlParameters,
			HeaderParameters headerParameters,
			String bodyContentData)
	{
		super(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, clazzSingle, manualDeserialization, httpRequestType, true, url, urlParameters, headerParameters, bodyContentData);
	}
}