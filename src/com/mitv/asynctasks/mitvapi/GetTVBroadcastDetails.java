
package com.mitv.asynctasks.mitvapi;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelId;



public class GetTVBroadcastDetails
	extends AsyncTaskBase<TVBroadcastWithChannelInfo>
{
	private static final String TAG = GetTVBroadcastDetails.class.getName();
	
	
	
	private static String buildURL(
			TVChannelId tvChannelId,
			long beginTime)
	{
		StringBuilder url = new StringBuilder();
		
		url.append(Constants.URL_CHANNELS_ALL)
		.append(Constants.FORWARD_SLASH);
		
		if(tvChannelId != null)
		{
			url.append(tvChannelId.getChannelId());
		}
		else
		{
			Log.w(TAG, "TVChannel is null");
		}
		
		url.append(Constants.API_BROADCASTS)
		.append(Constants.FORWARD_SLASH)
		.append(beginTime);
		
		return url.toString();
	}
	
	
	
	public GetTVBroadcastDetails(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			TVChannelId tvChannelId,
			long beginTime,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.BROADCAST_DETAILS, TVBroadcastWithChannelInfo.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvChannelId, beginTime), false, retryThreshold);
	}
}