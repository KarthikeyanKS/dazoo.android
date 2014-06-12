
package com.mitv.asynctasks.mitvapi;



import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.comparators.TVBroadcastComparatorByTime;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class GetTVBroadcastsPopular 
	extends AsyncTaskBase<TVBroadcastWithChannelInfo[]> 
{	
	private static final String TAG = GetTVBroadcastsPopular.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_POPULAR;

	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standalone)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(standalone)
		{
			requestIdentifier = RequestIdentifierEnum.POPULAR_ITEMS_STANDALONE;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.POPULAR_ITEMS_INITIAL_CALL;
		}
		
		return requestIdentifier;
	}
	
	
	
	public GetTVBroadcastsPopular(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean standalone,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standalone), TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		if(getRequestIdentifier() == RequestIdentifierEnum.POPULAR_ITEMS_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		}
		
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVBroadcastWithChannelInfo[] contentAsArray = (TVBroadcastWithChannelInfo[]) requestResultObjectContent;
			
			/* Filter out old popular broadcasts, we only need from todays date */
			ArrayList<TVBroadcastWithChannelInfo> contentAsArrayList = new ArrayList<TVBroadcastWithChannelInfo>();
			
			for(int i = 0; i < contentAsArray.length; ++i)
			{
				TVBroadcastWithChannelInfo broadcast = contentAsArray[i];
				
				if(!broadcast.hasEnded())
				{
					contentAsArrayList.add(broadcast);
				}
			}
			
			/* Sort the broadcasts according to start time */
			Collections.sort(contentAsArrayList, new TVBroadcastComparatorByTime());
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		if(getRequestIdentifier() == RequestIdentifierEnum.POPULAR_ITEMS_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		}
		
		return null;
	}

	
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(getRequestIdentifier() == RequestIdentifierEnum.POPULAR_ITEMS_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		}
		
		super.onPostExecute(result);
		
		if(getRequestIdentifier() == RequestIdentifierEnum.POPULAR_ITEMS_INITIAL_CALL)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
		}
	}
}