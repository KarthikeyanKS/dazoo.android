
package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.mitv.Constants;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.TVDate;

/**
 * This class tests the fetched dates. Returns a list of date objects.
 * 
 * GET /epg/dates
 * 
 * @author atsampikakis
 *
 */
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

		String url = Constants.URL_DATES;
		
	
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
			assertTrue(tvDate.areDataFieldsValid());
		}
	}
}
