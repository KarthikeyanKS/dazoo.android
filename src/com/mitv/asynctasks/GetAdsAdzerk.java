package com.mitv.asynctasks;


import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.AdAdzerk;



public class GetAdsAdzerk 
	extends AsyncTaskWithRelativeURL<AdAdzerk> 
{	
	private static final String URL_SUFFIX = Constants.ADS_POST_URL;
	
	
	public GetAdsAdzerk(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.ADS_ADZERK_GET, AdAdzerk.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
	}
}