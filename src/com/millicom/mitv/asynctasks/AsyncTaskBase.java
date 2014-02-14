
package com.millicom.mitv.asynctasks;



import java.util.Collections;
import java.util.Map;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.HTTPCore;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;



public class AsyncTaskBase<T> 
	extends AsyncTask<String, Void, Void> 
{	
	private ContentCallbackListener contentCallbackListener;
	private ActivityCallbackListener activityCallBackListener;
	private RequestIdentifierEnum requestIdentifier;
	
	private Gson gson;
	private Class<T> clazz;
	
	private HTTPRequestTypeEnum httpRequestType;
	private boolean isRelativeURL;
	private String url;
	
	protected URLParameters urlParameters;
	protected Map<String, String> headerParameters;
	protected String bodyContentData;
	
	protected FetchRequestResultEnum requestResultStatus;
	protected Object requestResultObjectContent;
	
	
	
	public AsyncTaskBase(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier, 
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			boolean isRelativeURL,
			String url) 
	{
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, httpRequestType, isRelativeURL, url, new URLParameters(), Collections.<String, String> emptyMap(), null);
	}
	
	
	
	public AsyncTaskBase(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			boolean isRelativeURL,
			String url,
			URLParameters urlParameters,
			Map<String, String> headerParameters,
			String bodyContentData)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallBackListener = activityCallBackListener;
		this.requestIdentifier = requestIdentifier;
		
		this.httpRequestType = httpRequestType;
		this.isRelativeURL = isRelativeURL;
		this.url = url;
		this.urlParameters = urlParameters;
		this.headerParameters = headerParameters;
		this.bodyContentData = bodyContentData;
		
		this.requestResultStatus = FetchRequestResultEnum.UNKNOWN_ERROR;
		this.requestResultObjectContent = null;
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		try 
		{
			gsonBuilder.registerTypeAdapter(clazz, clazz.newInstance());
			this.gson = gsonBuilder.create();
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		HTTPCoreResponse response = HTTPCore.sharedInstance().executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		this.requestResultStatus = FetchRequestResultEnum.getFetchRequestResultEnumFromCode(response.getStatusCode());
		
		if(response.hasResponseString())
		{
			String contentAsString = response.getResponseString();
			
			// TODO - Parse string into json
			
			// gson.
			
			// this.content =
		}
		else
		{
			this.requestResultObjectContent = null;
		}
				
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{
		contentCallbackListener.onResult(activityCallBackListener, requestIdentifier, requestResultStatus, requestResultObjectContent);
	}	
}