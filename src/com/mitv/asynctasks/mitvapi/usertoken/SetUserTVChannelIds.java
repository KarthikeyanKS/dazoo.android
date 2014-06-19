
package com.mitv.asynctasks.mitvapi.usertoken;



import java.util.List;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.DummyData;
import com.mitv.models.objects.mitvapi.TVChannelId;



public class SetUserTVChannelIds 
	extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String TAG = SetUserTVChannelIds.class.getName();
	
	
	private static final String URL_SUFFIX = Constants.URL_MY_CHANNEL_IDS;
	

	
	public SetUserTVChannelIds(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final List<TVChannelId> channelIds,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_SET_CHANNELS, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
				
		this.bodyContentData = gson.toJson(channelIds);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		}
	
		super.doInBackground(params);

		if (Constants.USE_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		}
		
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		if (Constants.USE_DETAILED_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		}
		
		super.onPostExecute(result);

		if (Constants.USE_DETAILED_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
		}
	}
}