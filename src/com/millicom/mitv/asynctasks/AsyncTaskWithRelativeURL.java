package com.millicom.mitv.asynctasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;

public class AsyncTaskWithRelativeURL<T> extends AsyncTaskBase<T>{
	
	public AsyncTaskWithRelativeURL(ContentCallbackListener contentCallbackListener, ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, Class<T> clazz, String url) {
		this(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, url, new URLParameters());
	}
	
	public AsyncTaskWithRelativeURL(ContentCallbackListener contentCallbackListener, ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, Class<T> clazz, String url, URLParameters urlParameters) {
		super(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, true, url, urlParameters);
	}
} 
