package com.mitv.test;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.Broadcast;
import com.millicom.mitv.models.gson.ImageSetOrientation;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVCredit;
import com.millicom.mitv.models.gson.TVProgram;
import com.millicom.mitv.models.gson.TVProgramAndChannelInfo;
import com.millicom.mitv.models.gson.TVSeries;
import com.millicom.mitv.models.gson.TVSeriesSeason;
import com.mitv.Consts;


/**
 * This test class tests the "broadcast details" call, which returns a list of objects with program (extends Broadcast) and channel fields
 * 
 * GET /epg/channels/{channelId}/broadcasts/{beginTimeMillis}
 * 
 * @author atsampikakis
 *
 */
public class TVProgramAndChannelInfoTest 
	extends TestBaseWithGuide 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private TVProgramAndChannelInfo tvBroadcastDetails;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
				
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		Broadcast broadcast = someGuide.getBroadcasts().get(0);
		String channelId = someGuide.getChannelId().getChannelId();
		Long beginTimeMillis = broadcast.getBeginTimeMillis();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_CHANNELS_ALL);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(channelId);
		sb.append(Consts.API_BROADCASTS);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(beginTimeMillis);
		String url = sb.toString();
		
		HTTPCoreResponse httpCoreResponse = executeRequestGet(url);
		
		String jsonString = httpCoreResponse.getResponseString();
				
		try
		{
			tvBroadcastDetails = new Gson().fromJson(jsonString, TVProgramAndChannelInfo.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvBroadcastDetails);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		TVChannel tvChannel = tvBroadcastDetails.getChannel();
		TVChannelGSONTest.testTVChannelObject(tvChannel);
		
		TVProgram tvProgram = tvBroadcastDetails.getTvProgram();
		testTVProgram(tvProgram);
	}
	
	public static void testTVProgram(TVProgram program) {
		/* All program types */
		Assert.assertNotNull(program.getProgramId());
		Assert.assertFalse(TextUtils.isEmpty(program.getProgramId()));
		
		Assert.assertNotNull(program.getTitle());
		Assert.assertFalse(TextUtils.isEmpty(program.getTitle()));
		
		Assert.assertNotNull(program.getSynopsisShort());
		Assert.assertFalse(TextUtils.isEmpty(program.getSynopsisShort()));
		
		Assert.assertNotNull(program.getSynopsisLong());
		Assert.assertFalse(TextUtils.isEmpty(program.getSynopsisLong()));
		
		/* All program types - Images */
		ImageSetOrientation images = program.getImages();
		testImages(images);
		
		/* All program types - tags */
		Assert.assertNotNull(program.getTags());
		Assert.assertFalse(TextUtils.isEmpty(program.getTags().toString())); /* Needed? */
		
		/* All program types - credits */
		for(TVCredit tvCredit : program.getCredits()) {
			testCredits(tvCredit);
		}
		
		/* Test depending on programType */
		ProgramTypeEnum programType = program.getProgramType();
		switch (programType) {
			case MOVIE: {
				testTypeMovie(program);
				break;
			}
			case TV_EPISODE: {
				testTypeTVEpisode(program);
				break;
			}
			case SPORT: {
				testTypeSport(program);
				break;
			}
			case OTHER: {
				testTypeOther(program);
				break;
			}
			case UNKNOWN:
			default: {
				/* Do nothing */
			}
		}
	}
	public static void testImages(ImageSetOrientation images) {
		Assert.assertNotNull(images);
		
		/* Portrait */
		Assert.assertNotNull(images.getPortrait().getSmall());
		Assert.assertFalse(TextUtils.isEmpty(images.getPortrait().getSmall()));
		
		Assert.assertNotNull(images.getPortrait().getMedium());
		Assert.assertFalse(TextUtils.isEmpty(images.getPortrait().getMedium()));
		
		Assert.assertNotNull(images.getPortrait().getLarge());
		Assert.assertFalse(TextUtils.isEmpty(images.getPortrait().getLarge()));
		
		/* Landscape */
		Assert.assertNotNull(images.getLandscape().getSmall());
		Assert.assertFalse(TextUtils.isEmpty(images.getLandscape().getSmall()));
		
		Assert.assertNotNull(images.getLandscape().getMedium());
		Assert.assertFalse(TextUtils.isEmpty(images.getLandscape().getMedium()));
		
		Assert.assertNotNull(images.getLandscape().getLarge());
		Assert.assertFalse(TextUtils.isEmpty(images.getLandscape().getLarge()));
	}
	
	public static void testCredits(TVCredit tvCredit) {
		Assert.assertNotNull(tvCredit.getName());
		Assert.assertFalse(TextUtils.isEmpty(tvCredit.getName()));

		Assert.assertNotNull(tvCredit.getType());
		Assert.assertFalse(TextUtils.isEmpty(tvCredit.getType()));
	}
	
	public static void testTypeMovie(TVProgram program) {
		Assert.assertNotNull(program.getYear());
		Assert.assertTrue(program.getYear() != 0);
		
		Assert.assertNotNull(program.getGenre());
		Assert.assertFalse(TextUtils.isEmpty(program.getGenre()));
	}
	
	public static void testTypeOther(TVProgram program) {
		Assert.assertNotNull(program.getCategory());
		Assert.assertFalse(TextUtils.isEmpty(program.getCategory()));
	}
	
	public static void testTypeTVEpisode(TVProgram program) {
		testTVSeries(program.getSeries());
		testTVSeason(program.getSeason());
		
		Assert.assertNotNull(program.getEpisodeNumber());
		Assert.assertTrue(program.getEpisodeNumber() != 0);
	}

	public static void testTypeSport(TVProgram program) {
		Assert.assertNotNull(program.getSportType());
		Assert.assertFalse(TextUtils.isEmpty(program.getSportType().toString()));
		
		Assert.assertNotNull(program.getTournament());
		Assert.assertFalse(TextUtils.isEmpty(program.getTournament()));
	}

	public static void testTVSeries(TVSeries series) {
		Assert.assertNotNull(series.getSeriesId());
		Assert.assertFalse(TextUtils.isEmpty(series.getSeriesId()));
		
		Assert.assertNotNull(series.getName());
		Assert.assertFalse(TextUtils.isEmpty(series.getName()));
	}
	
	public static void testTVSeason(TVSeriesSeason season) {
		Assert.assertNotNull(season.getNumber());
		Assert.assertTrue(season.getNumber() != 0);
	}
	
}
