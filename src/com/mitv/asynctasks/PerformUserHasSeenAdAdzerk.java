
package com.mitv.asynctasks;


import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.DummyData;



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