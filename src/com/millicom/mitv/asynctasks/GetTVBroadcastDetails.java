
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



public class GetTVBroadcastDetails
	extends AsyncTaskWithRelativeURL<TVBroadcastWithChannelInfo>
{
	private static String buildURL(
			TVChannelId tvChannelId,
			long beginTime)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_CHANNELS_ALL);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(tvChannelId.getChannelId());
		url.append(Consts.API_BROADCASTS);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(beginTime);
		
		return url.toString();
	}
	
	
	public GetTVBroadcastDetails(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			TVChannelId tvChannelId,
			long beginTime)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCAST_DETAILS, TVBroadcastWithChannelInfo.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvChannelId, beginTime));
	}
}