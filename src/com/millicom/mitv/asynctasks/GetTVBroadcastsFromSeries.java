
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldBroadcast;



public class GetTVBroadcastsFromSeries 
	extends AsyncTaskWithRelativeURL<OldBroadcast>
{
	
	private static String buildURL(String tvSeriesId)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_SERIES);
		url.append(tvSeriesId);
		url.append(Consts.API_UPCOMING_BROADCASTS);
		
		return url.toString();
	}
	
	
	
	public GetTVBroadcastsFromSeries(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String tvSeriesId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, OldBroadcast.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvSeriesId));
	}
}