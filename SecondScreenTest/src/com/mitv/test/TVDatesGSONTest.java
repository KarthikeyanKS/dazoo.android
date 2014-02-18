
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
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.TVDates;
import com.mitv.Consts;



public class TVDatesGSONTest 
	extends TestCore
{	
	private List<TVDates> tvDates;

	
	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		
		String url = Consts.URL_DATES;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
		
		String responseString = httpCoreResponse.getResponseString();

		tvDates = Arrays.asList(new Gson().fromJson(responseString, TVDates[].class));
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(tvDates);
		Assert.assertFalse(tvDates.isEmpty());
	}
	
	
	
	@Test
	public void testAllVariablesNotNull()
	{
		for(TVDates tvDate : tvDates)
		{
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