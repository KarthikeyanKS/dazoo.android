
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.gson.mitvapi.TVSearchResultsJSON;
import com.mitv.models.objects.mitvapi.TVSearchResults;



public class TVSearchGSONTest 
	extends TestCore
{
	private static final String	TAG	= TVSearchGSONTest.class.getName();

	private TVSearchResults receivedData;
	
	
	private boolean executeTVSearch(String wordToSearchFor)
	{
		String url = Constants.URL_SEARCH;
		
		URLParameters urlParameters = new URLParameters();
		
		StringBuilder querystringValueSB = new StringBuilder();
		wordToSearchFor = wordToSearchFor.trim();
		querystringValueSB.append(wordToSearchFor);
		
		urlParameters.add(Constants.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, querystringValueSB.toString());
		
		HeaderParameters headerParameters = new HeaderParameters();
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
		
		String responseString = httpCoreResponse.getResponseString();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TVSearchResults.class, new TVSearchResultsJSON());
		Gson gson = gsonBuilder.create();
		
		try
		{
			receivedData = gson.fromJson(responseString, TVSearchResults.class);
			return true;
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
			return false;
		}
	}
	
	
	private void checkResult() {
		Assert.assertNotNull(receivedData);
		boolean areDataFieldsValid = receivedData.areDataFieldsValid();
		Assert.assertFalse(!areDataFieldsValid);
	}
	
	
	@Test
	public void testSearchForSeries() 
	{
		String seriesName = "los simpson";
		boolean executionWentWell = executeTVSearch(seriesName);
		assertTrue(executionWentWell);
		checkResult();
	}
	
	@Test
	public void testSearchForProgram() 
	{
		String programName = "treehouse of horror";
		boolean executionWentWell = executeTVSearch(programName);
		assertTrue(executionWentWell);
		checkResult();
	}
	
	@Test
	public void testSearchForChannel() 
	{
		String channelName = "caracol";
		boolean executionWentWell = executeTVSearch(channelName);
		assertTrue(executionWentWell);
		checkResult();
	}
}