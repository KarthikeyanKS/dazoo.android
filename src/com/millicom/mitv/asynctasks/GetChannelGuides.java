package com.millicom.mitv.asynctasks;

import java.util.List;

import com.millicom.mitv.asynctasks.builders.GetChannelGuidesBuilder;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;
import com.mitv.model.ChannelGuide;
import com.mitv.model.TVDate;

public class GetChannelGuides extends AsyncTaskBase<ChannelGuide> {
	
	private static final String URL_SUFFIX = Consts.URL_GUIDE;
	
	
	public static GetChannelGuides getChannelGuide(TVDate tvDate, List<TVChannelId> tvChannelIds) {
		GetChannelGuidesBuilder channelGuideBuilder = new GetChannelGuidesBuilder();
		channelGuideBuilder.setTVDate(tvDate);
		channelGuideBuilder.setTVChannelIds(tvChannelIds);
		GetChannelGuides getChannelGuides = channelGuideBuilder.build();
		return getChannelGuides;
	}

	public GetChannelGuides() {
		super(ChannelGuide.class, URL_SUFFIX, new URLParameters());
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
