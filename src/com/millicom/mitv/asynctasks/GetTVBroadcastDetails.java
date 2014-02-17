
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.asynctasks.builders.GetTVBroadcastsDetailsBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.BroadcastDetails;
import com.millicom.mitv.models.gson.TVChannelId;



public class GetTVBroadcastDetails
	extends AsyncTaskWithRelativeURL<BroadcastDetails>
{
	
	
	public static GetTVBroadcastDetails newGetTVBroadcastDetailsTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			TVChannelId tvChannelId,
			long beginTime)
	{
		GetTVBroadcastsDetailsBuilder getTVBroadcastsDetailsBuilder = new GetTVBroadcastsDetailsBuilder();
		getTVBroadcastsDetailsBuilder.setTvChannelId(tvChannelId.getChannelId());
		getTVBroadcastsDetailsBuilder.setBeginTime(beginTime);
		
		GetTVBroadcastDetails getTVBroadcastDetails = getTVBroadcastsDetailsBuilder.build(contentCallbackListener, activityCallBackListener);
		
		return getTVBroadcastDetails;
	}
	
	
	
	public GetTVBroadcastDetails(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCAST_DETAILS, BroadcastDetails.class, HTTPRequestTypeEnum.HTTP_GET, url);
	}
}