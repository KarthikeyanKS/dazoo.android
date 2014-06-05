package com.mitv.asynctasks.mitvapi;


import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.DummyData;



public class PerformInternalTracking 
	extends AsyncTaskBase<DummyData> 
{	
	private static final String URL_SUFFIX = Constants.URL_INTERNAL_TRACKING;
	
	
	public PerformInternalTracking(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String tvProgramId,
			String deviceId,
			boolean isRetry) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.INTERNAL_TRACKING, DummyData.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, false, isRetry);
			
		this.urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_KEY, Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_VALUE_VIEWS);
		this.urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_KEY, Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VALUE_PROGRAM_ID);
		this.urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_VALUE, tvProgramId);
		this.urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_UID, deviceId);
	}
}