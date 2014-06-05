
package com.mitv.asynctasks.mitvapi;


import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.DummyData;



public class PerformUserHasSeenAdAdzerk 
	extends AsyncTaskBase<DummyData> 
{	
	public PerformUserHasSeenAdAdzerk(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String url,
			boolean isRetry) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.ADS_ADZERK_SEEN, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, url, false, isRetry);
	}
}