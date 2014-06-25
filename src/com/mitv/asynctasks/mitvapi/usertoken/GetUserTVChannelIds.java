
package com.mitv.asynctasks.mitvapi.usertoken;



import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVChannelId;



public class GetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId[]> 
{
	@SuppressWarnings("unused")
	private static final String TAG = GetUserTVChannelIds.class.getName();
	
	
	private static final String URL_SUFFIX = Constants.URL_MY_CHANNEL_IDS;
	
	
	
	public GetUserTVChannelIds(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			boolean standalone,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standalone), TVChannelId[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
	}
	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standalone)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(standalone)
		{
			requestIdentifier = RequestIdentifierEnum.TV_CHANNEL_IDS_USER_STANDALONE;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL;
		}
		
		return requestIdentifier;
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		}
		
		super.doInBackground(params);
				
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		}
		
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		}
		
		super.onPostExecute(result);
		
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
		}
	}
}