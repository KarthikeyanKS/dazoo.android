package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVDates;
import com.mitv.Consts;


public class TVDatesGSONTest extends Tests {
	
	private List<TVDates> tvDates;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_DATES);
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TVDates.class, new TVDates());
		Gson gson = gsonBuilder.create();

		tvDates = Arrays.asList(gson.fromJson(jsonString, TVDates[].class));

	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvDates);
		Assert.assertFalse(tvDates.isEmpty());
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVDates tvDate : tvDates) {
			Assert.assertNotNull(tvDates);
			
			Assert.assertNotNull(tvDate.getId());
			Assert.assertFalse(TextUtils.isEmpty(tvDate.getId()));
			
			Assert.assertNotNull(tvDate.getDate());
			Assert.assertTrue(tvDate.getDate().getTime() > TIMESTAMP_OF_YEAR_2000);
			
			Assert.assertNotNull(tvDate.getDisplayName());
			Assert.assertFalse(TextUtils.isEmpty(tvDate.getDisplayName()));
			
		}
	}
	
}
