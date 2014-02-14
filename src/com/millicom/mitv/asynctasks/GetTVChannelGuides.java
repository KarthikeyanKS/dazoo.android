
package com.millicom.mitv.asynctasks;



import java.util.List;

import com.millicom.mitv.asynctasks.builders.GetTVChannelGuidesBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;
import com.mitv.model.OldTVChannelGuide;
import com.mitv.model.OldTVDate;



public class GetTVChannelGuides 
	extends AsyncTaskWithRelativeURL<OldTVChannelGuide>
{	
	private static final String URL_SUFFIX = Consts.URL_GUIDE;
	
	
	public static GetTVChannelGuides newGetTVChannelGuidesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			OldTVDate tvDate,
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
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_GUIDE, OldTVChannelGuide.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}

	
	
	public void setUrlParameters(URLParameters urlParameters) 
	{
		this.urlParameters = urlParameters;
	}
}