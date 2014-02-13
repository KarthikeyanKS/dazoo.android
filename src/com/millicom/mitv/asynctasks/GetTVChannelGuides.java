package com.millicom.mitv.asynctasks;

import java.util.List;

import com.millicom.mitv.asynctasks.builders.GetTVChannelGuidesBuilder;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;
import com.mitv.model.TVChannelGuide;
import com.mitv.model.TVDate;

public class GetTVChannelGuides extends AsyncTaskWithRelativeURL<TVChannelGuide> {
	
	private static final String URL_SUFFIX = Consts.URL_GUIDE;
	
	
	public static GetTVChannelGuides newGetTVChannelGuidesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			TVDate tvDate,
			List<TVChannelId> tvChannelIds) {
		GetTVChannelGuidesBuilder channelGuideBuilder = new GetTVChannelGuidesBuilder();
		channelGuideBuilder.setTVDate(tvDate);
		channelGuideBuilder.setTVChannelIds(tvChannelIds);
		
		GetTVChannelGuides getChannelGuides = channelGuideBuilder.build(contentCallbackListener, activityCallBackListener);
		return getChannelGuides;
	}

	public GetTVChannelGuides(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) {
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_GUIDE, TVChannelGuide.class, URL_SUFFIX, new URLParameters());
	}
		
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

	public void setUrlParameters(URLParameters urlParameters) {
		this.urlParameters = urlParameters;
	}
}
