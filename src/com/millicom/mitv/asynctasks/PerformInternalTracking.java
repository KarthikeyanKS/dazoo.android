package com.millicom.mitv.asynctasks;


import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.mitv.Consts;



public class PerformInternalTracking extends AsyncTaskBase<DummyData> 
{	
	private static final String URL_SUFFIX = Consts.URL_INTERNAL_TRACKING;
	
	public PerformInternalTracking(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.ADS, DummyData.class, false, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		return null;
	}
}