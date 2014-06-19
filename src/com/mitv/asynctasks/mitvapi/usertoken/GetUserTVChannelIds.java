
package com.mitv.asynctasks.mitvapi.usertoken;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVChannelId;



public class GetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId[]> 
{
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
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL && Constants.USE_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		}
		
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVChannelId[] contentAsArray = (TVChannelId[]) requestResultObjectContent;
			
			ArrayList<TVChannelId> contentAsArrayList = new ArrayList<TVChannelId>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL && Constants.USE_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		}
		
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL &&  Constants.USE_DETAILED_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		}
		
		super.onPostExecute(result);
		
		if(getRequestIdentifier() == RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL &&  Constants.USE_DETAILED_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
		}
	}
}