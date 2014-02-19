
package com.millicom.mitv.asynctasks;



import java.util.List;

import com.millicom.mitv.asynctasks.builders.GetTVChannelGuidesBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.model.OldTVChannelGuide;



public class GetTVChannelGuides 
	extends AsyncTaskWithRelativeURL<OldTVChannelGuide>
{	
	public static GetTVChannelGuides newGetTVChannelGuidesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			TVDate tvDate,
			List<TVChannelId> tvChannelIds) 
	{
		GetTVChannelGuidesBuilder channelGuideBuilder = new GetTVChannelGuidesBuilder();
		channelGuideBuilder.setTVDate(tvDate);
		channelGuideBuilder.setTVChannelIds(tvChannelIds);
		
		GetTVChannelGuides getChannelGuides = channelGuideBuilder.build(contentCallbackListener, activityCallBackListener);
		
		return getChannelGuides;
	}

	
	
	public GetTVChannelGuides(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener, final String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_GUIDE, OldTVChannelGuide.class, HTTPRequestTypeEnum.HTTP_GET, url);
	}

	
	
	public void setUrlParameters(URLParameters urlParameters) 
	{
		this.urlParameters = urlParameters;
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		/* TVGuide is created */
		
		return null;
	}
}
