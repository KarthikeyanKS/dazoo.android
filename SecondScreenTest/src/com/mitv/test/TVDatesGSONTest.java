package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;


public class TVDatesGSONTest extends Tests {
	
	private List<TVDate> tvDates;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_DATES);
		
		tvDates = Arrays.asList(new Gson().fromJson(jsonString, TVDate[].class));


	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvDates);
		Assert.assertFalse(tvDates.isEmpty());
	}
	
	@Test
	public void testAllVariablesNotNull() {
		Assert.assertNotNull(tvDates);
		for(TVDate tvDate : tvDates) {
			testTVDateObject(tvDate);
		}
	}
	
	public static void testTVDateObject(TVDate tvDate) {
		Assert.assertNotNull(tvDate);
		
		Assert.assertNotNull(tvDate.getId());
		Assert.assertFalse(TextUtils.isEmpty(tvDate.getId()));
		
		Assert.assertNotNull(tvDate.getDate());
		Assert.assertTrue(tvDate.getDate().getTime() > TIMESTAMP_OF_YEAR_2000);
		
		Assert.assertNotNull(tvDate.getDisplayName());
		Assert.assertFalse(TextUtils.isEmpty(tvDate.getDisplayName()));
	}
	
}
