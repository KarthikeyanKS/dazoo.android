package com.millicom.mitv.asynctasks;

import java.util.List;

import com.millicom.mitv.asynctasks.builders.GetTVChannelGuidesBuilder;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;
import com.mitv.model.TVChannelGuide;
import com.mitv.model.TVDate;

public class GetTVChannelGuides extends AsyncTaskBase<TVChannelGuide> {
	
	private static final String URL_SUFFIX = Consts.URL_GUIDE;
	
	
	public static GetTVChannelGuides getTVChannelGuide(TVDate tvDate, List<TVChannelId> tvChannelIds) {
		GetTVChannelGuidesBuilder channelGuideBuilder = new GetTVChannelGuidesBuilder();
		channelGuideBuilder.setTVDate(tvDate);
		channelGuideBuilder.setTVChannelIds(tvChannelIds);
		GetTVChannelGuides getChannelGuides = channelGuideBuilder.build();
		return getChannelGuides;
	}

	public GetTVChannelGuides() {
		super(TVChannelGuide.class, URL_SUFFIX, new URLParameters());
	}
		
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		/* Parse JSON data using GSON */
	}
		
	public void setUrlParameters(URLParameters urlParameters) {
		this.urlParameters = urlParameters;
	}
}
