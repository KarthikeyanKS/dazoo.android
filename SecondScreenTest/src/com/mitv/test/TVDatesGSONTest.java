package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.TVDates;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.Consts;


public class TVDatesGSONTest extends Tests {
	
	private List<TVDates> tvDates;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_DATES);
		tvDates = Arrays.asList(new Gson().fromJson(jsonString, TVDates[].class));
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
			Assert.assertTrue(tvDate.getDate().getYear()> TIMESTAMP_OF_YEAR_2000);
			
			Assert.assertNotNull(tvDate.getDisplayName());
			Assert.assertFalse(TextUtils.isEmpty(tvDate.getDisplayName()));
			
		}
	}
	
}
