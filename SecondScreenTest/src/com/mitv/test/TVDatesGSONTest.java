
package com.mitv.test;



import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;

public class TVDatesGSONTest 
	extends TestCore
{	
	private List<TVDate> tvDates;

	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		tvDates = testFetchTVDates();
	}

	public static List<TVDate> testFetchTVDates() {
		TVDatesGSONTest instance = new TVDatesGSONTest();

		String url = Consts.URL_DATES;
		
	
		HTTPCoreResponse httpCoreResponse = instance.executeRequestGet(url);

		String jsonString = httpCoreResponse.getResponseString();

		List<TVDate> tvDates = Arrays.asList(new Gson().fromJson(jsonString,
				TVDate[].class));

		return tvDates;
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(tvDates);
		Assert.assertFalse(tvDates.isEmpty());
	}

	@Test
	public void testAllVariablesNotNull() {
		Assert.assertNotNull(tvDates);
		for (TVDate tvDate : tvDates) {
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