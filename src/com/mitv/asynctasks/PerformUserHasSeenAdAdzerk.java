
package com.mitv.asynctasks;


import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.DummyData;



public class PerformUserHasSeenAdAdzerk 
	extends AsyncTaskBase<DummyData> 
{	
	public PerformUserHasSeenAdAdzerk(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.ADS_ADZERK_SEEN, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, false, url);
	}
}