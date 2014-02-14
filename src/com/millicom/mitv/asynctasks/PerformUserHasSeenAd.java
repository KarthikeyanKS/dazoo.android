
package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;



public class PerformUserHasSeenAd 
	extends AsyncTaskBase<DummyData> 
{	
	public PerformUserHasSeenAd(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, false, url);
	}

	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}