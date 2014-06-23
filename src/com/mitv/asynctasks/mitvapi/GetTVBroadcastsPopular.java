
package com.mitv.asynctasks.mitvapi;



import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.comparators.TVBroadcastComparatorByTime;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class GetTVBroadcastsPopular 
	extends AsyncTaskBase<TVBroadcastWithChannelInfo[]> 
{	
	private static final String TAG = GetTVBroadcastsPopular.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_POPULAR;

	
	
	public GetTVBroadcastsPopular(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.POPULAR_ITEMS_STANDALONE, TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
	}
	
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			@SuppressWarnings("unchecked")
			ArrayList<TVBroadcastWithChannelInfo> contentFromService = (ArrayList<TVBroadcastWithChannelInfo>) requestResultObjectContent;
			
			ArrayList<TVBroadcastWithChannelInfo> contentToReturn = new ArrayList<TVBroadcastWithChannelInfo>();
			
			/* Filter out old popular broadcasts, we only need from todays date */
			for(int i = 0; i < contentFromService.size(); ++i)
			{
				TVBroadcastWithChannelInfo broadcast = contentFromService.get(i);
				
				if(!broadcast.hasEnded())
				{
					contentToReturn.add(broadcast);
				}
			}
			
			/* Sort the broadcasts according to start time */
			Collections.sort(contentToReturn, new TVBroadcastComparatorByTime());
			
			requestResultObjectContent = contentToReturn;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}

	
	
	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);
	}
}