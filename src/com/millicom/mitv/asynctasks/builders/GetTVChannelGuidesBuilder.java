package com.millicom.mitv.asynctasks.builders;

import java.util.List;

import com.millicom.mitv.asynctasks.GetTVChannelGuides;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;



public class GetTVChannelGuidesBuilder
{	
	private TVDate tvDate;
	private List<TVChannelId> tvChannelIds;
	
	
	
	public void setTVDate(TVDate tvDate) 
	{
		this.tvDate = tvDate;
	}
	
	public void setTVChannelIds(List<TVChannelId> tvChannelIds)
	{
		this.tvChannelIds = tvChannelIds;
	}
	
	public GetTVChannelGuides build(ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_GUIDE);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(tvDate.getId());
		String url = sb.toString();
		
		GetTVChannelGuides getChannelGuides = new GetTVChannelGuides(contentCallbackListener, activityCallBackListener, url);
		URLParameters urlParameters = new URLParameters();
			
		for(TVChannelId tvChannelId : tvChannelIds) 
		{
			String tvChannelIdAsString = tvChannelId.getChannelId();
			urlParameters.add(Consts.API_CHANNEL_ID, tvChannelIdAsString);
		}
		
		getChannelGuides.setUrlParameters(urlParameters);
		
		return getChannelGuides;
	}	
}
