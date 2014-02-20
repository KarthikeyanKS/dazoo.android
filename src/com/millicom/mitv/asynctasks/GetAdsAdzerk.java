package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.AdAdzerkJSON;
import com.mitv.Consts;



public class GetAdsAdzerk 
	extends AsyncTaskWithRelativeURL<AdAdzerkJSON> 
{	
	private static final String URL_SUFFIX = Consts.ADS_POST_URL;
	
	
	public GetAdsAdzerk(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS_ADZERK_GET, AdAdzerkJSON.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
	}
}