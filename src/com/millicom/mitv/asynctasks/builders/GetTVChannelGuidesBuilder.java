package com.millicom.mitv.asynctasks.builders;

import java.util.List;

import com.millicom.mitv.asynctasks.GetTVChannelGuides;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;
import com.mitv.model.TVDate;

public class GetTVChannelGuidesBuilder {
	
	private TVDate tvDate;
	private List<TVChannelId> tvChannelIds;
	
	public void setTVDate(TVDate tvDate) {
		this.tvDate = tvDate;
	}
	
	public void setTVChannelIds(List<TVChannelId> tvChannelIds) {
		this.tvChannelIds = tvChannelIds;
	}
	
	public GetTVChannelGuides build(ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) {
		GetTVChannelGuides getChannelGuides = new GetTVChannelGuides(contentCallbackListener, activityCallBackListener);
		URLParameters urlParameters = new URLParameters();
		
		//TODO check if getDate is what we want to do
		urlParameters.add("", tvDate.getDate());
		for(TVChannelId tvChannelId : tvChannelIds) {
			String tvChannelIdAsString = tvChannelId.getTvChannelId();
			urlParameters.add(Consts.API_CHANNEL_ID, tvChannelIdAsString);
		}
		
		getChannelGuides.setUrlParameters(urlParameters);
		
		return getChannelGuides;
	}	
}
