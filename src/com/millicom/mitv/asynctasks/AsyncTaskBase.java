
package com.millicom.mitv.asynctasks;



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
	private Map<String, String> headerParameters;
	private String bodyContentData;
	
	protected FetchRequestResultEnum requestResult;
	protected Object content;
	
	
	
	public AsyncTaskBase(
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier, 
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			boolean isRelativeURL,
			String url) 
	{
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, httpRequestType, isRelativeURL, url, new URLParameters());
	}
	
	
	
	public AsyncTaskBase(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			HTTPRequestTypeEnum httpRequestType,
			boolean isRelativeURL,
			String url,
			URLParameters urlParameters)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallBackListener = activityCallBackListener;
		this.httpRequestType = httpRequestType;
		this.isRelativeURL = isRelativeURL;
		this.url = url;
		this.urlParameters = urlParameters;
		this.requestIdentifier = requestIdentifier;
		this.requestResult = FetchRequestResultEnum.UNKNOWN_ERROR;
		
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
		
		this.requestResult = FetchRequestResultEnum.getFetchRequestResultEnumFromCode(response.getStatusCode());
		
		if(response.hasResponseString())
		{
			String contentAsString = response.getResponseString();
			
			// TODO - Parse string into json
			
			// this.content =
		}
		else
		{
			this.content = null;
		}
				
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{
		contentCallbackListener.onResult(activityCallBackListener, requestIdentifier, requestResult, content);
	}	
}