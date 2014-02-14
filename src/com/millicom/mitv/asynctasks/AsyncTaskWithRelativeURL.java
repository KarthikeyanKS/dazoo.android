
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;



public class AsyncTaskWithRelativeURL<T> 
	extends AsyncTaskBase<T>
{	
	public AsyncTaskWithRelativeURL(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			String url) 
	{
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, httpRequestType, url, new URLParameters());
	}

	
	
	public AsyncTaskWithRelativeURL(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			String url,
			URLParameters urlParameters)
	{
		super(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, httpRequestType, true, url, urlParameters);
	}
}