
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.asynctasks.GetTVBroadcastDetails;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public class GetTVBroadcastsDetailsBuilder
{	
	private String tvChannelId;
	private long beginTime;
	
	
	
	public GetTVBroadcastDetails build(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_CHANNELS_ALL);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(tvChannelId);
		sb.append(Consts.API_BROADCASTS);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(beginTime);
				
		GetTVBroadcastDetails getTVBroadcastDetails = new GetTVBroadcastDetails(contentCallbackListener, activityCallBackListener, sb.toString());
		
		return getTVBroadcastDetails;
	}



	public void setTvChannelId(String tvChannelId) 
	{
		this.tvChannelId = tvChannelId;
	}


	
	public void setBeginTime(long beginTime) 
	{
		this.beginTime = beginTime;
	}	
}