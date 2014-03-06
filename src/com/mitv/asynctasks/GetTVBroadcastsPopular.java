
package com.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.comparators.TVBroadcastComparatorByTime;



public class GetTVBroadcastsPopular 
	extends AsyncTaskWithRelativeURL<TVBroadcastWithChannelInfo[]> 
{	
	private static final String TAG = GetTVBroadcastsPopular.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_POPULAR;

	
	public GetTVBroadcastsPopular(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.POPULAR_ITEMS, TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
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

		return null;
	}
}