
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.asynctasks.builders.GetTVBroadcastsFromSeriesBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.model.OldBroadcast;



public class GetTVBroadcastsFromSeries 
	extends AsyncTaskWithRelativeURL<OldBroadcast>
{
	
	public static GetTVBroadcastsFromSeries newGetTVBroadcastsFromSeriesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String tvSeriesId) 
	{
		GetTVBroadcastsFromSeriesBuilder getTVBroadcastsFromSeriesBuilder = new GetTVBroadcastsFromSeriesBuilder();
		getTVBroadcastsFromSeriesBuilder.setTvSeriesId(tvSeriesId);
		
		GetTVBroadcastsFromSeries getTVBroadcastsFromSeries = getTVBroadcastsFromSeriesBuilder.build(contentCallbackListener, activityCallBackListener);
		
		return getTVBroadcastsFromSeries;
	}
	
	
	
	public GetTVBroadcastsFromSeries(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, OldBroadcast.class, HTTPRequestTypeEnum.HTTP_GET, url);
	}
}