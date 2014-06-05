
package com.mitv.asynctasks;



import java.util.Locale;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.http.HTTPCore;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.RequestParameters;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.FileUtils;
import com.mitv.utilities.LanguageUtils;



public abstract class AsyncTaskBase<T> 
	extends AsyncTask<String, Void, Void> 
{	
	private static final String TAG = AsyncTaskBase.class.getName();
	
	
	private ContentCallbackListener contentCallbackListener;
	private ViewCallbackListener activityCallbackListener;
	private RequestIdentifierEnum requestIdentifier;
	
	protected Gson gson;   // This is protected due to its use in classes that need to serialize data into the bodyContentData
	
	private Class<T> clazz;
	
	private HTTPRequestTypeEnum httpRequestType;
	private String url;
	
	protected URLParameters urlParameters;
	protected HeaderParameters headerParameters;
	protected String bodyContentData;
	
	protected FetchRequestResultEnum requestResultStatus;
	protected Object requestResultObjectContent;
	
	protected HTTPCoreResponse response;
	
	// Request parameters to pass along to the handles. Each subclass should set it as necessary.
	protected RequestParameters requestParameters;
	
	protected boolean isMiTVAPICall;
	
	private boolean reportMetricsToTracker;
	
	private boolean isRetry;
	
	
	public AsyncTaskBase(
			final ContentCallbackListener contentCallbackListener, 
			final ViewCallbackListener activityCallbackListener,
			final RequestIdentifierEnum requestIdentifier, 
			final Class<T> clazz,
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final boolean reportMetricsToTracker,
			final boolean isRetry)
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, false, httpRequestType, url, new URLParameters(), new HeaderParameters(), null, true, reportMetricsToTracker, isRetry);
	}
	
	
	
	public AsyncTaskBase(
			final ContentCallbackListener contentCallbackListener, 
			final ViewCallbackListener activityCallbackListener,
			final RequestIdentifierEnum requestIdentifier, 
			final Class<T> clazz,
			final boolean manualDeserialization,
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final boolean reportMetricsToTracker,
			final boolean isRetry) 
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, manualDeserialization, httpRequestType, url, new URLParameters(), new HeaderParameters(), null, true, reportMetricsToTracker, isRetry);
	}
	
	
	
	public AsyncTaskBase(
			final ContentCallbackListener contentCallbackListener, 
			final ViewCallbackListener activityCallbackListener,
			final RequestIdentifierEnum requestIdentifier, 
			final Class<T> clazz,
			final boolean manualDeserialization,
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final boolean isMiTVAPICall,
			final boolean reportMetricsToTracker,
			final boolean isRetry)
	{
		this(contentCallbackListener, activityCallbackListener, requestIdentifier, clazz, null, manualDeserialization, httpRequestType, url, new URLParameters(), new HeaderParameters(), null, false, reportMetricsToTracker, isRetry);
	}
	

	
	private AsyncTaskBase(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final RequestIdentifierEnum requestIdentifier,
			final Class<T> clazz,
			final Class<?> clazzSingle,
			final boolean manualDeserialization,
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final HeaderParameters headerParameters,
			final String bodyContentData,
			final boolean isMiTVAPICall,
			final boolean reportMetricsToTracker,
			final boolean isRetry)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallbackListener = activityCallbackListener;
		this.requestIdentifier = requestIdentifier;
		this.clazz = clazz;
		this.httpRequestType = httpRequestType;
		this.url = url;
		this.urlParameters = urlParameters;
		this.headerParameters = headerParameters;
		this.bodyContentData = bodyContentData;
		
		this.requestResultStatus = FetchRequestResultEnum.UNKNOWN_ERROR;
		this.requestResultObjectContent = null;
		this.response = null;
		this.requestParameters = new RequestParameters();
		
		this.isMiTVAPICall = isMiTVAPICall;
		
		this.reportMetricsToTracker = reportMetricsToTracker;
		
		this.isRetry = isRetry;
		
		if(isMiTVAPICall)
		{
			/* Add the locale to the header data */
			Locale locale = LanguageUtils.getCurrentLocale();
			
			if(locale != null)
			{
				headerParameters.add(Constants.HTTP_REQUEST_DATA_LOCALE, locale.toString());
			}
			else
			{
				Log.w(TAG, "Locale has null value.");
			}
			
			/* Add the timezone offset */
			Integer timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			urlParameters.add(Constants.HTTP_REQUEST_DATA_TIME_ZONE_OFFSET, timeZoneOffsetInMinutes.toString());
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
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		Log.d(TAG, String.format("%s onPreExecute - Performing HTTP request: %s", clazz.getName(), requestIdentifier.getDescription()));
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		if(reportMetricsToTracker)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundNetworkRequestStart(this.getClass().getSimpleName());
		}
		
		if(Constants.FORCE_ENABLE_JSON_DATA_MOCKUPS_IF_AVAILABLE)
		{
			String responseString = FileUtils.getMockJSONString(getClass().getSimpleName());
			
			if(responseString != null)
			{
				int statusCode = FetchRequestResultEnum.SUCCESS.getStatusCode();
				
				response = new HTTPCoreResponse(url, statusCode, responseString);
			}
			else
			{
				response = HTTPCore.sharedInstance().executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData, isRetry);
			}
		}
		else
		{
			response = HTTPCore.sharedInstance().executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData, isRetry);
		}
		
		
		if(clazz.getName().contains("Compet")){
			Log.d(TAG, String.format("%s onPreExecute - Performing HTTP request: %s", clazz.getName(), requestIdentifier.getDescription()));
		}
		if(reportMetricsToTracker)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundNetworkRequestEnd(this.getClass().getSimpleName());
		}
		
		requestResultStatus = FetchRequestResultEnum.getFetchRequestResultEnumFromCode(response.getStatusCode());
		
		boolean wasSuccessful = requestResultStatus.wasSuccessful();
		boolean hasResponseString = response.hasResponseString();
		
		if(isMiTVAPICall)
		{
			/* 
			 * This is a backend restriction and should be changed. 
			 * In the future, appropriate error response codes should be returned.
			 * Avoid parsing the object in order not to cause an exception
			 */
			//TODO Backend support
			// When support from backend return appropriate error response
			if(requestResultStatus == FetchRequestResultEnum.BAD_REQUEST)
			{
				return null;
			}
		}
		
		if(hasResponseString)
		{
			String responseString = response.getResponseString();
			
			if(wasSuccessful)
			{	
				if(reportMetricsToTracker)
				{
					TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundJSONParsingStart(this.getClass().getSimpleName());
				}
				
				try
				{
					Log.d(TAG, String.format("%s doInBackground - Parsing JSON into model (using GSON)", clazz.getName()));
				
					requestResultObjectContent = gson.fromJson(responseString, clazz);
					
					Log.d(TAG, String.format("%s doInBackground - After parsing JSON into model (using GSON)", clazz.getName()));
				}
				catch(JsonSyntaxException jsex)
				{
					Log.e(TAG, jsex.getMessage(), jsex);

					requestResultStatus = FetchRequestResultEnum.UNKNOWN_ERROR;
					requestResultObjectContent = null;
				}
				
				if(reportMetricsToTracker)
				{
					TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundJSONParsingEnd(this.getClass().getSimpleName());
				}
			}
			else
			{
				requestResultObjectContent = new String(responseString);
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
		Log.d(TAG, String.format("%s onPostExecute - JSON parsing complete, notifying ContentManager", clazz.getName()));
		
		if(contentCallbackListener != null)
		{
			contentCallbackListener.onResult(activityCallbackListener, requestIdentifier, requestResultStatus, requestResultObjectContent, requestParameters);
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}	
	
	
	
	public RequestIdentifierEnum getRequestIdentifier()
	{
		return requestIdentifier;
	}
}