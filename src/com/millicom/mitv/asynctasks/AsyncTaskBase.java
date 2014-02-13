package com.millicom.mitv.asynctasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;

public class AsyncTaskBase<T> extends AsyncTask<String, Void, Void> 
{	
	private boolean isRelativeURL;
	private String url;
	protected URLParameters urlParameters;
	private Gson gson;
	private Class<T> clazz;
	private ContentCallbackListener contentCallbackListener;
	private ActivityCallbackListener activityCallBackListener;
	private RequestIdentifierEnum requestIdentifier;
	
	protected FetchRequestResultEnum requestResult;
	protected Object content;
	
	
	
	public AsyncTaskBase(ContentCallbackListener contentCallbackListener, ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, Class<T> clazz, boolean isRelativeURL, String url) {
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, isRelativeURL, url, new URLParameters());
	}
	
	public AsyncTaskBase(ContentCallbackListener contentCallbackListener, ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, Class<T> clazz, boolean isRelativeURL, String url, URLParameters urlParameters) {
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallBackListener = activityCallBackListener;
		this.isRelativeURL = isRelativeURL;
		this.url = url;
		this.urlParameters = urlParameters;
		this.requestIdentifier = requestIdentifier;
		this.requestResult = FetchRequestResultEnum.UNKNOWN_ERROR;
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		try {
			gsonBuilder.registerTypeAdapter(clazz, clazz.newInstance());
			this.gson = gsonBuilder.create();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Void doInBackground(String... params) {
		/* Use fancy HTTPCore stuff */
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		contentCallbackListener.onResult(activityCallBackListener, requestIdentifier, requestResult, content);
	}
	
	
}