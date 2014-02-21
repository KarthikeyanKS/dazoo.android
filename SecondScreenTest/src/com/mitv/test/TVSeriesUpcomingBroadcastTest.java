package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;

/**
 * This class tests the fetched upcoming broadcasts from series. Returns a list of fields from broadcast, channel object and program object.
 * 
 * GET GET /epg/series/{series}/broadcasts/upcoming
 * 
 * @author atsampikakis
 *
 */
public class TVSeriesUpcomingBroadcastTest extends TestBaseWithGuide {

	private List<TVBroadcastWithChannelInfo> tvSeriesUpcomingBroadcasts;
	private static final String TAG = "TVSeriesUpcomingBroadcastTest";

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String seriesId = getSomeSeriesId();

		Assert.assertNotNull(seriesId);
		if (seriesId != null) {

			StringBuilder sb = new StringBuilder();
			sb.append(Consts.URL_SERIES);
			sb.append(seriesId);
			sb.append(Consts.API_UPCOMING_BROADCASTS);
			String url = sb.toString();

			HTTPCoreResponse httpCoreResponse = executeRequestGet(url);

			String jsonString = httpCoreResponse.getResponseString();

			try {
				tvSeriesUpcomingBroadcasts = Arrays.asList(new Gson().fromJson(
						jsonString, TVBroadcastWithChannelInfo[].class));
			} catch (JsonSyntaxException jsex) {
				Log.e(TAG, jsex.getMessage(), jsex);
			}
		}
	}

	private String getSomeSeriesId() {
		for (TVChannelGuide someGuide : tvChannelGuides) {
			for (TVBroadcast broadcast : someGuide.getBroadcasts()) {
				TVProgram tvProgram = broadcast.getProgram();
				if (tvProgram.getProgramType() == ProgramTypeEnum.TV_EPISODE) {
					if (tvProgram.getSeries() != null
							&& tvProgram.getSeries().getSeriesId() != null) {
						String seriesId = tvProgram.getSeries().getSeriesId();
						return seriesId;
					}
				}
			}
		}
		return null;
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvSeriesUpcomingBroadcasts);
	}

	@Test
	public void testAllVariablesNotNull() {
		for (TVBroadcastWithChannelInfo tvProgramBroadcast : tvSeriesUpcomingBroadcasts) {
			TVChannel tvChannel = tvProgramBroadcast.getChannel();
			TVChannelGSONTest.testTVChannelObject(tvChannel);

			TVBroadcastWithChannelInfoTest.testBroadcast(tvProgramBroadcast);
		}
	}
}
