package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldAdzerkAd;



public class GetAds 
	extends AsyncTaskWithRelativeURL<OldAdzerkAd> 
{	
	private static final String URL_SUFFIX = Consts.URL_ACTIVITY_FEED;
	
	
	public GetAds(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS, OldAdzerkAd.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}