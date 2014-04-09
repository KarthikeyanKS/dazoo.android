
package com.mitv.asynctasks.mitvapi;



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
	private static String buildURL(
			TVChannelId tvChannelId,
			long beginTime)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_CHANNELS_ALL);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(tvChannelId.getChannelId());
		url.append(Constants.API_BROADCASTS);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(beginTime);
		
		return url.toString();
	}
	
	
	public GetTVBroadcastDetails(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			TVChannelId tvChannelId,
			long beginTime)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.BROADCAST_DETAILS, TVBroadcastWithChannelInfo.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvChannelId, beginTime));
	}
}