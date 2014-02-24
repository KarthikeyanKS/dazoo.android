package com.mitv.test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;

/**
 * This class tests the fetch broadcasts from program with channel info. Returns a list of fields from broadcast and channel object.
 * 
 * GET /epg/programs/{programid}/broadcasts
 * 
 * @author atsampikakis
 *
 */
public class RepetitionsOfBroadcastTest extends TestBaseWithGuide {
	
	private List<TVBroadcastWithChannelInfo> tvProgramBroadcasts;
	private static final String	TAG	= "TVBroadcastProgramTest";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		TVBroadcast broadcast = someGuide.getBroadcasts().get(0);
		TVProgram tvProgram = broadcast.getProgram();
		String programId = tvProgram.getProgramId();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_PROGRAMS);
		sb.append(programId);
		sb.append(Consts.API_BROADCASTS);
		String url = sb.toString();
				
		HTTPCoreResponse httpCoreResponse = executeRequestGet(url);
		
		String jsonString = httpCoreResponse.getResponseString();
				
		try
		{
			tvProgramBroadcasts = Arrays.asList(new Gson().fromJson(jsonString, TVBroadcastWithChannelInfo[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvProgramBroadcasts);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVBroadcastWithChannelInfo tvProgramBroadcast : tvProgramBroadcasts) {
			TVChannel tvChannel = tvProgramBroadcast.getChannel();
			TVChannelGSONTest.testTVChannelObject(tvChannel);
			
			testBroadcast(tvProgramBroadcast);
		}
	}
	
	public static void testBroadcast(TVBroadcast broadcast) {
		Assert.assertNotNull(broadcast.getBeginTimeMillis());
		
		Assert.assertNotNull(broadcast.getBeginTime());
		Assert.assertFalse(TextUtils.isEmpty(broadcast.getBeginTime()));
		Assert.assertTrue(broadcast.getBeginTimeCalendar().get(Calendar.YEAR) > YEAR_OF_2000);
		
		Assert.assertNotNull(broadcast.getEndTime());
		Assert.assertFalse(TextUtils.isEmpty(broadcast.getEndTime()));
		Assert.assertTrue(broadcast.getEndTimeCalendar().get(Calendar.YEAR) > YEAR_OF_2000);
		
		Assert.assertNotNull(broadcast.getBroadcastType());
		Assert.assertTrue(broadcast.getBroadcastType() != BroadcastTypeEnum.UNKNOWN);
		
		Assert.assertNotNull(broadcast.getShareUrl());
		Assert.assertFalse(TextUtils.isEmpty(broadcast.getShareUrl()));
	}
}
