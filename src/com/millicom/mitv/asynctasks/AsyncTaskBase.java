
package com.millicom.mitv.asynctasks;



import java.util.Locale;
import java.util.TimeZone;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.HTTPCore;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.HeaderParameters;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.utilities.DateUtils;
import com.mitv.Consts;
import com.mitv.SecondScreenApplication;



public abstract class AsyncTaskBase<T> 
	extends AsyncTask<String, Void, Void> 
{	
	private static final String TAG = AsyncTaskBase.class.getName();
	
	private ContentCallbackListener contentCallbackListener;
	private ActivityCallbackListener activityCallBackListener;
	private RequestIdentifierEnum requestIdentifier;
	
	protected Gson gson;   // This is protected due to its use in classes that need to serialize data into the bodyContentData
	
	private Class<T> clazz;
	
	private HTTPRequestTypeEnum httpRequestType;
	private boolean isRelativeURL;
	private String url;
	
	protected URLParameters urlParameters;
	protected HeaderParameters headerParameters;
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
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, null, false, httpRequestType, isRelativeURL, url, new URLParameters(), new HeaderParameters(), null);
	}
	
//	public AsyncTaskBase(
//			ContentCallbackListener contentCallbackListener, 
//			ActivityCallbackListener activityCallBackListener,
//			RequestIdentifierEnum requestIdentifier, 
//			Class<T> clazz,
//			Class clazzSingle,
//			boolean manualDeserialization,
//			HTTPRequestTypeEnum httpRequestType,
//			boolean isRelativeURL,
//			String url) 
//	{
//		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, clazzSingle, manualDeserialization, httpRequestType, isRelativeURL, url, new URLParameters(), new HeaderParameters(), null);
//	}
//	
//	public AsyncTaskBase(
//			ContentCallbackListener contentCallbackListener, 
//			ActivityCallbackListener activityCallBackListener,
//			RequestIdentifierEnum requestIdentifier, 
//			Class<T> clazz,
//			boolean manualDeserialization,
//			HTTPRequestTypeEnum httpRequestType,
//			boolean isRelativeURL,
//			String url) 
//	{
//		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, null, manualDeserialization, httpRequestType, isRelativeURL, url, new URLParameters(), new HeaderParameters(), null);
//	}
	
	
	
	public AsyncTaskBase(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			Class<T> clazz,
			Class clazzSingle,
			boolean manualDeserialization,
			HTTPRequestTypeEnum httpRequestType,
			boolean isRelativeURL,
			String url,
			URLParameters urlParameters,
			HeaderParameters headerParameters,
			String bodyContentData)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallBackListener = activityCallBackListener;
		this.requestIdentifier = requestIdentifier;
		this.clazz = clazz;
		this.httpRequestType = httpRequestType;
		this.isRelativeURL = isRelativeURL;
		this.url = url;
		this.urlParameters = urlParameters;
		this.headerParameters = headerParameters;
		this.bodyContentData = bodyContentData;
		
		this.requestResultStatus = FetchRequestResultEnum.UNKNOWN_ERROR;
		this.requestResultObjectContent = null;
		
		/* Add the locale to the header data */
		Locale locale = SecondScreenApplication.getCurrentLocale();
		TimeZone timeZone = TimeZone.getDefault();
		
		if(locale != null && timeZone != null)
		{
			int timeZoneOffsetInMinutesAsInt = (int) (timeZone.getRawOffset() / DateUtils.TOTAL_MILISECOUNDS_IN_ONE_MINUTE);
		
			Integer timeZoneOffsetInMinutes = Integer.valueOf(timeZoneOffsetInMinutesAsInt);
		
			urlParameters.add(Consts.HTTP_REQUEST_DATA_LOCALE, locale.toString());
			urlParameters.add(Consts.HTTP_REQUEST_DATA_TIME_ZONE_OFFSET, timeZoneOffsetInMinutes.toString());
		}
		else
		{
			Log.w(TAG, "Either locale or timeZone have null values.");
		}
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		try 
		{
			if(manualDeserialization) 
			{
				Object classIntance = null;
				
				if(clazz.isArray())
				{
					classIntance = clazzSingle.newInstance();
				} 
				else 
				{
					classIntance = clazz.newInstance();
				}
				
				gsonBuilder.registerTypeAdapter(clazz, classIntance);
				gsonBuilder.excludeFieldsWithoutExposeAnnotation();
			}
			this.gson = gsonBuilder.create();
		} 
		catch (InstantiationException iex)
		{
			Log.e(TAG, iex.getMessage(), iex);
		} 
		catch (IllegalAccessException ilaex) 
		{
			Log.e(TAG, ilaex.getMessage(), ilaex);
		}
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		HTTPCoreResponse response = HTTPCore.sharedInstance().executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		requestResultStatus = FetchRequestResultEnum.getFetchRequestResultEnumFromCode(response.getStatusCode());
		
		boolean hasResponseString = response.hasResponseString();
		
		if(hasResponseString)
		{
			String responseString = response.getResponseString();
			
			try
			{
				requestResultObjectContent = gson.fromJson(responseString, clazz);
			}
			catch(JsonSyntaxException jsex)
			{
				Log.e(TAG, jsex.getMessage(), jsex);
				
				requestResultStatus = FetchRequestResultEnum.UNKNOWN_ERROR;
				requestResultObjectContent = null;
			}
		}
		else
		{
			requestResultObjectContent = null;
		}
				
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{
		if(contentCallbackListener != null)
		{
			contentCallbackListener.onResult(activityCallBackListener, requestIdentifier, requestResultStatus, requestResultObjectContent);
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}	
}