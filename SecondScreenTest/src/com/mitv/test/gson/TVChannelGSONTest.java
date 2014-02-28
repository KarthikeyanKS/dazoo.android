
package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.TVChannel;
import com.mitv.Consts;


/**
 * This test class tests the fetching of all the channels. Returns a list of channel fields.
 * 
 * GET /epg/channels
 * 
 * @author atsampikakis
 *
 */
public class TVChannelGSONTest 
	extends TestCore
{
	private List<TVChannel> tvChannels;

	
	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		
		String url = Consts.URL_CHANNELS_ALL;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
		
		String responseString = httpCoreResponse.getResponseString();
		
		tvChannels = Arrays.asList(new Gson().fromJson(responseString, TVChannel[].class));
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(tvChannels);
		Assert.assertFalse(tvChannels.isEmpty());
	}

	
	
	@Test
	public void testAllVariablesNotNull() {
		for (TVChannel tvChannel : tvChannels) {
			assertTrue(tvChannel.areDataFieldsValid());
		}
	}
}
