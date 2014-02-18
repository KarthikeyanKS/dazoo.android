package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.TVChannel;
import com.mitv.Consts;

public class TVChannelGSONTest extends Tests {

	private List<TVChannel> tvChannels;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_CHANNELS_ALL);

		tvChannels = Arrays.asList(new Gson().fromJson(jsonString, TVChannel[].class));

	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvChannels);
		Assert.assertFalse(tvChannels.isEmpty());
	}

	@Test
	public void testAllVariablesNotNull() {
		for (TVChannel tvChannel : tvChannels) {
			testTVChannelObject(tvChannel);
		}
	}

	public static void testTVChannelObject(TVChannel tvChannel) {
		Assert.assertNotNull(tvChannel);

		Assert.assertNotNull(tvChannel.getName());
		Assert.assertFalse(TextUtils.isEmpty(tvChannel.getName()));

		Assert.assertNotNull(tvChannel.getChannelId());

		Assert.assertFalse(TextUtils.isEmpty(tvChannel.getChannelId().getChannelId()));
		

		Assert.assertNotNull(tvChannel.getLogo().getSmall());
		Assert.assertFalse(TextUtils.isEmpty(tvChannel.getLogo().getSmall()));

		Assert.assertNotNull(tvChannel.getLogo().getMedium());
		Assert.assertFalse(TextUtils.isEmpty(tvChannel.getLogo().getMedium()));

		Assert.assertNotNull(tvChannel.getLogo().getLarge());
		Assert.assertFalse(TextUtils.isEmpty(tvChannel.getLogo().getLarge()));
	}
}
