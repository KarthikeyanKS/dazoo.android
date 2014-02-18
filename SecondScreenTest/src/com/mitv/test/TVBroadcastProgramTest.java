package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.models.gson.TVBroadcastDetails;
import com.millicom.mitv.models.gson.TVBroadcastPrograms;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.Consts;

public class TVBroadcastProgramTest extends Tests {
	
	private List<TVBroadcastPrograms> tvBroadcastPrograms;
	private static final String	TAG	= "TVBroadcastProgramTest";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String programId = "90958923-a34d-41d7-80fe-86f6c803f96a";
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_PROGRAMS);
		sb.append(programId);
		sb.append(Consts.API_BROADCASTS);
		
		String jsonString = super.deserializeJson(sb.toString());
		
		try
		{
			tvBroadcastPrograms = Arrays.asList(new Gson().fromJson(jsonString, TVBroadcastPrograms[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvBroadcastPrograms);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVBroadcastPrograms tvBroadcastProgram : tvBroadcastPrograms) {
			Assert.assertNotNull(tvBroadcastPrograms);
			
			Assert.assertNotNull(tvBroadcastProgram.getBeginTimeMillis());
			
			Assert.assertNotNull(tvBroadcastProgram.getBeginTime());
			Assert.assertTrue(tvBroadcastProgram.getBeginTime().getTime() > TIMESTAMP_OF_YEAR_2000);
			
			Assert.assertNotNull(tvBroadcastProgram.getEndTime());
			/* Check if endTime is after today or something? */
			
			Assert.assertNotNull(tvBroadcastProgram.getBroadcastType());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getBroadcastType()));
			
			Assert.assertNotNull(tvBroadcastProgram.getShareUrl());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getShareUrl()));
			
			
			/* channelId tests */
			Assert.assertNotNull(tvBroadcastProgram.getChannel().getName());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getChannel().getName()));
			
			Assert.assertNotNull(tvBroadcastProgram.getChannel().getChannelId());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getChannel().getChannelId().getChannelId()));
			
			Assert.assertNotNull(tvBroadcastProgram.getChannel().getLogo().getSmall());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getChannel().getLogo().getSmall()));
			
			Assert.assertNotNull(tvBroadcastProgram.getChannel().getLogo().getMedium());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getChannel().getLogo().getMedium()));
			
			Assert.assertNotNull(tvBroadcastProgram.getChannel().getLogo().getLarge());
			Assert.assertFalse(TextUtils.isEmpty(tvBroadcastProgram.getChannel().getLogo().getLarge()));
		}
	}
}
