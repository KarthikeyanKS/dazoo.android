package com.mitv.test.gson;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVProgram;

/**
 * This class tests the fetched upcoming broadcasts from series. Returns a list of fields from broadcast, channel object and program object.
 * 
 * GET GET /epg/series/{series}/broadcasts/upcoming
 * 
 * @author atsampikakis
 *
 */
public class TVSeriesUpcomingBroadcastGSONTest extends TestBaseWithGuide {

	private static final String TAG = TVSeriesUpcomingBroadcastGSONTest.class.getName();
	
	
	private List<TVBroadcastWithChannelInfo> tvSeriesUpcomingBroadcasts;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String seriesId = getSomeSeriesId();

		Assert.assertNotNull(seriesId);
		if (seriesId != null) {

			StringBuilder sb = new StringBuilder();
			sb.append(Constants.URL_SERIES);
			sb.append(seriesId);
			sb.append(Constants.API_UPCOMING_BROADCASTS);
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
		for (TVBroadcastWithChannelInfo broadcastWithChannelInfo : tvSeriesUpcomingBroadcasts) {
			TVChannel tvChannel = broadcastWithChannelInfo.getChannel();
			assertTrue(tvChannel.areDataFieldsValid());

			assertTrue(broadcastWithChannelInfo.areDataFieldsValid());
		}
	}
}
