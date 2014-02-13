package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.AdzerkAd;



public class GetAds extends AsyncTaskWithRelativeURL<AdzerkAd> 
{	
	private static final String URL_SUFFIX = Consts.URL_ACTIVITY_FEED;
	
	public GetAds(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS, AdzerkAd.class, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		return null;
	}
}