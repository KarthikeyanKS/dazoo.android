
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.asynctasks.GetTVBroadcastsFromSeries;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public class GetTVBroadcastsFromSeriesBuilder
{	
	private String tvSeriesId;
	
	
	
	public GetTVBroadcastsFromSeries build(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_SERIES);
		sb.append(tvSeriesId);
		sb.append(Consts.API_UPCOMING_BROADCASTS);
			
		GetTVBroadcastsFromSeries getTVBroadcastsFromSeries = new GetTVBroadcastsFromSeries(contentCallbackListener, activityCallBackListener, sb.toString());
		
		return getTVBroadcastsFromSeries;
	}



	public void setTvSeriesId(String tvSeriesId) 
	{
		this.tvSeriesId = tvSeriesId;
	}
}