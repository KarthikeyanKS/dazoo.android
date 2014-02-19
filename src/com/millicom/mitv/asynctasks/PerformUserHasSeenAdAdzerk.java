
package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;



public class PerformUserHasSeenAdAdzerk 
	extends AsyncTaskBase<DummyData> 
{	
	public PerformUserHasSeenAdAdzerk(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS_ADZERK_SEEN, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, false, url);
	}
}