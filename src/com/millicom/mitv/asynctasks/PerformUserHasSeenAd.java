package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;



public class PerformUserHasSeenAd extends AsyncTaskBase<DummyData> 
{	
	public PerformUserHasSeenAd(
			String url,
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS, DummyData.class, false, url);
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		return null;
	}
}