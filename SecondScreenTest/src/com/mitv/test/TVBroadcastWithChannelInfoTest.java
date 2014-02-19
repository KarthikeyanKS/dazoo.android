package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.Broadcast;
import com.millicom.mitv.models.gson.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;

public class TVBroadcastWithChannelInfoTest extends TestBaseWithGuide {
	
	private List<TVBroadcastWithChannelInfo> tvProgramBroadcasts;
	private static final String	TAG	= "TVBroadcastProgramTest";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		Broadcast broadcast = someGuide.getBroadcasts().get(0);
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
	
	public static void testBroadcast(Broadcast broadcast) {
		Assert.assertNotNull(broadcast.getBeginTimeMillis());
		
		Assert.assertNotNull(broadcast.getBeginTime());
		Assert.assertTrue(broadcast.getBeginTime().getTime() > TIMESTAMP_OF_YEAR_2000);
		
		Assert.assertNotNull(broadcast.getEndTime());
		
		Assert.assertNotNull(broadcast.getBroadcastType());
		Assert.assertTrue(broadcast.getBroadcastType() != BroadcastTypeEnum.UNKNOWN);
		
		Assert.assertNotNull(broadcast.getShareUrl());
		Assert.assertFalse(TextUtils.isEmpty(broadcast.getShareUrl()));
	}
}
