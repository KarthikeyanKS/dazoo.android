
package com.mitv.test;



import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.HeaderParameters;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVSearchResults;
import com.millicom.mitv.models.gson.TVSearchResultsJSON;
import com.mitv.Consts;



public class TVSearchTest 
	extends TestCore
{
	private static final String	TAG	= TVSearchTest.class.getName();

	private TVSearchResults receivedData;
	
	
	private boolean executeTVSearch(String wordToSearchFor)
	{
		String url = Consts.URL_SEARCH;
		
		URLParameters urlParameters = new URLParameters();
		
		StringBuilder querystringValueSB = new StringBuilder();
		querystringValueSB.append(wordToSearchFor.trim());
		querystringValueSB.append(Consts.SEARCH_WILDCARD);
		
		urlParameters.add(Consts.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, querystringValueSB.toString());
		
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
	
	
//	@Test
//	public void testSearchForSeries() 
//	{
//		String seriesName = "los simpson";
//		boolean executionWentWell = executeTVSearch(seriesName);
//		assertTrue(executionWentWell);
//		checkResult();
//	}
	
//	@Test
//	public void testSearchForProgram() 
//	{
//		String programName = "treehouse of horror";
//		boolean executionWentWell = executeTVSearch(programName);
//		assertTrue(executionWentWell);
//		checkResult();
//	}
	
	@Test
	public void testSearchForChannel() 
	{
		String channelName = "caracol";
		boolean executionWentWell = executeTVSearch(channelName);
		assertTrue(executionWentWell);
		checkResult();
	}
}