
package com.millicom.mitv.asynctasks;



import java.util.List;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;
import com.mitv.model.OldTVChannelGuide;



public class GetTVChannelGuides 
	extends AsyncTaskWithRelativeURL<OldTVChannelGuide>
{	
	private static String buildURL(TVDate tvDate)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_GUIDE);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(tvDate.getId());
		
		return url.toString();
	}

	
	
	public GetTVChannelGuides(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			TVDate tvDate,
			List<TVChannelId> tvChannelIds) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_GUIDE, OldTVChannelGuide.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvDate));
		
		for(TVChannelId tvChannelId : tvChannelIds) 
		{
			String tvChannelIdAsString = tvChannelId.getChannelId();
			
			this.urlParameters.add(Consts.API_CHANNEL_ID, tvChannelIdAsString);
		}
	}
}