package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.mitv.Consts;



public class PerformInternalTracking 
	extends AsyncTaskBase<DummyData> 
{	
	private static final String URL_SUFFIX = Consts.URL_INTERNAL_TRACKING;
	
	
	public PerformInternalTracking(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String tvProgramId,
			String deviceId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.INTERNAL_TRACKING, DummyData.class, HTTPRequestTypeEnum.HTTP_GET, false, URL_SUFFIX);
			
		this.urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_KEY, Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_VALUE_VIEWS);
		this.urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_KEY, Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VALUE_PROGRAM_ID);
		this.urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_VALUE, tvProgramId);
		this.urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_UID, deviceId);
	}
}